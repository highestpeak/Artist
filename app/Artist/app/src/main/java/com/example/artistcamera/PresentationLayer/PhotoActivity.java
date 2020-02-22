package com.example.artistcamera.PresentationLayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.artistcamera.DataLayer.Bean.ArtistPhotoExtend;
import com.example.artistcamera.DataLayer.vo.PhotoInfoVO;
import com.example.artistcamera.R;
import com.example.artistcamera.Util.DialogShowHelp;
import com.example.artistcamera.Util.UriPhotoHelp;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.artistcamera.PresentationLayer.ViewLib.ArtistDialogLib.photoInfoDialog;
import static com.example.artistcamera.Util.PhotoHelp.getPhotoInfo;

public class PhotoActivity extends AppCompatActivity {
    /**
     * debug
     */
    private static final String TAG = "PhotoActivity";
    @BindView(R.id.onephoto_photo_info)
    ImageView photoInfoImageView;
    @BindView(R.id.onephoto_photo_new_style)
    ImageView photoNewStyleImageView;
    @BindView(R.id.onephoto_photo_poem)
    ImageView photoPoemImageView;
    @BindView(R.id.onephoto_photo_newscore)
    ImageView photoNewScoreImageView;
    @BindView(R.id.pager)
    ViewPager mPager;

    private ArrayList<Uri> uriArrayList;
    private ArtistPhotoExtend artistPhotoExtend =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);

        uriArrayList = getNewestPhoto(this);

        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
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
                artistPhotoExtend =null;
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
    public static ArrayList<Uri> getNewestPhoto(Context context) {

        ArrayList<String> img_path = new ArrayList<>();
        ArrayList<Uri> img_uri = new ArrayList<>();
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 获取SDcard卡的路径
        String sdcardPath = Environment.getExternalStorageDirectory().toString();

        ContentResolver mContentResolver = context.getContentResolver();
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
                finish();
                break;
            case R.id.onephoto_gallery:
                finish();
                break;
            case R.id.onephoto_close:
                finish();
                break;
            case R.id.onephoto_photo_info:
                photoInfoDialog(this,uriArrayList.get(mPager.getCurrentItem()));
                break;
            case R.id.onephoto_photo_new_style:
                intent = new Intent(this, StyleMigrationActivity.class);
                intent.putExtra("from", "onePhoto");
                intent.putExtra("photoUri", uriArrayList.get(mPager.getCurrentItem()).toString());
                startActivity(intent);
                break;
            case R.id.onephoto_photo_poem:
                //todo
                processPoem();
                break;
            case R.id.onephoto_photo_newscore:
                //todo
                processNewScore();
                break;
        }
    }

    private void processNewScore() {
        DialogShowHelp.newScoreGet(this,uriArrayList.get(mPager.getCurrentItem()));
    }

    private void processPoem() {
        DialogShowHelp.poemGet(this,uriArrayList.get(mPager.getCurrentItem()));
    }
}
