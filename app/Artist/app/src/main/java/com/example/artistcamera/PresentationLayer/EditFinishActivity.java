package com.example.artistcamera.PresentationLayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.artistcamera.R;

public class EditFinishActivity extends Activity {

    private ImageView text_finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_finish);

        text_finish = findViewById(R.id.edit_finish_back_home);
        text_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditFinishActivity.this, MainActivity.class));
            }
        });
    }

}
