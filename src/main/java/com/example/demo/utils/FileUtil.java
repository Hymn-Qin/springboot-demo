package com.example.demo.utils;

import com.example.demo.model.ResourceFile;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by lixiaoxin on 2017/12/12.
 */
public class FileUtil {

    private static Properties properties = null;

    private final static List<UploadInfo> uploadInfoList = new ArrayList<>();

    //上传头像
    public static FileSaveInfo uploadImageIcon(MultipartFile file) throws IOException {
            FileSaveInfo info = null;
            if(file != null && file.getSize() > 0 ) {
                info = new FileSaveInfo();
                String fileName = CodeUtils.random(file.getOriginalFilename());
                info.setName(fileName);
                String imagePath = getImageIconPath();
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    imageFile.mkdirs();
                }
                String save_file_name = fileName;
                int temp = fileName.lastIndexOf(".");
                if (temp != -1) {
                    String prevName = fileName.substring(0, temp);
                    String fixName = fileName.substring(temp, fileName.length());
                    info.setFix(fixName.substring(1));
                    save_file_name = prevName + new Date().getTime() + fixName;
                } else {
                    save_file_name = fileName + new Date().getTime();
                }
                File targetFile = new File(imagePath, save_file_name);
                info.setSize(file.getSize());
                info.setContentType(file.getContentType());
                info.setSaveName(save_file_name);
                if(!targetFile.exists()){
                    targetFile.createNewFile();
                }
                file.transferTo(targetFile);
                //压缩成100*100的最高质量的图片
                Thumbnails.of(targetFile).size(100, 100).outputQuality(1f).toFile(targetFile);
                info.setPath(targetFile.getAbsolutePath());
                info.setRelativePath(FileUtil.relativePath(FileUtil.getSave_path(),info.getPath()));
            }
        return info;
    }

    //上传多个文件
    public static  List<FileSaveInfo>  uploadFile(String type, MultipartFile[] files, String savePath) throws IOException {
        List<FileSaveInfo> infos = new ArrayList<>();
        if(files != null && files.length > 0) {
            for(MultipartFile file :files){
                FileSaveInfo info = new FileSaveInfo();
                String fileName = CodeUtils.random(file.getOriginalFilename());
                info.setName(fileName);
                File savePaths = new File(savePath);
                if (!savePaths.exists()) {
                    savePaths.mkdirs();
                }
                String save_file_name = fileName;
                int temp = fileName.lastIndexOf(".");
                if (temp != -1) {
                    String prevName = fileName.substring(0, temp);
                    String fixName = fileName.substring(temp, fileName.length());
                    info.setFix(fixName.substring(1));
                    save_file_name = prevName + new Date().getTime() + fixName;
                } else {
                    save_file_name = fileName + new Date().getTime();
                }
                File targetFile = new File(savePath, save_file_name);
                info.setSize(file.getSize());
                info.setContentType(file.getContentType());
                info.setSaveName(save_file_name);
                if(!targetFile.exists()){
                    targetFile.createNewFile();
                }
                file.transferTo(targetFile);
                info.setPath(targetFile.getAbsolutePath());
                info.setRelativePath(FileUtil.relativePath(FileUtil.getSave_path(),info.getPath()));
                info.setType(type);
                infos.add(info);
            }
        }
        return infos;
    }

    public static String getImageIconPath(){
        return getSave_path() + File.separator  + "image_icons";
    }

    public static String getResourcePath(){
        return getSave_path() + File.separator + "resource";
    }

    public static String getTaskPath(){return  getSave_path() + File.separator + "task";}

    public static String getTaskSourcePath(){
        return getTaskPath() + File.separator + "source";
    }

    public static String getTaskPublicPath(){
        return getTaskPath() + File.separator + "public";
    }
    public static String getItemPath(){
        return getSave_path() + File.separator + "item";
    }

    public static String getItemResourcePath(){
        return getItemPath() + File.separator + "resource";
    }

    public static String getItemMaterialPath(){
        return getItemPath() + File.separator + "material";
    }

    public static String getSave_path(){
        return  getPropertiesValue("file.save.path");
    }

    public static String getDetailPath(String table,String type){
        return getSave_path() + File.separator +  table + File.separator + type;
    }

    public static synchronized String getPropertiesValue(String key) {
        if(properties != null){
            return properties.getProperty(key);
        }
        InputStream in = null;
        try {
            in = FileUtil.class.getResourceAsStream("/application.properties");
            properties = new Properties();
            properties.load(in);
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param savePath
     * @param fileFullName
     * @param file
     * @return
     * @throws Exception
     */
    public static boolean saveFile( final String savePath,
                                    final String fileFullName,
                                    final MultipartFile file)
            throws Exception {
        byte[] data = readInputStream(file.getInputStream());
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        File uploadFile = new File(savePath + fileFullName);
        //判断文件夹是否存在，不存在就创建一个
        File fileDirectory = new File(savePath);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdirs()) {
                throw new Exception("文件夹创建失败！路径为：" + savePath);
            }
        }

        //创建输出流
        try (FileOutputStream outStream = new FileOutputStream(uploadFile)) {//写入数据
            outStream.write(data);
            outStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return uploadFile.exists();
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    /**
     * 压缩文件
     *
     * @param srcFileList
     * @param zipFile
     * @throws IOException
     */
    public static void zipFiles(List<File> srcFileList, File zipFile) throws IOException {
        byte[] buf = new byte[1024];
        //ZipOutputStream类：完成文件或文件夹的压缩
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        for (File aSrcFileList : srcFileList) {
            FileInputStream in = new FileInputStream(aSrcFileList);
            out.putNextEntry(new ZipEntry(aSrcFileList.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
        out.close();
    }


    /**
     * @param md5
     * @param chunks
     * @return
     */
    public static boolean isAllUploaded( final String md5,
                                         final String chunks) {
        int size = uploadInfoList.stream()
                .filter(item -> item.getMd5().equals(md5))
                .distinct()
                .collect(Collectors.toList())
                .size();
        boolean bool = (size == Integer.parseInt(chunks));
        if (bool) {
            synchronized (uploadInfoList) {
                uploadInfoList.removeIf(item -> Objects.equals(item.getMd5(), md5));
            }
        }
        return bool;
    }

    /**
     * @param md5         MD5
     * @param guid        随机生成的文件名
     * @param chunk       文件分块序号
     * @param chunks      文件分块数
     * @param fileName    文件名
     * @param ext         文件后缀名
     * @return FileSaveInfo     文件保存信息对象 如果全部分片保存成功则返回对象，还没成功返回空
     */
    public static ResourceFile uploaded( final String md5,
                                 final String guid,
                                 final String chunk,
                                 final String chunks,
                                 final String uploadFolderPath,
                                 final String fileName,
                                 final String ext)
            throws Exception {
        synchronized (uploadInfoList) {
            uploadInfoList.add(new UploadInfo(md5, chunks, chunk, uploadFolderPath, fileName, ext));
        }
        boolean allUploaded = isAllUploaded(md5, chunks);
        int chunksNumber = Integer.parseInt(chunks);

        if (allUploaded) {
            String lastSavePath = mergeFile(chunksNumber, ext, guid, uploadFolderPath);
            File file = new File(lastSavePath);
            ResourceFile info = new ResourceFile();
            info.setSaveName(guid + ext);
            info.setMd5Value(md5);
            info.setSize(file.length());
            info.setPath(lastSavePath);
            info.setFix(ext);
            info.setRelativePath(relativePath(FileUtil.getSave_path(),lastSavePath));
            info.setName(fileName);
            return info;
        }else{
            return null;
        }
    }


    /**
     * @param chunksNumber
     * @param ext
     * @param guid
     * @param uploadFolderPath
     * @throws Exception
     */
    public static String mergeFile(final int chunksNumber,
                                  final String ext,
                                  final String guid,
                                  final String uploadFolderPath)
            throws Exception {
        /*合并输入流*/
        String mergePath = uploadFolderPath + File.separator + guid + "/";
        SequenceInputStream s ;
        InputStream s1 = new FileInputStream(mergePath + 1 + ext);
        InputStream s2 = new FileInputStream(mergePath + 2 + ext);
        s = new SequenceInputStream(s1, s2);
        for (int i = 3; i <= chunksNumber; i++) {
            InputStream s3 = new FileInputStream(mergePath + i + ext);
            s = new SequenceInputStream(s, s3);
        }
        String finalSavePath = uploadFolderPath + File.separator +  guid + ext;
        //通过输出流向文件写入数据
        saveStreamToFile(s, finalSavePath);

        //删除保存分块文件的文件夹
        deleteFolder(mergePath);
        return finalSavePath;
    }


    /**
     * 从stream中保存文件
     *
     * @param inputStream inputStream
     * @param filePath    保存路径
     * @throws Exception 异常 抛异常代表失败了
     */
    public static void saveStreamToFile( final InputStream inputStream,
                                         final String filePath)
            throws Exception {
         /*创建输出流，写入数据，合并分块*/
        OutputStream outputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    /**
     * 删除指定文件夹
     * @param folderPath 文件夹路径
     * @return 是否删除成功
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean deleteFolder( final String folderPath) {
        File dir = new File(folderPath);
        File[] files = dir.listFiles();
        if(files!=null){
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dir.delete();
    }

    /**
     * 打包文件
     * @param savePath 保存路径
     * @param sourcePath 源文件数组(需被打包的文件)
     * **/
    public static void toZip(String savePath,String[] sourcePath) throws Exception {
        byte[] buffer = new byte[1024];
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                savePath));
        File[] file1 = new File[sourcePath.length];
        for(int x = 0;x<sourcePath.length;x++){
            file1[x] = new File(sourcePath[x]);
        }
        for (int i = 0; i < file1.length; i++) {
            FileInputStream fis = new FileInputStream(file1[i]);
            out.putNextEntry(new ZipEntry(file1[i].getName()));
            int len;
            // 读入需要下载的文件的内容，打包到zip文件
            while ((len = fis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.closeEntry();
            fis.close();
        }
        out.close();
    }

    public static String relativePath(String base,String path){
        return new File(base).toURI().relativize(new File(path).toURI()).getPath();
    }
}
