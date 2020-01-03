package com.example.demo.data.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MultipartFileParam {

    /**
     * @param guid             临时文件名
     * @param md5value         客户端生成md5值
     * @param chunks           分块数
     * @param chunk            分块序号
     * @param name             上传文件名
     * @param file             文件本身
     * @return
     */

    @NotNull(message = "{}")
    @NotBlank(message = "{}")
    private String title;

    @NotNull(message = "{}")
    @NotBlank(message = "{}")
    private String resourceType;

    private String brief;

    private String guid;

    @NotNull(message = "{}")
    @NotBlank(message = "{}")
    private String md5value;

    private int chunks;

    private int chunk;

    private String name;

    private MultipartFile file;

    private String iconMd5value;
}
