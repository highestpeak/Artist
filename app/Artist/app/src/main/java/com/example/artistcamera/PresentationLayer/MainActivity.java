package com.example.artistcamera.PresentationLayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artistcamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnCamera)
    ImageButton btnCamera;
    @BindView(R.id.btnAlbum)
    ImageButton btnAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnCamera, R.id.btnAlbum})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCamera:
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
                break;
            case R.id.btnAlbum:
                startActivity(new Intent(MainActivity.this, PicEditActivity.class));
                break;
        }
    }

}
