package com.example.demo.service;

import com.example.demo.data.model.MultipartFileParam;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IOService {

    public void saveUpload(MultipartFile file);

    public void saveFileupload(MultipartFileParam param, Integer userId);

    public Object checkFile(String md5);

    public void download(HttpServletResponse response, String filename);

    public void download(HttpServletRequest request, HttpServletResponse response, String range, String filename);

    public ResponseEntity<Resource> download(String filename);
}
