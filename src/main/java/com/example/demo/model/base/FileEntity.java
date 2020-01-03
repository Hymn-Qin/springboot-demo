package com.example.demo.model.base;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class FileEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键生成策略
    @Column(name = "id")
    private Integer id;

    //    文件类型	type
    private String type;
    //    文件后缀	fix
    private String fix;
    //    文件名	name
    private String name;
    //    关联id	rid
    private Long rid;
    //    创建者	creator
    private Integer userId;
    //    文件大小	size
    private Long size;
    //    文件地址	path
    private String path;
    //    备注信息	remark
    private String remark;
    //    其他	other
    private String other;

    private String relativePath;

    private String contentType;

    private String saveName;
}
