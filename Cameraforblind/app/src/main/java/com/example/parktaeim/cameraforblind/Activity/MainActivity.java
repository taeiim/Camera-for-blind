package com.example.parktaeim.cameraforblind.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parktaeim.cameraforblind.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import static com.example.parktaeim.cameraforblind.R.id.faceDetectionView;
import static com.example.parktaeim.cameraforblind.R.id.loadCameraButton;

//import com.example.parktaeim.cameraforblind.Preview;
//import com.example.parktaeim.cameraforblind.R;
//import com.example.parktaeim.cameraforblind.views.CameraSurfaceView;
//import com.example.parktaeim.cameraforblind.views.FaceDetectionView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

    }


}
