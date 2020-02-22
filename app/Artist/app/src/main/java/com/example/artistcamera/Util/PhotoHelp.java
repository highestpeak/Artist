package com.example.artistcamera.Util;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;

import com.example.artistcamera.DataLayer.vo.PhotoInfoVO;

import java.io.IOException;

public class PhotoHelp {

    public static PhotoInfoVO getPhotoInfo(Context context, Uri uri) throws IOException {
        String path=UriPhotoHelp.getRealPathFromUri(context,uri);
        PhotoInfoVO photoInfoVO=null;
        try {
            ExifInterface  exifInterface = new ExifInterface(path);
            photoInfoVO=new PhotoInfoVO(exifInterface);
        } catch (IOException e) {
            throw new IOException("get exifInterface by path error");
        }
        return photoInfoVO;
    }

}
