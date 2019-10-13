package com.example.artistcamera.PresentationLayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.artistcamera.PresentationLayer.ViewLib.BottomNavigationViewHelper;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.BaseEditFragment;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.FilterFragment;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.PoemFragment;
import com.example.artistcamera.PresentationLayer.ViewLib.Fragment.FragmentTransfer;
import com.example.artistcamera.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class PicEditActivity extends Activity {

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_base_edit:
                    transaction.replace(R.id.fragment_content, new BaseEditFragment());
                    transaction.commit();
                    return true;
                case R.id.nav_filter:
                    transaction.replace(R.id.fragment_content, new FilterFragment());
                    transaction.commit();
                    return true;
                case R.id.nav_transfer:
                    transaction.replace(R.id.fragment_content, new FragmentTransfer());
                    transaction.commit();
                    return true;
                case R.id.nav_poem:
                    transaction.replace(R.id.fragment_content, new PoemFragment());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    // 设置默认进来时显示的页面
    private void setDefaultFragment(){
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content,new FilterFragment());
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_edit);

        //悬浮按钮点击事件
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PicEditActivity.this, EditFinishActivity.class));
            }
        });

        setDefaultFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setItemIconTintList(null);
        BottomNavigationViewHelper navigationViewHelper = new BottomNavigationViewHelper();
        navigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.nav_filter); //初始默认位置为滤镜
    }

}
