package com.example.asus.myparkingapp.FaceVerification.ui;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.example.asus.myparkingapp.BoolPref;
import com.example.asus.myparkingapp.KEYS;
import com.example.asus.myparkingapp.MarshmallowPermission;
import com.example.asus.myparkingapp.R;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

// The activity for the user to select a image and to detect faces in the image.
public class SelectImageActivity extends Activity {
    // Flag to indicate the request of the next task to be performed
    public static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    // The URI of photo taken with camera
    private Uri mUriPhotoTaken;

    // When the activity is created, set all the member variables to initial state.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        BoolPref boolPref=new BoolPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.AUTOMATIC,false);
        if(boolPref.getBooleanPref()){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                MarshmallowPermission permission=new MarshmallowPermission(SelectImageActivity.this);
                if(!permission.checkPermissionForCamera())permission.requestPermissionForCamera();
                if(!permission.checkPermissionForExternalStorage())permission.requestPermissionForExternalStorage();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null) {
                // Save the photo taken to a temporary file.
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File file = File.createTempFile("IMG_", ".jpg", storageDir);
                    mUriPhotoTaken = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    setInfo(e.getMessage());
                }
            }
        }
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", mUriPhotoTaken);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri");
    }

    // Deal with the result of selection of the photos and faces.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }
                    Intent intent = new Intent();
                    intent.setData(imageUri);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    // When the button of "Take a Photo with Camera" is pressed.
    public void takePhoto(View view) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            MarshmallowPermission permission=new MarshmallowPermission(SelectImageActivity.this);
            if(!permission.checkPermissionForCamera())permission.requestPermissionForCamera();
            if(!permission.checkPermissionForExternalStorage())permission.requestPermissionForExternalStorage();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile("IMG_", ".jpg", storageDir);
                mUriPhotoTaken = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                setInfo(e.getMessage());
            }
        }
    }

    // When the button of "Select a Photo in Album" is pressed.
    public void selectImageInAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
        }
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }
}
