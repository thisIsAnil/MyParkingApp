package com.example.asus.myparkingapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.example.asus.myparkingapp.FaceVerification.ui.IdentificationActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Asus on 17-09-2016.
 */

public class PhotoHandler implements Camera.PictureCallback {

    private final Context context;
    private final boolean isAdd;
    private Uri uri;
    public PhotoHandler(Context context,boolean isAdd) {
        this.context = context;
        this.isAdd=isAdd;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {


            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        long count=pictureFileDir.listFiles().length;
        String photoFile = "User" + count + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            StringPref stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.LAST_PATH,filename);
            stringPref.setStringPref(filename);

            Toast.makeText(context, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {

            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
            uri=null;
        }
    }

    public Uri getUri() {
        return uri;
    }

    private File getDir() {
        if(!isAdd)return TempFolderMaker.getRequest();
        else return TempFolderMaker.getDb();
    }
}
