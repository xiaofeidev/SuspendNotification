package com.github.xiaofei_dev.suspensionnotification.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/3/19.
 */

public class RegexText {

    public static List<String> regexText(CharSequence charSequence){
        if(charSequence.length() == 0){
            return null;
        }

        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("\\d{2}\\d+").matcher(charSequence);
        while(m.find()){
            list.add(m.group());
        }
        return list;
    }
}
