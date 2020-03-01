package com.example.artistcamera.DataLayer.vo;

import android.media.ExifInterface;

public class PhotoInfoVO {

    private String TYPE_TAG="";

    private String dateTime;
    private String exposureTime;//曝光时间
    private String focalLength;//焦距
    private String length;//长
    private String width;//宽
    private String orientation;//角度
    private String whiteBalance;//白平衡

    public PhotoInfoVO() {
    }

    public PhotoInfoVO(ExifInterface exifInterface) throws NullPointerException{
        if (exifInterface == null) {
            throw new NullPointerException("exifInterface is null");
        }

        dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        exposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        length = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);

    }

    @Override
    public String toString() {
        return  String.format("%-15s=%s \n", "dateTime",dateTime)+
                String.format("%-15s=%s \n", "exposureTime",exposureTime)+
                String.format("%-15s=%s \n", "focalLength",focalLength)+
                String.format("%-15s=%s \n", "length",length)+
                String.format("%-15s=%s \n", "width",width)+
                String.format("%-15s=%s \n", "orientation",orientation)+
                String.format("%-15s=%s \n", "whiteBalance",whiteBalance);
    }
}
