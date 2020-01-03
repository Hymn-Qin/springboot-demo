package com.example.demo.model;

import com.example.demo.model.base.FileEntity;
import com.example.demo.utils.JSONChange;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "tb_resource_file")
public class ResourceFile extends FileEntity implements Serializable {
    private static final long serialVersionUID = 1236231541298143621L;
    private String md5Value;

    public ResourceFile() {

    }

    public ResourceFile(String name, String saveName, String type, String fix, String path, String relativePath, String contentType, Long size, String md5Value) {
        this.setName(name);
        this.setSaveName(saveName);
        this.setType(type);
        this.setFix(fix);
        this.setPath(path);
        this.setContentType(contentType);
        this.setSize(size);
        this.setRelativePath(relativePath);
        this.md5Value = md5Value;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return JSONChange.objToJson(this);
    }
}
