package com.example.demo.utils;

import com.sun.xml.bind.v2.TODO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 国际化输出，待验证
 *
 */
public class Messages {
    private static Logger logger = LoggerFactory.getLogger("MessageSource");

    @Autowired
    private MessageSource messageSource;

    /**
     * 国际化
     *
     * @param result
     * @return
     */
    public String getMessage(String result, Object[] params) {
        //TODO
        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(result, params, locale);
        } catch (Exception e) {
            logger.error("parse message error! ", e);
        }
        return message;
    }

    /**
     * 国际化
     * 注：通过@Autowired private MessageSource messageSource无法获取
     *
     * @param result
     * @return
     */
    public static String getMessages(String result, Object[] params) {
        //TODO
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setCacheSeconds(-1);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setBasenames("/message/messages");

        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(result, params, locale);
        } catch (Exception e) {
            logger.error("parse message error! ", e);
        }
        return message;
    }
}
