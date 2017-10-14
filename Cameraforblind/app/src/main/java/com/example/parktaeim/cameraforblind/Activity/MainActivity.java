package com.example.parktaeim.cameraforblind.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.parktaeim.cameraforblind.Preview;
import com.example.parktaeim.cameraforblind.R;

public class MainActivity extends AppCompatActivity {

    private TextureView cameraView;
    private Preview preview;

    private static final String TAG = "MAINACTIVITY";
    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cameraView = (TextureView) findViewById(R.id.cameraTextureView);
        preview = new Preview(this, cameraView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            cameraView = (TextureView) findViewById(R.id.cameraTextureView);
                            preview = new Preview(this, cameraView);
                            Log.d(TAG, "mPreview set");
                        } else {
                            Toast.makeText(this, "Should have camera permission to run", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        preview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.onPause();
    }
}
