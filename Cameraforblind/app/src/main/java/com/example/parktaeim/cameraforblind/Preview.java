package com.example.parktaeim.cameraforblind;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.parktaeim.cameraforblind.Activity.MainActivity;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by user on 2017-10-14.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Preview extends Thread{

    private final static String TAg = "Preview : ";

    private Size previewSize;
    private Context context;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    private TextureView textureView;

    public Preview(Context context, TextureView textureView) {
        this.context = context;
        this.textureView = textureView;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getBackFacingCameraId(CameraManager manager) {
        try {
            for(final String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics charactaristics = manager.getCameraCharacteristics(cameraId);
                int orientation = charactaristics.get(CameraCharacteristics.LENS_FACING);
                if(orientation == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {}
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openCamera() {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = getBackFacingCameraId(manager);
            CameraCharacteristics characteristic = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristic.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            int permissinoCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if(permissinoCamera == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, /*MainActivity.REQUEST_CAMERA*/1);
            }
        }catch (CameraAccessException e) {}
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
    };

    protected void startPreview() {
        /*if(null = cameraDevice || textureView.isAvailable() || null == previewSize) {

        }*/

        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        if(null == surfaceTexture) {return;}

        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(surfaceTexture);

        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {}

        previewBuilder.addTarget(surface);

        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    previewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(context, "onConfigureFailed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {}
    }

    protected void updatePreview() {
        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundhandler = new Handler(thread.getLooper());

        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundhandler);
        }catch (CameraAccessException e) {}
    }

    public void setSurfaceTextureListener() {
        textureView.setSurfaceTextureListener(textureListener);
    }

    public void onResume() {
        setSurfaceTextureListener();
    }

    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    public void onPause() {
        try {
            cameraOpenCloseLock.acquire();
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing");
        } finally {
            cameraOpenCloseLock.release();
        }
    }
}
