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

    Button btnProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnProgress = (Button) findViewById(R.id.btnProgress);
        final Bitmap myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.face);
        detectFace(myBitmap);
    }

    public void detectFace(final Bitmap myBitmap) {

        final Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);

        final Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(myBitmap, 0, 0, null);

        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.google.android.gms.vision.face.FaceDetector faceDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                        .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                        .build();
                if (!faceDetector.isOperational()) {
                    Toast.makeText(getApplicationContext(), "your device is not operationg face detector", Toast.LENGTH_SHORT).show();
                    return;
                }
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> sparseArray = faceDetector.detect(frame);

                if (sparseArray != null) {
                    RectF rectF;
                    for (int i = 0; i < sparseArray.size(); i++) {
                        Face face = sparseArray.valueAt(i);
                        float x1 = face.getPosition().x;
                        float y1 = face.getPosition().y;
                        float x2 = x1 + face.getWidth();
                        float y2 = y1 + face.getHeight();
                        rectF = new RectF(x1, y1, x2, y2);
                        canvas.drawRoundRect(rectF, 2, 2, rectPaint);
                        Log.d(String.valueOf(x1) + String.valueOf(x2) + String.valueOf(y1) + String.valueOf(y2), "locationOfRect");
                    }
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), tempBitmap);
                    drawable.draw(canvas);
//                    imageView.setImageDrawable(drawable);
                    Toast.makeText(getApplicationContext(), "take picture", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "I don't know what to do", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
