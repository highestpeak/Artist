package com.example.artistcamera.PresentationLayer;

import android.Manifest;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.artistcamera.DataLayer.ScoreCallBack;
import com.example.artistcamera.PresentationLayer.Presenter.ProcessWithThreadPool;
import com.example.artistcamera.PresentationLayer.ViewLib.CameraPreview;
import com.example.artistcamera.PresentationLayer.ViewLib.DirectSuggest;
import com.example.artistcamera.PresentationLayer.ViewLib.FocusRect;
import com.example.artistcamera.PresentationLayer.ViewLib.SettingsFragment;
import com.example.artistcamera.R;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class CameraActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, ScoreCallBack {
    /**
     * view bind
     */
    @BindView(R.id.camera_preview)
    FrameLayout preview;
    @BindView(R.id.img_gallery_switch)
    ImageView gallerySwitchButon;
    @BindView(R.id.img_take_photo)
    ImageView takePhotoButon;
    @BindView(R.id.img_camera_switch)
    ImageView cameraSwitchButon;
    @BindView(R.id.img_camera_setting)
    ImageView cameraSettingButon;
    @BindView(R.id.score_text)
    TextView scoreTextView;

    /**
     * debug
     */
    private static final String TAG = "CameraActivity";

    /**
     * permission
     */
    public static final int RC_CAMERA =0x100;//随便赋值的一个唯一权限标识码
    public static final String RC_STR_CAMERA ="需要相机和文件读写权限";
    private String[] perms = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * field use
     */
    private CameraPreview mPreview;
    private Point pointCenter=new Point();
    private ProcessWithThreadPool processWithThreadPool=new ProcessWithThreadPool();
    private FocusRect focusRect;
    private DirectSuggest directSuggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        Glide.with(this)
                .load(PhotoActivity.getNewestPhoto(this).get(0))
                .fitCenter()
                .into(gallerySwitchButon);

        //TODO scorecallback的调用顺序不明确，camerapreview启动顺序不是明确
        processWithThreadPool.setScoreCallBack(this::getScore);
        focusRect=new FocusRect(this);
        directSuggest=new DirectSuggest(this);
        startPreview();
    }

    public void startPreview() {
        mPreview = new CameraPreview(this);
        preview =findViewById(R.id.camera_preview);
        preview.removeAllViews();
        preview.addView(mPreview);
        mPreview.setFocusRect(focusRect);
        mPreview.setCenter(pointCenter);
        mPreview.setProcessFrameThreadPool(processWithThreadPool);
        preview.addView(focusRect);
        preview.addView(directSuggest);

        SettingsFragment.passCamera(mPreview.getCameraInstance());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SettingsFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
        SettingsFragment.init(PreferenceManager.getDefaultSharedPreferences(this));

        cameraSettingButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.camera_preview,
                        new SettingsFragment()).addToBackStack(null).commit();
            }
        });
    }

    public void stopPreview() {
        preview.removeAllViews();
    }

    /**
     * 解决返回黑屏
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mPreview == null) {
            startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview = null;
    }

   /**
    * 权限
    */
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            //已经打开权限
            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            try {
                EasyPermissions.requestPermissions(this, RC_STR_CAMERA, RC_CAMERA, perms);
            }catch (Exception e){
                Log.d(TAG, "failed to get permission");
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + list.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * click
     */
    @OnClick({R.id.img_gallery_switch, R.id.img_take_photo, R.id.img_camera_switch})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.img_gallery_switch:
                intent = new Intent(this, PhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.img_take_photo:
                mPreview.takePicture(gallerySwitchButon);
                break;
            case R.id.img_camera_switch:
                mPreview.switchCamera();
                break;
        }
    }

    /**
     * 回调
     */
    private long timeMark;
    @Override
    public void getScore(String scoreText, DirectSuggest.SUGGEST_DIRECT suggest_direct) {
        scoreTextView.post(new Runnable() {
            @Override
            public void run() {
                scoreTextView.setText(scoreText);
                if(mPreview!=null){
                    mPreview.setCurrScore(scoreText);
                }
            }
        });
        long yoSee=System.currentTimeMillis()-timeMark;
        if(System.currentTimeMillis()-timeMark>=1000 && mPreview!=null){
            directSuggest.setSuggest(mPreview.getCenter(),200,suggest_direct);
            timeMark=System.currentTimeMillis();
        }
    }
}
