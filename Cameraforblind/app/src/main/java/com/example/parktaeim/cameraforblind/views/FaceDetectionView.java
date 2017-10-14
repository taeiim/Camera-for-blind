package com.example.parktaeim.cameraforblind.views;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.example.parktaeim.cameraforblind.R;
import com.example.parktaeim.cameraforblind.patch.SafeFaceDetector;
import com.example.parktaeim.cameraforblind.utils.Constants;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

public class FaceDetectionView extends View {

    private static final String TAG = FaceDetectionView.class.getSimpleName();

    private Bitmap bitmap;
    private SparseArray<Face> faces;
    private Resources resources;
    private Paint paintText;
    private Paint paintFace;

    public FaceDetectionView(Context context) {
        this(context, null);
    }

    public FaceDetectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceDetectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            resources = context.getResources();
            paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setTextSize(resources.getDimensionPixelSize(resources.getDimensionPixelSize(R.dimen.faceTextSize)));
            paintText.setColor(0xFFFF0000);

            paintFace = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintFace.setColor(Color.BLUE);
            paintFace.setStyle(Paint.Style.STROKE);
            paintFace.setStrokeWidth(8);
        }
    }

    public void setContent(Bitmap bitmap) {
        this.bitmap = bitmap;

        FaceDetector detector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        faces = safeDetector.detect(frame);

        if (!safeDetector.isOperational()) {
            Log.d(TAG, "Face detector dependencies are not yet available.");
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getContext().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getContext(), R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.d(TAG, getContext().getString(R.string.low_storage_error));
            }
        }
        invalidate();
        safeDetector.release();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((bitmap != null) && (faces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceAnnotations(canvas, scale);
        }
    }

    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = bitmap.getWidth();
        double imageHeight = bitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
        canvas.drawBitmap(bitmap, null, destBounds, null);
        return scale;
    }

    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);

            float left = (float) (face.getPosition().x * scale);
            float top = (float) (face.getPosition().y * scale);
            float right = left + (float) (face.getWidth() * scale);
            float bottom = top + (float) (face.getHeight() * scale);

            canvas.drawRect(left, top, right, bottom, paintFace);

            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }

            if (!face.getLandmarks().isEmpty()) {
                float midPoint = (left + right) / 2;
                float yPosition = top;
                String faceDetected = getContext().getString(R.string.s_face_detected);
                String doNotSmile = getContext().getString(R.string.s_dont_smile);

                float textWidth = paintText.measureText(faceDetected);
                canvas.drawText(getContext().getString(R.string.s_face_detected), midPoint - textWidth * 0.5f, yPosition + 50, paintText);
                if (face.getIsSmilingProbability() >= Constants.SMILE_PROBABILITY) {
                    textWidth = paintText.measureText(doNotSmile);
                    canvas.drawText(getContext().getString(R.string.s_dont_smile), midPoint - textWidth * 0.5f, yPosition + 150, paintText);
                }
            }

        }
    }

    public void release() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
