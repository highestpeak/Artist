package com.example.artistcamera.PresentationLayer.ViewLib;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.artistcamera.DataLayer.Bean.ArtistPhotoExtend;
import com.example.artistcamera.DataLayer.vo.PhotoInfoVO;
import com.example.artistcamera.Util.DialogShowHelp;

import org.litepal.LitePal;

import java.io.IOException;

import com.example.artistcamera.Util.PhotoHelp;

public class ArtistDialogLib {
    /**
     * debug
     */
    private static final String TAG = "ArtistDialog";

    public static void photoInfoDialog(Context context, Uri uri){
        PhotoInfoVO photoInfoVO=null;
        try {
            photoInfoVO=PhotoHelp.getPhotoInfo(context,uri);
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }

        ArtistPhotoExtend artistPhotoExtend=null;
        String artistPhotoExtendStr="";
        try{
            String uriToFind=uri.toString();
            artistPhotoExtend = LitePal.where("uri = ? ",uriToFind)
                    .find(ArtistPhotoExtend.class).get(0);
            artistPhotoExtendStr=artistPhotoExtend.toString();
        }catch (Exception e){
            Log.d(TAG,"error query photo info ; msg : " +e.getMessage());
            artistPhotoExtendStr="";
        }

        String infoStr=photoInfoVO.toString()+artistPhotoExtendStr;
        DialogShowHelp.showPhotoInfo(context,infoStr);
    }

    //todo
    public static void poemDialog(){

    }

    //todo
    public static void scoreDialog(){

    }
}
