package com.example.artistcamera.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.artistcamera.DataLayer.Bean.ArtistPhotoExtend;
import com.example.artistcamera.DataLayer.PoemGetHelp;
import com.example.artistcamera.DataLayer.ScoreGetHelp;
import com.example.artistcamera.R;

import org.litepal.LitePal;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.dinus.com.loadingdrawable.render.LoadingDrawable;
import app.dinus.com.loadingdrawable.render.shapechange.CoolWaitLoadingRenderer;

public class DialogShowHelp {
    private static LoadingDrawable drawable;
    private static ImageView dialogLoading;
    private static TextView dialogText;
    private static android.os.Handler handler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            //这里接受并处理消息
            switch (msg.what){
                case 0:
                    if (drawable!=null){
                        if(drawable.isRunning()){
                            drawable.stop();
                            dialogLoading.clearAnimation();
                            dialogLoading.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case 1:
                    if (dialogText!=null){
                        dialogText.setVisibility(View.VISIBLE);
                        if(msg.obj!=null){
                            dialogText.setText((String)msg.obj);
                        }
                    }
                    break;
            }
        }
    };

    private static AlertDialog dialog=null;

    private static void poemGet(Context context){
        String title="POEM";
        //加载布局并初始化组件
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout,null);
        dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogLoading = (ImageView) dialogView.findViewById(R.id.dialog_loading);
        Button dialogBtnConfirm = (Button) dialogView.findViewById(R.id.dialog_btn_confirm);
        Button dialogBtnCancel = (Button) dialogView.findViewById(R.id.dialog_btn_cancel);

        final AlertDialog.Builder layoutDialog = new AlertDialog.Builder(context);
        layoutDialog.setView(dialogView);

        //设置组件
//        dialogText.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        dialogText.setVisibility(View.INVISIBLE);
        //loading
        CoolWaitLoadingRenderer.Builder builder = new CoolWaitLoadingRenderer.Builder(context);
        drawable = new LoadingDrawable(builder.build());
        dialogLoading.setImageDrawable(drawable);
        //you need start animation
        drawable.start();
        //---

        dialog = layoutDialog.create();
//        final Window window = dialog.getWindow();
//        Objects.requireNonNull(window).setBackgroundDrawable(new ColorDrawable(0));
        dialogBtnConfirm .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(drawable.isRunning()){
                    drawable.stop();
                }
            }
        });
        dialogBtnCancel .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(drawable.isRunning()){
                    drawable.stop();
                }
            }
        });
    }

    public static void poemGet(Context context, Bitmap bitmap){
        poemGet(context);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                PoemGetHelp poemGetHelp=new PoemGetHelp();
                String poem=poemGetHelp.poemReturnShortest(context,bitmap);
                Message msg=new Message();
                msg.what=0;
                handler.sendMessage(msg);
                msg=new Message();
                msg.what=1;
                msg.obj=poem;
                handler.sendMessage(msg);
            }
        });
        dialog.show();
        thread.start();
    }

    public static void poemGet(Context context, Uri uri) {
        poemGet(context);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                PoemGetHelp poemGetHelp=new PoemGetHelp();
                String poem=poemGetHelp.poemReturnShortest(context,uri);
                Message msg=new Message();
                msg.what=0;
                handler.sendMessage(msg);
                msg=new Message();
                msg.what=1;
                msg.obj=poem;
                handler.sendMessage(msg);
                try {
                    List<ArtistPhotoExtend> artistPhotoExtends = LitePal.where("uri = ? ",uri.toString()).find(ArtistPhotoExtend.class);
                    if(artistPhotoExtends.size()==0){
                        ArtistPhotoExtend artistPhotoExtend =new ArtistPhotoExtend();
                        String uriToIn=uri.toString();
                        artistPhotoExtend.setUri(uriToIn);
                        artistPhotoExtend.setPoem(poem);
                        artistPhotoExtend.save();
                    }else {
                        ArtistPhotoExtend artistPhotoExtend = artistPhotoExtends.get(0);
                        artistPhotoExtend.setPoem(poem);
                        artistPhotoExtend.updateAll(" uri = ? ", uri.toString());
                    }
                }catch (Exception e){
                    Log.d("POEM SAVE","save error");
                }
            }
        });
        dialog.show();
        thread.start();
    }


    private static void newScoreGet(Context context){
        String title="newScore";
        //加载布局并初始化组件
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout,null);
        dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogLoading = (ImageView) dialogView.findViewById(R.id.dialog_loading);
        Button dialogBtnConfirm = (Button) dialogView.findViewById(R.id.dialog_btn_confirm);
        Button dialogBtnCancel = (Button) dialogView.findViewById(R.id.dialog_btn_cancel);

        final AlertDialog.Builder layoutDialog = new AlertDialog.Builder(context);
        layoutDialog.setView(dialogView);

        //设置组件
