package com.example.parktaeim.cameraforblind.Activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parktaeim.cameraforblind.Classifier;
import com.example.parktaeim.cameraforblind.R;
import com.example.parktaeim.cameraforblind.TensorFlowImageClassifier;
import com.google.android.cameraview.CameraView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;


/**
 * Created by parktaeim on 2017. 10. 14..
 */

public class CameraTensorflowActivity extends AppCompatActivity {

    Bitmap bitmap;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private String resultString;
    private TextToSpeech myTTS;
    private boolean status = false;
    Button btnProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tensorflow);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = (Button) findViewById(R.id.btnToggleCamera);
        btnDetectObject = (Button) findViewById(R.id.btnDetectObject);

        if (cameraView != null) {
            cameraView.addCallback(mCallback);
        }

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView != null) {
                    int facing = cameraView.getFacing();
                    cameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });
        initTensorFlowAndLoadModel();

//        cameraView.takePicture();

//        cameraView.setCameraListener(new CameraListener() {
//            @Override
//            public void onPictureTaken(byte[] picture) {
//                super.onPictureTaken(picture);
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
//
//                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
//
//                imageViewResult.setImageBitmap(bitmap);
//
//                String title = "image11";
//
//                Context context = getApplicationContext();
//                String filePath = (context.getExternalCacheDir()).toString() + "/" + title + ".jpg";
//
////                addImageToGallery(filePath,context);
//                imageSave(bitmap);
//                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
//
//                Log.d("result message",results.toString());
//
//                resultString = results.toString();
//                String pattern =  "^[a-zA-Z]*$";
//
//                resultString = resultString.replaceAll("[^a-zA-Z,]","");
//                Log.d("resultString",resultString);
//
//                myTTS = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
//                    @Override
//                    public void onInit(int i) {
//                        myTTS.setLanguage(Locale.KOREAN);
//                        myTTS.speak(resultString,TextToSpeech.QUEUE_FLUSH,null);
//                    }
//                });
//
//                textViewResult.setText(results.toString());
//            }
//        });
    }
//    public static void addImageToGallery(final String filePath, final Context context) {
//
//        ContentValues values = new ContentValues();
//
//        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.MediaColumns.DATA, filePath);
//
//        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        Toast.makeText(context,"파일 저장",Toast.LENGTH_SHORT).show();
//    }

    private void imageSave(Bitmap finalBitmap) {
        Context context = getApplicationContext();
        Calendar calendar = Calendar.getInstance();
        String title = "image11";
        String filePath = (context.getExternalCacheDir()).toString() + "/" + title + ".jpg";


        try {
            File file = new File(title);
            FileOutputStream fos = openFileOutput(title, 0);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        checkPermission();
        MediaStore.Images.Media.insertImage(getContentResolver(), finalBitmap, "title", "descripton");

        Toast.makeText(this, "파일 저장 완료", Toast.LENGTH_SHORT).show();

    }


    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    }


    private CameraView.Callback mCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, byte[] data) {
            super.onPictureTaken(cameraView, data);

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

//            ExifInterface exif = new ExifInterface(imagePath);
//            int exifOrientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = exifOrientationToDegrees(exifOrientation);
//            image = rotate(image, exifDegree);

            imageViewResult.setImageBitmap(bitmap);

            String title = "image11";

            Context context = getApplicationContext();
            String filePath = (context.getExternalCacheDir()).toString() + "/" + title + ".jpg";

//                addImageToGallery(filePath,context);
            imageSave(bitmap);
            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

            Log.d("result message", results.toString());

            resultString = results.toString();
            String pattern = "^[a-zA-Z]*$";

            resultString = resultString.replaceAll("[^a-zA-Z,]", "");
            Log.d("resultString", resultString);

            myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    myTTS.setLanguage(Locale.KOREAN);
                    myTTS.speak(resultString, TextToSpeech.QUEUE_FLUSH, null);
                }
            });

            textViewResult.setText(results.toString());

        }

//
    };

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }
}