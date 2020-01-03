//package com.example.demo.security.config;
//
//import com.example.demo.security.service.UserDetailServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//
////OAuth2
//@Configuration
//@EnableAuthorizationServer
//public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
//
//    @Autowired
//    private UserDetailServiceImpl userDetailService;
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    TokenStore tokenStore;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
//        endpoints.authenticationManager(authenticationManager)
//                .tokenStore(tokenStore)
//                .userDetailsService(userDetailService);
//    }
//
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//
//                .withClient("test1")
//                .secret(passwordEncoder().encode("test1111"))
//                //token有效时间
//                .accessTokenValiditySeconds(3600)
//                //刷新令牌的token 10天
//                .refreshTokenValiditySeconds(864000)
//                //scope只能指定为all，a，b或c中的某个值，否则将获取失败；
//                .scopes("all", "a", "b", "c")
//                //密码模式
//                .authorizedGrantTypes("password")
//
//                .and()
//
//                .withClient("test2")
//                .secret(passwordEncoder().encode("test2222"))
//                .accessTokenValiditySeconds(7200);
//
//        //header添加
//        // Authorization  Basic test1:test1111(client_id:client-secret)base64加密
//        //
//        //{"grant_type": "password",
//        // "username": "138****",
//        // "password": "123456",
//        // "scope": "all/a/b/c"}
//    }
//}
