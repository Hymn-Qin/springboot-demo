package com.example.demo.controller;

import com.example.demo.data.model.MultipartFileParam;
import com.example.demo.exception.FileNotFoundException;
import com.example.demo.security.model.CurrentUser;
import com.example.demo.service.IOService;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class IOController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IOService service;

    /**
     * 当前用户
     */
    @Autowired
    private CurrentUser currentUser;

    /**
     * 单文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Object upload(@RequestParam("filename") MultipartFile file) {
        // 判断是否为空文件
        if (file.isEmpty()) {
            throw new FileNotFoundException("上传文件不能为空");
        }
        logger.info("上传 upload: {}", file.getName());
        service.saveUpload(file);
        return "";
    }

    /**
     * 多文件上传
     * @param files
     * @return
     */
    @PostMapping("/uploads")
    public Object uploads(@RequestParam("filename") MultipartFile[] files) {
        // 判断是否为空文件
        if (files.length < 1) {
            throw new FileNotFoundException("上传文件不能为空");
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            logger.info("上传 uploads: {}", file.getName());
            service.saveUpload(file);
        }
        return "";
    }

    /**
     * 大文件分片上传
     *
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/fileupload")
    public Object fileupload(HttpServletRequest request, MultipartFileParam param) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            logger.info("上传 fileupload: {}", param.getName());
            service.saveFileupload(param, currentUser.getCurrentUser().getId());
        } else {
            throw new FileNotFoundException("上传文件失败");
        }
        return "上传成功";
    }

    /**
     * 秒传和断点判断
     *
     * @param md5value
     * @return
     */
    @PostMapping("/checkFileMD5")
    public Object checkFileMD5(String md5value) {
        return service.checkFile(md5value);
    }

    /**
     * 下载 断点下载
     * @param request
     * @param response
     * @param filename
     * @param range
     * @return
     */
    @GetMapping("/download/{filename:.*}")
    public Object download(HttpServletRequest request, HttpServletResponse response, @PathVariable("filename") String filename, @RequestHeader(required = false) String range) {
        logger.info("下载 download: {}", filename);
        if (!range.isEmpty() && range.contains("bytes=") && range.contains("-")) {
            logger.info("断点续传 download: {}", filename);
            service.download(request, response, range, filename);
        } else {
            service.download(response, filename);
        }

        return "";
    }

    /**
     * 下载
     * @param filename
     * @return
     */
    @GetMapping("/downloads/{filename:.*}")
    public Object downloadResource(@PathVariable("filename") String filename) {
        logger.info("下载 downloads: {}", filename);
        return service.download(filename);
    }
}
