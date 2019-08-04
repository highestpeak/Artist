package com.example.artistcamera.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NameGenerator {
    public static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd_HHmmss");
    public static String getShotName(){
        return "IMG_"+dateFormat.format(new Date());
    }
}
