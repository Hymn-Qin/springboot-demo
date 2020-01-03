package com.example.demo.security.service;

import com.example.demo.model.User;
import com.example.demo.security.model.JwtUser;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        logger.info("开始帐号验证 username: {}", s);
        User user = userService.findUserByUsername(s);
        if (user == null) {
            logger.error("帐号验证失败 username: {} 不存在", s);
        } else  {
            logger.info("帐号验证通过 username: {}", s);
        }
        return new JwtUser(user);
    }
}
