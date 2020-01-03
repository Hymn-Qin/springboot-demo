package com.example.demo.model;

import com.example.demo.model.base.AbstractEntity;
import com.example.demo.utils.JSONChange;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "tb_role")
public class Role extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 6279544733789507793L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", length = 64)
    private String roleName;

    public Role() {

    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return JSONChange.objToJson(this);
    }

    public static final String USER = "USER";
    public static final String PM = "PM";
    public static final String DEV = "DEV";
    public static final String ADMIN = "ADMIN";
}
