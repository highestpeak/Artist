package com.example.artistcamera.PresentationLayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.bumptech.glide.Glide;
import com.example.artistcamera.R;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    /**
     * debug
     */
    private static final String TAG = "PhotoActivity";

    private ViewPager mPager;

    private ArrayList<Uri> uriArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        uriArrayList=getNewestPhoto();

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
                ImageView imageView=new ImageView(getApplicationContext());
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
     * @return String
     */
    public ArrayList<Uri> getNewestPhoto() {

        ArrayList<String> img_path=new ArrayList<>();
        ArrayList<Uri> img_uri=new ArrayList<>();
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 获取SDcard卡的路径
        String sdcardPath = Environment.getExternalStorageDirectory().toString();

        ContentResolver mContentResolver = PhotoActivity.this.getContentResolver();
        Cursor mCursor = mContentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" },
                MediaStore.Images.Media.DATE_ADDED + " DESC"); // 按图片ID降序排列

        while (mCursor.moveToNext()) {
            // 打印LOG查看照片ID的值
            long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
//            Log.i("MediaStore.Images.Media_ID=", id + "");

            // 过滤掉不需要的图片，只获取拍照后存储照片的相册里的图片
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            img_path.add("file://" + path);
            Uri uri  = ContentUris.withAppendedId(mediaUri,
                    mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            img_uri.add(uri);
            if (img_path.size()==100){
                break;
            }

        }
        mCursor.close();
        return img_uri;
    }
}
