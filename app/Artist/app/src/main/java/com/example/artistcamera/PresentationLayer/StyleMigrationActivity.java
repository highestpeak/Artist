package com.example.artistcamera.PresentationLayer;

//import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.artistcamera.DataLayer.Bean.PhotoInfo;
import com.example.artistcamera.DataLayer.Bean.StyleJsonBean;
import com.example.artistcamera.DataLayer.StyleSwitchHelp;
import com.example.artistcamera.R;
import com.example.artistcamera.Util.DialogShowHelp;
import com.example.artistcamera.Util.UriPhotoHelp;

import org.litepal.LitePal;

import java.io.IOException;

import app.dinus.com.loadingdrawable.render.LoadingDrawable;
import app.dinus.com.loadingdrawable.render.shapechange.CoolWaitLoadingRenderer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StyleMigrationActivity extends AppCompatActivity {
    private final int CODE_SELECT_IMAGE = 2;//相册RequestCode
    @BindView(R.id.style_backto)
    ImageView styleBackto;
    @BindView(R.id.style_close)
    ImageView styleClose;
    @BindView(R.id.style_oldphoto)
    ImageView styleOldphoto;
    @BindView(R.id.style_targetphoto)
    ImageView styleTargetphoto;
    @BindView(R.id.style_newphoto)
    ImageView styleNewphoto;
    @BindView(R.id.style_newphoto_info)
    LinearLayout styleNewphotoInfo;
    @BindView(R.id.style_photo_poem)
    ImageView stylePhotoPoem;
    @BindView(R.id.style_photo_newscore)
    Button stylePhotoNewscore;
    @BindView(R.id.style_photo_save)
    Button stylePhotoSave;

    private Uri uriOld=null;
    private Uri uriTarget=null;
    private Boolean isTargetPhotoSet=false;
    private Boolean isNewStylePhotoGet=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        String from = intent.getStringExtra("from");
        if (from.equals("onePhoto")) {
            uriOld = Uri.parse(intent.getStringExtra("photoUri"));
            Glide.with(this)
                    .load(uriOld)
                    .fitCenter()
                    .into(styleOldphoto);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
//                    selectPic(data);
                    uriTarget=data.getData();
                    Glide.with(this)
                            .load(uriTarget)
                            .fitCenter()
                            .into(styleTargetphoto);
                    isTargetPhotoSet=true;
                    styleTargetphoto.setAlpha(1f);
                }
                break;
        }
    }

    //选择照片
    private void selectPic(Intent intent) {
        Uri selectImageUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        styleTargetphoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    }

    @OnClick({R.id.style_backto, R.id.style_close, R.id.style_targetphoto, R.id.style_photo_poem, R.id.style_photo_newscore, R.id.style_photo_save,R.id.style_newphoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.style_backto:
                finish();
                break;
            case R.id.style_close:
                finish();
                break;
            case R.id.style_targetphoto:
                Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
                break;
            case R.id.style_newphoto:
                if(!isTargetPhotoSet){
                    Toast.makeText(this,"尚未选择目标风格",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(drawable!=null && drawable.isRunning()){//正在请求
                    break;
                }
                if(drawable==null){//尚未请求
                    styleMigrationHelp();
                }
                break;
            case R.id.style_photo_poem:
                processPoem();
                break;
            case R.id.style_photo_newscore:
                processNewScore();
                break;
            case R.id.style_photo_save:
                break;
        }
    }

    private LoadingDrawable drawable;
    private android.os.Handler handler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            //这里接受并处理消息
            switch (msg.what){
                case 0:
                    if (drawable!=null){
                        if(drawable.isRunning()){
                            drawable.stop();
                            styleTargetphoto.clearAnimation();
                        }
                    }
                    break;
                case 1:
                    if (styleTargetphoto!=null){
//                        dialogText.setVisibility(View.VISIBLE);
//                        dialogText.setText((String)msg.obj);
                    }
                    break;
            }
        }
    };

    private void styleMigrationHelp() {
        //loading
        CoolWaitLoadingRenderer.Builder builder = new CoolWaitLoadingRenderer.Builder(this);
        drawable = new LoadingDrawable(builder.build());
        styleNewphoto.setImageDrawable(drawable);
        //you need start animation
        drawable.start();
        //---
        //TODO 获取风格迁移的图像的处理
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                StyleSwitchHelp styleSwitchHelp=new StyleSwitchHelp();
//                StyleJsonBean bean=styleSwitchHelp.styleInfoReturn(StyleMigrationActivity.this,uriOld,uriTarget);
//
//                isNewStylePhotoGet=true;
//                //TODO 获取风格迁移的图像的处理--图片显示 保存
//                Message msg=new Message();
//                msg.what=0;
//                handler.sendMessage(msg);//暂停显示进度条
//                //
//                msg=new Message();
//                msg.what=1;
////                msg.obj="";//设置风格迁移的图像
//                handler.sendMessage(msg);//显示风格迁移的图像
//            }
//        });
//        thread.start();
    }

    private void processNewScore() {
        if(isNewStylePhotoGet){
            //TODO 一旦新的风格迁移图片保存成功  则使用新的uri调用方法
            DialogShowHelp.newScoreGet(this, ((BitmapDrawable) styleNewphoto.getBackground()).getBitmap());
        }
    }

    private void processPoem() {
        if(isNewStylePhotoGet){
            //TODO 一旦新的风格迁移图片保存成功  则使用新的uri调用方法
            DialogShowHelp.poemGet(this,((BitmapDrawable) styleNewphoto.getBackground()).getBitmap());
        }
    }
}
