package com.example.parktaeim.cameraforblind.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.parktaeim.cameraforblind.R;

/**
 * Created by parktaeim on 2017. 10. 15..
 */

public class CameraSelectActivity extends AppCompatActivity {
    private RelativeLayout cameraTensorflowLayout;
    private RelativeLayout cameraLetterLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_select);

        cameraTensorflowLayout = (RelativeLayout) findViewById(R.id.cameraTensorflowLayout);
        cameraLetterLayout = (RelativeLayout) findViewById(R.id.cameraLetterLayout);

        cameraTensorflowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraSelectActivity.this,CameraTensorflowActivity.class);
                startActivity(intent);
            }
        });

        cameraLetterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraSelectActivity.this, CameraLetterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
