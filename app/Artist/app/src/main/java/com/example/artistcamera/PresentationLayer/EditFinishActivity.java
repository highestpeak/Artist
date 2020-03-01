package com.example.artistcamera.PresentationLayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.artistcamera.PresentationLayer.ViewLib.CameraPreview;
import com.example.artistcamera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditFinishActivity extends Activity {
    private static final String TAG = "EditFinishActivity";
    private static final String ALBUM_NAME = "Artist";

    @BindView(R.id.finish_imageview)
    ImageView finishImageview;
    @BindView(R.id.edit_finish_save)
    ImageView editFinishSave;
    @BindView(R.id.edit_finish_back_home)
    ImageView editFinishBackHome;
    @BindView(R.id.edit_finish_back_previous)
    ImageView editFinishBackPrevious;

    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_finish);
        ButterKnife.bind(this);

        // get and set bitmap
        bytes = getIntent().getByteArrayExtra("finishImageBytes");
        Bitmap finishImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        finishImageview.setImageBitmap(finishImage);
    }

    @OnClick({R.id.edit_finish_save, R.id.edit_finish_back_home, R.id.edit_finish_back_previous})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_finish_save:
                saveImageToGallery(getApplicationContext(),bytes);
            case R.id.edit_finish_back_home:
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_finish_back_previous:
                finish();
                break;
        }
    }

    //保存文件到指定路径
    private static boolean saveImageToGallery(Context context, byte[] data) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +ALBUM_NAME ;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            if (!appDir.mkdir()) {
                Log.d(TAG, "failed to create directory");
                return false;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = System.currentTimeMillis() + ".jpg";
        File mediaFile = new File(storePath + File.separator +
                "IMG_" + timeStamp + ".jpg");
        String outputMediaFileType = "image/*";
        try {
            FileOutputStream fos = new FileOutputStream(mediaFile);
            //通过io流的方式来压缩保存图片
            fos.write(data);
            fos.close();
//            //把文件插入到系统图库
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), mediaFile.getAbsolutePath(), fileName, null);
            Uri outputMediaFileUri = Uri.fromFile(mediaFile);
//            //保存图片后发送广播通知更新数据库
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outputMediaFileUri));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
