package com.example.artistcamera.PresentationLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.artistcamera.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnCamera;
    private ImageButton btnAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo 替换findviewbyid为bufferkie
        btnCamera = findViewById(R.id.btn_1);
        btnAlbum = findViewById(R.id.btn_2);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PicEditActivity.class));
            }
        });
    }
}
