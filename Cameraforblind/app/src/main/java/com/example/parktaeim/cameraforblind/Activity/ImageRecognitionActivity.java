package com.example.parktaeim.cameraforblind.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.parktaeim.cameraforblind.R;

/**
 * Created by parktaeim on 2017. 10. 14..
 */

public class ImageRecognitionActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ALBUM = 100;
    ImageView selectedImageView;
    Button imageBtn;

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

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
