package com.example.demo.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiaoxin on 2018/1/10.
 */
public class CodeUtils {
    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        try {
            Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
            Matcher m = p.matcher(strName);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();

            int length = (ch != null) ? ch.length : 0;
            for (int i = 0; i < length; i++) {
                char c = ch[i];
                if (!Character.isLetterOrDigit(c)) {
                    String str = "" + ch[i];
                    if (!str.matches("[\u4e00-\u9fa5]+")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public static String random(String fileName){
        if(fileName.indexOf("?") == 0) {
            fileName = fileName.replace("?", "x");
        }
        return  fileName;
    }

    public static Set<String> stringSplitToSet(String splitStr){
        Set<String> set = new HashSet<String>();
        for(String str : splitStr.split(",")){
            set.add(str);
        }
        return set;
    }

    public static List<String> splitByBit(String str,int bit){
        int res = str.length() / bit;
        int last = bit;
        List<String> strings = new ArrayList<>();
        for(int x = 0;x < res;x++){
            strings.add(str.substring(0,last));
            last += bit;
        }
        return strings;
    }
    public static void main(String [] args){
        System.out.println(splitByBit("000100020003",4));
    }

}
