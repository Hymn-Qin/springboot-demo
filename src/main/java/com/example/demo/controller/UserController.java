package com.example.demo.controller;

import com.example.demo.common.validation.Sequential;
import com.example.demo.data.model.RegisterUser;
import com.example.demo.exception.UsernameNotFoundException;
import com.example.demo.model.User;
import com.example.demo.security.model.CurrentUser;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/user")
public class UserController {

    //@RequestMapping变种（@GetMapping,@PostMapping,@PutMapping,@DeleteMapping,@PatchMapping）
    /**
     * 职责：注册接口，参数校验  具体的业务逻辑在service的impl中
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 用户模块服务
     */
    @Autowired
    private UserService service;

    /**
     * 当前用户
     */
    @Autowired
    private CurrentUser currentUser;

    /**
     * 注册
     * @param registerUser 注册参数 username password
     * @return
     */
    @PostMapping("/register")
    public Object register(@RequestBody @Validated(Sequential.class) RegisterUser registerUser) {
        logger.info("注册register:{}", registerUser);

        User user = service.saveUser(registerUser);
        return user;
    }

    /**
     * 查询
     * @param request
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public Object findUserById(HttpServletRequest request, @PathVariable Integer id) {
        logger.info("查询findUserById:{}", id);
        //要查询的id是否是当前用户Id
        if (!id.equals(currentUser.getCurrentUser().getId())) {
            throw new UsernameNotFoundException("参数错误");
        }

        User user = service.findByUserId(id);
        return user;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('DEV','PM','ADMIN')")//可以加在类上
    public Object deleteUserByUsername(@PathVariable Integer id) {
        service.deleteUserByUserId(id);
        return "null";
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DEV','PM','ADMIN')")
    public Object updateUserById(@PathVariable Integer id) {
        return "null";
    }
}