//        dialogText.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        dialogText.setVisibility(View.INVISIBLE);
        //loading
        CoolWaitLoadingRenderer.Builder builder = new CoolWaitLoadingRenderer.Builder(context);
        drawable = new LoadingDrawable(builder.build());
        dialogLoading.setImageDrawable(drawable);
        //you need start animation
        drawable.start();
        //---

        dialog = layoutDialog.create();
        final Window window = dialog.getWindow();
        Objects.requireNonNull(window).setBackgroundDrawable(new ColorDrawable(0));
        dialogBtnConfirm .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(drawable.isRunning()){
                    drawable.stop();
                }
            }
        });
        dialogBtnCancel .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(drawable.isRunning()){
                    drawable.stop();
                }
            }
        });
    }

    public static void newScoreGet(Context context, Bitmap bitmap){
        newScoreGet(context);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                ScoreGetHelp scoreGetHelp=new ScoreGetHelp();
                Map<String,Object> scoreReturn =scoreGetHelp.scoreReturn(bitmap,0);
                //TODO 处理单一图片返回的数据
                Message msg=new Message();
                msg.what=0;
                handler.sendMessage(msg);
                msg=new Message();
                msg.what=1;
//                msg.obj=poem;
                handler.sendMessage(msg);
            }
        });
        dialog.show();
        thread.start();
    }

    public static void newScoreGet(Context context, Uri uri){
        newScoreGet(context);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                ScoreGetHelp scoreGetHelp=new ScoreGetHelp();
                Map<String,Object> scoreReturn =scoreGetHelp.scoreReturn(UriPhotoHelp.uriToBitmap(context,uri),0);
                //TODO 处理单一图片返回的数据
                Message msg=new Message();
                msg.what=0;
                handler.sendMessage(msg);
                msg=new Message();
                msg.what=1;
                msg.obj=scoreReturn.get("score");
                handler.sendMessage(msg);
                try {
                    ArtistPhotoExtend artistPhotoExtend = LitePal.where("uri = ? ",uri.toString()).find(ArtistPhotoExtend.class).get(0);
                    artistPhotoExtend.setScore((String) scoreReturn.get("score"));
                    artistPhotoExtend.updateAll(" uri = ? ", uri.toString());
                }catch (Exception e){
                    Log.d("POEM SAVE","save error");
                }
            }
        });
        dialog.show();
        thread.start();

    }


    public static void showPhotoInfo(Context context,String infoStr){
        String title="ArtistPhotoExtend";
        //加载布局并初始化组件
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout,null);
        dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogLoading = (ImageView) dialogView.findViewById(R.id.dialog_loading);
        Button dialogBtnConfirm = (Button) dialogView.findViewById(R.id.dialog_btn_confirm);
        Button dialogBtnCancel = (Button) dialogView.findViewById(R.id.dialog_btn_cancel);

        final AlertDialog.Builder layoutDialog = new AlertDialog.Builder(context);
        layoutDialog.setView(dialogView);

        //设置组件
//        dialogText.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        dialogText.setVisibility(View.VISIBLE);
        dialogText.setText(infoStr);
        dialogLoading.setVisibility(View.INVISIBLE);
        //loading
        CoolWaitLoadingRenderer.Builder builder = new CoolWaitLoadingRenderer.Builder(context);
        drawable = new LoadingDrawable(builder.build());
        dialogLoading.setImageDrawable(drawable);
        //you need start animation
        drawable.start();
        //---

        dialog = layoutDialog.create();
        final Window window = dialog.getWindow();
        Objects.requireNonNull(window).setBackgroundDrawable(new ColorDrawable(0));
        dialogBtnConfirm .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(drawable.isRunning()){
                    drawable.stop();
                }
            }
        });
        dialogBtnCancel .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(drawable.isRunning()){
                    drawable.stop();
                }
            }
        });
        dialog.show();
    }
}
