package com.example.parktaeim.cameraforblind.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parktaeim.cameraforblind.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.Locale;

/**
 * Created by parktaeim on 2017. 10. 14..
 */

public class ImageRecognitionActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ALBUM = 100;
    ImageView selectedImageView;
    Button imageBtn;
    private TextToSpeech myTTS;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);

        selectedImageView = (ImageView) findViewById(R.id.selectedImageView);
        imageBtn = (Button) findViewById(R.id.imageBtn);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAlbumAction();

            }
        });

    }

    private void takeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_IMAGE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                try{
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                    selectedImageView.setImageBitmap(image_bitmap);

                    final Bitmap myBitmap = image_bitmap;
                    selectedImageView.setImageBitmap(myBitmap);

                    final Paint rectPaint = new Paint();
                    rectPaint.setStrokeWidth(5);
                    rectPaint.setColor(Color.RED);
                    rectPaint.setStyle(Paint.Style.STROKE);

                    final Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                    final Canvas canvas = new Canvas(tempBitmap);
                    canvas.drawBitmap(myBitmap,0,0,null);

                    FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                            .setTrackingEnabled(false)
                            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                            .setMode(FaceDetector.FAST_MODE)
                            .build();

                    if(!faceDetector.isOperational()){
                        return;
                    }
                    Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                    SparseArray<Face> sparseArray = faceDetector.detect(frame);


                    double scale = Math.min(canvas.getWidth() / image_bitmap.getWidth(), canvas.getHeight() / image_bitmap.getHeight());
                    for(int i =  0; i < sparseArray.size(); i++) {
                        Face face = sparseArray.valueAt(i);
                        float x1 = (float) (face.getPosition().x * scale);
                        float y1 = (float) (face.getPosition().y * scale);
                        float x2 = x1 + (float) (face.getWidth() * scale);
                        float y2 = y1 + (float) (face.getHeight() * scale);

                        if(String.valueOf(x1) != null) {
                            myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int i) {
                                    myTTS.setLanguage(Locale.KOREAN);
                                    myTTS.speak("얼굴 사진 입니다. ", TextToSpeech.QUEUE_FLUSH, null);

                                }
                            });
                        } else {
//                            Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();
                            myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int i) {
                                    myTTS.setLanguage(Locale.KOREAN);
                                    myTTS.speak("얼굴 사진이 아닙니다. ", TextToSpeech.QUEUE_FLUSH, null);

                                }
                            });
                        }

                        RectF rectF = new RectF(x1,y1,x2,y2);
                        canvas.drawRoundRect(rectF,2,2,rectPaint);

//                        Toast.makeText(getApplicationContext(), String.valueOf(x1) + String.valueOf(y1) + String.valueOf(x2) + String.valueOf(y2), Toast.LENGTH_SHORT).show();


                        for(Landmark landmark : face.getLandmarks()) {
                            int cx = (int) (landmark.getPosition().x * scale);
                            int cy = (int) (landmark.getPosition().y * scale);
                            canvas.drawCircle(cx, cy, 10, rectPaint);
                        }
                        selectedImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));


                        if(!face.getLandmarks().isEmpty()) {
                            float midPoint = (x1 + x2) / 2;
                            float yPoint = y1;

                            String smile = "status : smile";
                            if(face.getIsSmilingProbability() >= 0.5f) {
//                                Toast.makeText(getApplicationContext(), smile, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    selectedImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
