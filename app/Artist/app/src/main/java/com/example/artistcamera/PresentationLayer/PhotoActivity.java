package com.example.artistcamera.PresentationLayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.artistcamera.DataLayer.Bean.PhotoInfo;
import com.example.artistcamera.DataLayer.PoemGetHelp;
import com.example.artistcamera.R;
import com.example.artistcamera.Util.DialogShowHelp;
import com.example.artistcamera.Util.UriPhotoHelp;
import com.example.artistcamera.Util.WebHelp;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoActivity extends AppCompatActivity {
    /**
     * debug
     */
    private static final String TAG = "PhotoActivity";
    @BindView(R.id.onephoto_photo_info)
    ImageView onephotoPhotoInfo;
    @BindView(R.id.onephoto_photo_new_style)
    ImageView onephotoPhotoNewStyle;
    @BindView(R.id.onephoto_photo_poem)
    ImageView onephotoPhotoPoem;
    @BindView(R.id.onephoto_photo_newscore)
    Button onephotoPhotoNewscore;

    private ViewPager mPager;
    private int currPosition=-1;
    private ArrayList<Uri> uriArrayList;
    private PhotoInfo photoInfo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        uriArrayList = getNewestPhoto();

        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return uriArrayList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(getApplicationContext());
                currPosition=position;
                photoInfo=null;
                Glide.with(PhotoActivity.this)
                        .load(uriArrayList.get(position))
                        .fitCenter()
                        .into(imageView);
//                view.setImageURI(uriArrayList.get(position));
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                Glide.clear((View) object);
            }
        });

    }

    /**
     * 获取最新图片
     *
     * @return String
     */
    public ArrayList<Uri> getNewestPhoto() {

        ArrayList<String> img_path = new ArrayList<>();
        ArrayList<Uri> img_uri = new ArrayList<>();
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 获取SDcard卡的路径
        String sdcardPath = Environment.getExternalStorageDirectory().toString();

        ContentResolver mContentResolver = PhotoActivity.this.getContentResolver();
        Cursor mCursor = mContentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_ADDED + " DESC"); // 按图片ID降序排列

        while (mCursor.moveToNext()) {
            // 打印LOG查看照片ID的值
            long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
//            Log.i("MediaStore.Images.Media_ID=", id + "");

            // 过滤掉不需要的图片，只获取拍照后存储照片的相册里的图片
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            img_path.add("file://" + path);
//            Uri uri = ContentUris.withAppendedId(mediaUri,
//                    mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Uri uri=Uri.fromFile(new File(path));
            img_uri.add(uri);
            if (img_path.size() == 100) {
                break;
            }

        }
        mCursor.close();
        return img_uri;
    }

    @OnClick({R.id.onephoto_backtogallery, R.id.onephoto_gallery, R.id.onephoto_close, R.id.onephoto_photo_info, R.id.onephoto_photo_new_style, R.id.onephoto_photo_poem, R.id.onephoto_photo_newscore})
    public void onViewClicked(View view) {
        Intent intent=null;
        switch (view.getId()) {
            case R.id.onephoto_backtogallery:
//                intent = new Intent(this, StyleMigrationActivity.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.onephoto_gallery:
//                intent = new Intent(this, StyleMigrationActivity.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.onephoto_close:
                finish();
                break;
            case R.id.onephoto_photo_info:
                try{
                    String uriToFind=uriArrayList.get(currPosition).toString();
                    photoInfo= LitePal.where("uri = ? ",uriToFind).find(PhotoInfo.class).get(0);
                }catch (Exception e){
                    Log.d(TAG,"error query photo info ; msg : " +e.getMessage());
                }
                processShowPhotoInfo();
                break;
            case R.id.onephoto_photo_new_style:
                intent = new Intent(this, StyleMigrationActivity.class);
                // 在Intent中传递数据
                intent.putExtra("from", "onePhoto");
                intent.putExtra("photoUri", uriArrayList.get(currPosition).toString());
                // 启动Intent
                startActivity(intent);
                break;
            case R.id.onephoto_photo_poem:
                processPoem();
                break;
            case R.id.onephoto_photo_newscore:
                processNewScore();
                break;
        }
    }

    private void processShowPhotoInfo() {
        //photoInfo
        ExifInterface exifInterface = null;
        String path=UriPhotoHelp.getRealPathFromUri(this,uriArrayList.get(currPosition));
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String shijain = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        String baoguangshijian = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        String jiaoju = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        String chang = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        String kuan = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        String jiaodu = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        String baiph = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);


        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder
                .append("路径 = " + path+"\n");
        if(photoInfo!=null){
            infoBuilder
                    .append("AI写诗 = " + photoInfo.getPoem()+"\n")
                    .append("分数 = " + photoInfo.getScore()+"\n");
        }
        infoBuilder
                .append("时间 = " + shijain+"\n")
                .append("曝光时长 = " + baoguangshijian+"\n")
                .append("焦距 = " + jiaoju+"\n")
                .append("长 = " + chang+"\n")
                .append("宽 = " + kuan+"\n")
                .append("角度 = " + jiaodu+"\n")
                .append("白平衡 = " + baiph+"\n");

        DialogShowHelp.showPhotoInfo(this,infoBuilder.toString());
    }

    private void processNewScore() {
        DialogShowHelp.newScoreGet(this,uriArrayList.get(currPosition));
    }

    private void processPoem() {
        DialogShowHelp.poemGet(this,uriArrayList.get(currPosition));
    }
}
