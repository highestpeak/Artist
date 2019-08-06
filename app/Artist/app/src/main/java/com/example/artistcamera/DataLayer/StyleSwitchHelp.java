package com.example.artistcamera.DataLayer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.artistcamera.DataLayer.Bean.PoemBean;
import com.example.artistcamera.DataLayer.Bean.StyleJsonBean;
import com.example.artistcamera.Util.UriPhotoHelp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.artistcamera.Util.WebHelp.generateRequestBody;
import static com.example.artistcamera.Util.WebHelp.imageToBase64;

public class StyleSwitchHelp {
    private static final String BASE_URL ="http://106.14.33.234:80/";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private WebService webService = retrofit.create(WebService.class);

    public StyleSwitchHelp() {
    }

    public  StyleJsonBean styleInfoReturn(Context context,
                                               Uri uriOld,
                                               Uri uriNew){
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),"this_is_username");
        String uriOldBody = imageToBase64(UriPhotoHelp.getRealPathFromUri(context,uriOld));
        String uriNewBody = imageToBase64(UriPhotoHelp.getRealPathFromUri(context,uriNew));
        Map<String, String> requestDataMap=new HashMap<>();
        requestDataMap.put("photo",uriOldBody);
        requestDataMap.put("style",uriNewBody);
        Call<StyleJsonBean> call= webService.styleMigration(generateRequestBody(requestDataMap));
        //TODO: 风格迁移数据传输
        StyleJsonBean bean=null;
        try {
            bean = call.execute().body();
        } catch (IOException e) {
            Log.d("PoemGetHelp ","execute error");
        }
        if (bean==null){
            return null;
        }
        return bean;
    }
}
