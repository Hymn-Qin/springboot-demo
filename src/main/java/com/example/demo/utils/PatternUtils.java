package com.example.demo.utils;

public class PatternUtils {
    public static final String MOBILE_NUMBER_PATTERN = "^[1][3,4,5,6,7,8,9][0-9]{9}$";
    public static final String NUMBER_PATTERN = "^[0-9]+$";

    public static final String CHINESE_PATTERN = "^[\u4e00-\u9fa5]+$";
    public static final String LETTER_PATTERN = "^[a-zA-Z]+$";

    public static final String CHINESE_LETTER_PATTERN = "^[a-zA-Z\u4e00-\u9fa5]+$";
    public static final String LETTER_NUMBER_PATTERN = "^[a-zA-Z0-9\u4e00-\u9fa5]+$";
}
