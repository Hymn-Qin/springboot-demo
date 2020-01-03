//package com.example.demo.service.login;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//@Service("authenticationProviderImpl")
//public class AuthenticationProviderImpl implements AuthenticationProvider {
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String userName = (String) authentication.getPrincipal();
//        String password = (String) authentication.getCredentials();
//        logger.info("验证登录: {}", userName);
////        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
//        String encodePwd = password;//md5PasswordEncoder.encodePassword(password, userName);
//
//        UserDetails userInfo = userDetailsService.loadUserByUsername(userName);
//
//        if (!userInfo.getPassword().equals(encodePwd)) {
//            logger.error("验证登录失败: {},{}", userName, password);
//            throw new BadCredentialsException("用户名或密码不正确！");
//        }
//
//        return new UsernamePasswordAuthenticationToken(userName, password, userInfo.getAuthorities());
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return true;
//    }
//}