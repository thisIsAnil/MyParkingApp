package com.example.asus.myparkingapp.FaceVerification.helper;

import android.app.Application;

import com.example.asus.myparkingapp.R;
import com.example.asus.myparkingapp.TempFolderMaker;
import com.example.asus.myparkingapp.UpdateOnAreaChanged;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

/**
 * Created by Asus on 18-09-2016.
 */

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        sFaceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
        TempFolderMaker folderMaker=new TempFolderMaker();
        new UpdateOnAreaChanged(getApplicationContext(),false,"400X200");
    }

    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient;
}
