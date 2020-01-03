package com.example.demo.common.config;

import com.example.demo.common.interceptor.LoggerRequestInterceptor;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.MultipartConfigElement;
import java.util.Collections;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    //MapStruct 接口聚合
//    @Bean
//    public FilterRegistrationBean<Filter> addFilter() {
//        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
//
//        filterRegistrationBean.setFilter(new TimeFilter());
//        List<String> urlList = new ArrayList<>();
//        urlList.add("/*");
//
//        filterRegistrationBean.setUrlPatterns(urlList);
//        return filterRegistrationBean;
//    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //拦截器1. 定义过滤拦截的url名称，拦截所有请求url
        registry.addInterceptor(new LoggerRequestInterceptor())
                .addPathPatterns("/**");
        //拦截器2. 。。。
        super.addInterceptors(registry);
    }

    @Autowired
    private MessageSource messageSource;

    @Bean
    public Validator validator() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        //开启快速失败
        validator.setValidationPropertyMap(
                Collections.singletonMap(
                        HibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString()
                )
        );
        return validator;
    }

    @Override
    protected Validator getValidator() {
        return validator();
    }

    /**
     * file upload
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String dir = System.getProperty("user.dir");
        factory.setLocation(dir);
        //factory.setMaxFileSize(1024);
        //单个文件最大
        factory.setMaxFileSize(DataSize.parse("10240KB")); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.parse("102400KB"));
        return factory.createMultipartConfig();
    }

    /**
     * websocket
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
