
package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import com.example.asus.myparkingapp.FaceVerification.ui.IdentificationActivity;
import com.example.asus.myparkingapp.FaceVerification.ui.SelectImageActivity;

import java.io.File;
import java.util.List;


public class CameraActivity extends Activity
        implements SurfaceHolder.Callback {

    public static final String TAG = CameraActivity.class.getSimpleName();

    private Camera mCamera;
    private int mOrientation;
    private int mOrientationCompensation;
    private OrientationEventListener mOrientationEventListener;

    private int mDisplayRotation;
    private int mDisplayOrientation;

    private Camera.Face[] mFaces;

    private SurfaceView mView;

    private FaceOverlayView mFaceView;

    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();

    private FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {
        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            Log.d("onFaceDetection", "Number of Faces:" + faces.length);
            try {
                mFaceView.setFaces(faces);
                if (faces.length > 0) {
                    final Camera cam = camera;
                    cam.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, final Camera camera) {
                            PhotoHandler photoHandler = new PhotoHandler(getApplicationContext(), false);
                            camera.takePicture(null, new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    BoolPref boolPref = new BoolPref(getSharedPreferences(KEYS.PREF_NAME, MODE_PRIVATE), KEYS.IS_CAMERA, false);
                                    boolPref.setBooleanPref(true);
                                    startActivity(new Intent(CameraActivity.this, IdentificationActivity.class));

                                }
                            }, photoHandler);
                        }
                    });


                }
            }catch (Exception e){
                Log.d("onFaceDetection",e.getMessage());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new SurfaceView(this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            MarshmallowPermission permission=new MarshmallowPermission(CameraActivity.this);
            if(!permission.checkPermissionForCamera())permission.requestPermissionForCamera();
            if(!permission.checkPermissionForExternalStorage())permission.requestPermissionForExternalStorage();
        }
        setContentView(mView);
        mFaceView = new FaceOverlayView(this);
        addContentView(mFaceView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mOrientationEventListener = new SimpleOrientationEventListener(this);
        mOrientationEventListener.enable();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SurfaceHolder holder = mView.getHolder();
        holder.addCallback(this);
    }

    @Override
    protected void onPause() {
        mOrientationEventListener.disable();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mOrientationEventListener.enable();
        super.onResume();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open();
        mCamera.setFaceDetectionListener(faceDetectionListener);
        mCamera.startFaceDetection();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            Log.e(TAG, "Could not preview the image.", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }

        configureCamera(width, height);
        setDisplayOrientation();
        setErrorCallback();

        mCamera.startPreview();
    }

    private void setErrorCallback() {
        mCamera.setErrorCallback(mErrorCallback);
    }

    private void setDisplayOrientation() {
        mDisplayRotation = Util.getDisplayRotation(CameraActivity.this);
        mDisplayOrientation = Util.getDisplayOrientation(mDisplayRotation, 0);

        mCamera.setDisplayOrientation(mDisplayOrientation);

        if (mFaceView != null) {
            mFaceView.setDisplayOrientation(mDisplayOrientation);
        }
    }

    private void configureCamera(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        setOptimalPreviewSize(parameters, width, height);
        setAutoFocus(parameters);
        mCamera.setParameters(parameters);
    }

    private void setOptimalPreviewSize(Camera.Parameters cameraParameters, int width, int height) {
        List<Camera.Size> previewSizes = cameraParameters.getSupportedPreviewSizes();
        float targetRatio = (float) width / height;
        Camera.Size previewSize = Util.getOptimalPreviewSize(this, previewSizes, targetRatio);
        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);
    }

    private void setAutoFocus(Camera.Parameters cameraParameters) {
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.setPreviewCallback(null);
        mCamera.setFaceDetectionListener(null);
        mCamera.setErrorCallback(null);
        mCamera.release();
        mCamera = null;
    }

    private class SimpleOrientationEventListener extends OrientationEventListener {

        public SimpleOrientationEventListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            mOrientation = Util.roundOrientation(orientation, mOrientation);
            int orientationCompensation = mOrientation
                    + Util.getDisplayRotation(CameraActivity.this);
            if (mOrientationCompensation != orientationCompensation) {
                mOrientationCompensation = orientationCompensation;
                mFaceView.setOrientation(mOrientationCompensation);
            }
        }
    }
}