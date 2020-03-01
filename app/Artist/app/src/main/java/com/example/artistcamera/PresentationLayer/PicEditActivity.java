package com.example.artistcamera.PresentationLayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import android.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.artistcamera.PresentationLayer.ViewLib.BottomNavigationViewHelper;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.BaseEditFragment;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.FilterFragment;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.FragmentTransfer;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.PoemFragment;
import com.example.artistcamera.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PicEditActivity extends Activity {
    static {
        System.loadLibrary("NativeImageProcessor"); // 滤镜库载入
    }

    private Uri uriProcessed=null;

    @BindView(R.id.processed_photo)
    ImageView processedPhoto;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Map<Integer, Fragment> fragmentMap;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean succeed = false;
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if (!fragmentMap.containsKey(item.getItemId())) {
                transaction.replace(R.id.fragment_content, fragmentMap.get(R.id.nav_filter));
            } else {
                transaction.replace(R.id.fragment_content, fragmentMap.get(item.getItemId()));
                succeed = true;
            }
            transaction.commit();
            return succeed;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_edit);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if (from.equals("onePhoto")) {
            uriProcessed = Uri.parse(intent.getStringExtra("photoUri"));
        }
        if (uriProcessed == null){
            finish();
        }

        initImageProcess();

        initFragment();

    }

    @SuppressLint("UseSparseArrays")
    private void initFragment() {
        // set fragment map
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.nav_base_edit, new BaseEditFragment());
        fragmentMap.put(R.id.nav_filter, new FilterFragment());
        fragmentMap.put(R.id.nav_transfer, new FragmentTransfer());
        fragmentMap.put(R.id.nav_poem, new PoemFragment());

        // set default fragment
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, new FilterFragment());
        transaction.commit();

        // set navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setItemIconTintList(null);
        BottomNavigationViewHelper navigationViewHelper = new BottomNavigationViewHelper();
        navigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.nav_filter);
    }

    private void initImageProcess() {
        Glide.with(this)
                .load(uriProcessed)
                .fitCenter()
                .into(processedPhoto);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = new Intent(this, EditFinishActivity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Object ob =  processedPhoto.getDrawable();
        Bitmap bm = null;
        if (ob instanceof GlideBitmapDrawable){
            bm = ((GlideBitmapDrawable) ob).getBitmap();
        }else{
            bm = ((BitmapDrawable) ob).getBitmap();
        }
        Bitmap outputImage = bm.copy(bm.getConfig(),true);
        outputImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        intent.putExtra("finishImageBytes",bytes);
        startActivity(intent);
    }

    public Uri getUriProcessed() {
        return uriProcessed;
    }

    public ImageView getProcessedPhoto() {
        return processedPhoto;
    }
}
