package com.example.demo.service.impl;

import com.example.demo.data.Constants;
import com.example.demo.data.model.MultipartFileParam;
import com.example.demo.exception.FileNotFoundException;
import com.example.demo.exception.*;
import com.example.demo.model.ResourceFile;
import com.example.demo.repos.ResourceFileRepository;
import com.example.demo.service.IOService;
import com.example.demo.utils.FileMD5Util;
import com.example.demo.utils.FileUtil;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Transactional(rollbackFor = SQLException.class)
@Service
public class IOServiceImpl implements IOService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String filePath = "./target/upload";

    //这个必须与前端设定的值一致
    @Value("${file.upload.chunkSize}")
    private long CHUNK_SIZE;

    @Autowired
    private ResourceFileRepository resourceFileRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 单文件上传
     *
     * @param file
     */
    @Override
    public void saveUpload(MultipartFile file) {
        // 文件类型
        String contentType = file.getContentType();

        String fileName = file.getName();
        // 原文件名即上传的文件名
        String origFileName = file.getOriginalFilename();
        String suffix = origFileName.substring(origFileName.lastIndexOf("."));
        // 保存文件名
        String filename = UUID.randomUUID() + suffix;
        // 文件大小
        Long fileSize = file.getSize();
        try {
            // 保存文件
            // 可以使用二进制流直接保存
            // 这里直接使用transferTo
            File dest = new File(filePath + File.separator + filename);

            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                if (!dest.getParentFile().mkdir()) {
                    throw new FileNotFoundException("文件夹创建失败");
                }
            }
            file.transferTo(dest);
            //String.format(file.getClass().getName() + "方式文件上传成功！\n文件名:%s,文件类型:%s,文件大小:%s", origFileName, contentType, fileSize)
            logger.info("文件上传成功 path: {}, 文件类型: {}, 文件大小: {}", dest.getPath(), contentType, fileSize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException("文件读写错误");
        }
    }

    /**
     * 分片上传 断点续传
     *
     * @param param
     * @param userId
     */
    @Override
    public void saveFileupload(MultipartFileParam param, Integer userId) {
        String filename = param.getName();
        String uploadDirPath = filePath + param.getMd5value();
        String tempFileName = filename + "_tmp";

        try {
            File tmpFile = new File(uploadDirPath + File.separator + tempFileName);
            if (!tmpFile.getParentFile().exists()) { //判断文件父目录是否存在
                if (!tmpFile.getParentFile().mkdir()) {
                    throw new FileNotFoundException("文件夹创建失败");
                }
            }

            RandomAccessFile tempRaf = new RandomAccessFile(tmpFile, "rw");
            FileChannel fileChannel = tempRaf.getChannel();

            //写入分片数据
            long offset = CHUNK_SIZE * param.getChunk();
            byte[] fileData = param.getFile().getBytes();
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
            buffer.put(fileData);
            FileMD5Util.freedMappedByteBuffer(buffer);
            fileChannel.close();

            boolean isOk = checkAndSetUploadProgress(param, uploadDirPath);

            if (isOk) {
                boolean flag = renameFile(tmpFile, filename);
                logger.info("upload complete !!" + flag + " name=" + filename);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException("文件读写错误");
        }

    }

    public Object checkFile(String md5) {
        Object processingObj = stringRedisTemplate.opsForHash().get(Constants.FILE_UPLOAD_STATUS, md5);
        if (processingObj == null) {
            //没有上传过
            return "";
        }
        String processingStr = processingObj.toString();
        boolean processing = Boolean.parseBoolean(processingStr);
        String value = stringRedisTemplate.opsForValue().get(Constants.FILE_MD5_KEY + md5);
        if (processing) {
            //已经上传过
            return "";
        } else {
            try {
                File confFile = new File(value);
                byte[] completeList = FileUtils.readFileToByteArray(confFile);
                List<String> missChunkList = new LinkedList<>();
                for (int i = 0; i < completeList.length; i++) {
                    if (completeList[i] != Byte.MAX_VALUE) {
                        missChunkList.add(i + "");
                    }
                }
                return missChunkList;
            } catch (IOException e) {
                e.printStackTrace();
                throw new FileNotFoundException("文件读写错误");
            }
        }
    }

    /**
     * 下载
     *
     * @param response
     * @param filename
     */
    @Override
    public void download(HttpServletResponse response, String filename) {
        File file = new File(filePath + File.separator + filename);
        if (!file.exists()) {
            return;
        }
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
//        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream outputStream = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                outputStream.flush();
                i = bis.read(buffer);
            }
            logger.info("文件下载成功 filename: {}", filename);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileNotFoundException("文件读写错误");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void download(HttpServletRequest request, HttpServletResponse response, String range, String filename) {
        //文件目录
        File file = new File(filePath + File.separator + filename);

        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length() - 1;

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String ranges[] = range.split("-");
            try {
                //判断range的类型
                if (ranges.length == 1) {
                    //类型一：bytes=-2343
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //类型二：bytes=2343-
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //类型三：bytes=22-2343
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
            }
        }

        //要下载的长度（为啥要加一问小学数学老师去）
        long contentLength = endByte - startByte + 1;
        //文件名
        String fileName = file.getName();
        //文件类型
        String contentType = request.getServletContext().getMimeType(fileName);


        //各种响应头设置
        //参考资料：https://www.ibm.com/developerworks/cn/java/joy-down/index.html
        //坑爹地方一：看代码
        response.setHeader("Accept-Ranges", "bytes");
        //坑爹地方二：http状态码要为206
        response.setStatus(response.SC_PARTIAL_CONTENT);
        response.setContentType(contentType);
        response.setHeader("Content-Type", contentType);
        //这里文件名换你想要的，inline表示浏览器直接实用（我方便测试用的）
        //参考资料：http://hw1287789687.iteye.com/blog/2188500
        response.setHeader("Content-Disposition", "inline;filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(contentLength));
        //坑爹地方三：Content-Range，格式为
        // [要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());


        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[4096];
            int len = 0;
            randomAccessFile.seek(startByte);
            //坑爹地方四：判断是否到了最后不足4096（buff的length）个byte这个逻辑（(transmitted + len) <= contentLength）要放前面！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            //不然会会先读取randomAccessFile，造成后面读取位置出错，找了一天才发现问题所在
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
                //停一下，方便测试，用的时候删了就行了
//                Thread.sleep(10);
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }

            outputStream.flush();
            response.flushBuffer();
            randomAccessFile.close();
            logger.info("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);

        } catch (ClientAbortException e) {
            logger.error("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
            //捕获此异常表示拥护停止下载
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 下载
     *
     * @param filename
     * @return
     */
    @Override
    public ResponseEntity<Resource> download(String filename) {
        try {
            // 获取文件名称，中文可能被URL编码
            String fileName = URLDecoder.decode(filename, "UTF-8");
            // 获取本地文件系统中的文件资源
            FileSystemResource resource = new FileSystemResource(filePath + File.separator + fileName);

            // 解析文件的 mime 类型
            String mediaTypeStr = URLConnection.getFileNameMap().getContentTypeFor(fileName);
            // 无法判断MIME类型时，作为流类型
            mediaTypeStr = (mediaTypeStr == null) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mediaTypeStr;
            // 实例化MIME
            MediaType mediaType = MediaType.parseMediaType(mediaTypeStr);

            /*
             * 构造响应的头
             */
            HttpHeaders headers = new HttpHeaders();
            // 下载之后需要在请求头中放置文件名，该文件名按照ISO_8859_1编码。
            String filenames = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            headers.setContentDispositionFormData("attachment", filenames);
            headers.setContentType(mediaType);
            logger.info("文件下载成功 filename: {}", filename);
            /*
             * 返还资源
             */
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.getInputStream().available())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException("文件读写错误");
        }
    }


    /**
     * 检查并修改文件上传进度
     *
     * @param param
     * @param uploadDirPath
     * @return
     * @throws IOException
     */
    private boolean checkAndSetUploadProgress(MultipartFileParam param, String uploadDirPath) throws IOException {
        String fileName = param.getName();
        File confFile = new File(uploadDirPath, fileName + ".conf");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");
        //把该分段标记为 true 表示完成
        logger.info("set part " + param.getChunk() + " complete");
        accessConfFile.setLength(param.getChunks());
        accessConfFile.seek(param.getChunk());
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
            System.out.println("check part " + i + " complete?:" + completeList[i]);
        }

        accessConfFile.close();
        if (isComplete == Byte.MAX_VALUE) {
            stringRedisTemplate.opsForHash().put(Constants.FILE_UPLOAD_STATUS, param.getMd5value(), "true");
            stringRedisTemplate.opsForValue().set(Constants.FILE_MD5_KEY + param.getMd5value(), uploadDirPath + "/" + fileName);
            return true;
        } else {
            if (!stringRedisTemplate.opsForHash().hasKey(Constants.FILE_UPLOAD_STATUS, param.getMd5value())) {
                stringRedisTemplate.opsForHash().put(Constants.FILE_UPLOAD_STATUS, param.getMd5value(), "false");
            }
            if (stringRedisTemplate.hasKey(Constants.FILE_MD5_KEY + param.getMd5value())) {
                stringRedisTemplate.opsForValue().set(Constants.FILE_MD5_KEY + param.getMd5value(), uploadDirPath + "/" + fileName + ".conf");
            }
            return false;
        }

    }

    /**
     * 文件重命名
     *
     * @param toBeRenamed   将要修改名字的文件
     * @param toFileNewName 新的名字
     * @return
     */
    public boolean renameFile(File toBeRenamed, String toFileNewName) {
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            logger.info("File does not exist: " + toBeRenamed.getName());
            return false;
        }
        String p = toBeRenamed.getParent();
        File newFile = new File(p + File.separatorChar + toFileNewName);
        //修改文件名
        return toBeRenamed.renameTo(newFile);
    }


    /**
     * @param guid     临时文件名
     * @param md5value 客户端生成md5值
     * @param chunks   分块数
     * @param chunk    分块序号
     * @param name     上传文件名
     * @param file     文件本身
     * @return
     */
    public ResourceFile fileUpload(String guid,
                                   String md5value,
                                   String chunks,
                                   String chunk,
                                   String name,
                                   MultipartFile file,
                                   String uploadFolderPath) {
        String fileName;
        int temp = guid.lastIndexOf(".");
        if (temp != -1) {
            guid = guid.substring(0, temp);
        }
        try {
            int index;

            //合并文件保存的路径
            String mergePath = uploadFolderPath + File.separator + guid + "/";
            String ext = "";
            if (name.lastIndexOf(".") != -1) {
                ext = name.substring(name.lastIndexOf("."));
            }
            //判断文件是否分块
            if (chunks != null && chunk != null && !chunks.equals("1")) {
                index = Integer.parseInt(chunk);
                fileName = String.valueOf(index) + ext;
                // 将文件分块保存到临时文件夹里，便于之后的合并文件
                FileUtil.saveFile(mergePath, fileName, file);
                // 验证所有分块是否上传成功，成功的话进行合并
                ResourceFile info = FileUtil.uploaded(md5value, guid, chunk, chunks, uploadFolderPath, fileName, ext);
                //合并成功的话
                return info;
            } else {
                fileName = guid + ext;
                //上传文件没有分块的话就直接保存
                FileUtil.saveFile(uploadFolderPath + File.separator, fileName, file);
                ResourceFile info = new ResourceFile();
                info.setName(fileName);
                info.setFix(ext);
                info.setSize(file.getSize());
                info.setContentType(file.getContentType());
                info.setSaveName(fileName);
                info.setPath(uploadFolderPath + File.separator + fileName);
                info.setRelativePath(FileUtil.relativePath(FileUtil.getSave_path(), info.getPath()));
                info.setMd5Value(md5value);
                return info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FileNotFoundException("文件读写错误");
        }
    }
}
