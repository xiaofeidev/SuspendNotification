package com.github.xiaofei_dev.suspensionnotification.util;
/**
 *author Administrator
 *time 2017/3/19. 15:02
 *desc：正则分析字符串的工具类，主要方法返回分析和产生的列表
 */
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class RegexText {
    public static List<String> regexText(CharSequence charSequence){
        if(charSequence.length() == 0){
            return null;
        }

        List<String> list = new ArrayList<>();
        //\\d{2}\\d+|
        Matcher m = Pattern.compile("[\\d\\w\\.:/_-]{2}[\\d\\w\\.:/_-]+").matcher(charSequence);
        while(m.find()){
            list.add(m.group());
        }
        return list;
    }
}
