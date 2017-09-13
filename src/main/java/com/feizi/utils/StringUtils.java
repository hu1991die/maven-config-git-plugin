package com.feizi.utils;

/**
 * Created by feizi Ruan on 2017/9/13.
 * 字符串工具类
 */
public final class StringUtils {
    public static boolean isBlank(String str){
        if(null != str && str.trim().length() > 0){
            return false;
        }
        return true;
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }
}
