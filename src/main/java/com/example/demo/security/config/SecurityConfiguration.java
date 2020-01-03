package com.example.demo.security.config;

import com.example.demo.model.Role;
import com.example.demo.security.filter.JWTAuthenticationFilter;
import com.example.demo.security.filter.JWTAuthorizationFilter;
import com.example.demo.security.handler.JWTAccessDeniedHandler;
import com.example.demo.security.handler.JWTAuthenticationEntryPoint;
import com.example.demo.security.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailServiceImpl userDetailsServiceImpl;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //OAuth2
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and()
                // 禁用csrf防御机制(跨域请求伪造)，这么做在测试和开发会比较方便。
                .csrf()
                .disable()

                //定义登录api post 表单form
                .formLogin()
//                .loginPage()
                .loginProcessingUrl("/api/user/login")
                //登录成功
//                .successHandler(new AuthSuccessHandler())
                //登录失败
//                .failureHandler(new AuthFailureHandler())

                .and()

//                //网页 登录页面的记住我勾选框
//                .rememberMe()
//                //配置token仓库
//                .tokenRepository(persistentTokenRepository())
//                .tokenValiditySeconds(3600)
//                .userDetailsService(userDetailsServiceImpl)
//
//                .and()

                .authorizeRequests()
                // 指定路径下的资源需要验证了的用户才能访问
//                .antMatchers("/web/**").hasAnyRole("ADMIN", "PM")
                //跨域的post的请求会验证两次，get不会。网上的解释是，post请求第一次是预检请求，Request Method： OPTIONS。
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/register", "error").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole(Role.ADMIN, Role.DEV, Role.PM)
                // 所有用户均可访问的资源
                .antMatchers("/ws/**","/druid/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "**/favicon.ico", "/index").permitAll()
//                .antMatchers("/api/**").authenticated()
                // 其他任何请求,登录后可以访问
                .anyRequest()
                .authenticated()

                .and()
                //添加自定义Filter
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))

                // 不需要session（不创建会话）
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .exceptionHandling()
                //未登录或token过期
                .authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                //没有权限
                .accessDeniedHandler(new JWTAccessDeniedHandler());
        // 防止H2 web 页面的Frame 被拦截
        http.headers().frameOptions().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/druid/**")
        .antMatchers("/ws/**");
        super.configure(web);
    }
}
