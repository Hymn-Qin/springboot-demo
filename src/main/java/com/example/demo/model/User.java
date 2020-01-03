package com.example.demo.model;

import com.example.demo.model.base.AbstractEntity;
import com.example.demo.utils.JSONChange;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Data
@Table(name = "tb_user")
public class User extends AbstractEntity implements Serializable {
    //@NoArgsConstructor ： 生成一个无参数的构造方法
    //@AllArgsContructor： ?会生成一个包含所有变量
    //@RequiredArgsConstructor： 会生成一个包含常量，和标识了NotNull的变量的构造方法。生成的构造方法是私有的private。
    //主要使用前两个注解，这样就不需要自己写构造方法，代码简洁规范

    private static final long serialVersionUID = -8995126123471018134L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键生成策略
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", unique = true, nullable = false, length = 64)
    private String username;
    @Column(name = "display_name", length = 64)
    private String displayName;
    @Column(name = "name", length = 64)
    private String name;
    @Column(name = "email", length = 64)
    private String email;

    //@JsonIgnore注解用来忽略某些字段，可以用在变量或者Getter方法上，用在Setter方法时，和变量效果一样。这个注解一般用在我们要忽略的字段上。
    //
    //@JsonIgnoreProperties(ignoreUnknown = true)，将这个注解写在类上之后，就会忽略类中不存在的字段。这个注解还可以指定要忽略的字段，
    // 例如@JsonIgnoreProperties({ “password”, “secretKey” })
    //
    //@JsonFormat可以帮我们完成格式转换。例如对于Date类型字段，如果不适用JsonFormat默认在rest返回的是long，
    // 如果我们使用@JsonFormat(timezone = “GMT+8”, pattern = “yyyy-MM-dd HH:mm:ss”)，就返回"2018-11-16 22:58:15"

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    //一对多
    @JsonIgnore
    @OneToMany(targetEntity = Car.class, mappedBy = "user", cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_car_id", referencedColumnName = "id")
    private Set<Car> cars = new HashSet<>(0);

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public User() {

    }

    public User(String email, String username, String displayName, String name, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.displayName = displayName;
        this.password = password;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return JSONChange.objToJson(this);
    }
}
