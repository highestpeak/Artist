package com.example.artistcamera.PresentationLayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.artistcamera.MyApplication;
import com.example.artistcamera.R;
import com.yalantis.ucrop.UCrop;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

import static com.example.artistcamera.Util.UriPhotoHelp.getRealPathFromUri;

public class CropActivity extends AppCompatActivity {
    private final int CODE_SELECT_IMAGE = 2;//相册RequestCode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);
        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, CODE_SELECT_IMAGE);



    }


    private void photoClip(Uri uri) {
        // 调用系统中自带的图片剪裁
        GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
//                this.authority
                System.out.println("hahahha");
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                System.out.println("hahahha");
            }
        };
        GalleryFinal.openCrop(CODE_SELECT_IMAGE, getRealPathFromUri(this,uri), mOnHanlderResultCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        } else if (requestCode == CODE_SELECT_IMAGE){
//            String  photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
            photoClip(data.getData());
//            Glide.with(this).load(photoPath);
        }

    }

}
