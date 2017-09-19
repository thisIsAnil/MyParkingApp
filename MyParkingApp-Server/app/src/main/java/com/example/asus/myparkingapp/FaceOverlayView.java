
package com.example.asus.myparkingapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera.Face;
import android.view.View;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FaceOverlayView extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private int mDisplayOrientation;
    private int mOrientation;
    private Face[] mFaces;

    public FaceOverlayView(Context context) {
        super(context);
        initialize();
        this.setDrawingCacheEnabled(true);
    }

    private void initialize() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStyle(Paint.Style.FILL);
    }

    public void setFaces(Face[] faces) {
        mFaces = faces;
        invalidate();
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaces != null && mFaces.length > 0) {
            Matrix matrix = new Matrix();
            Util.prepareMatrix(matrix, false, mDisplayOrientation, getWidth(), getHeight());
            canvas.save();
            matrix.postRotate(mOrientation);
            canvas.rotate(-mOrientation);
            RectF rectF = new RectF();
            for (Face face : mFaces) {
                rectF.set(face.rect);
                matrix.mapRect(rectF);
                canvas.drawRect(rectF, mPaint);
                //canvas.drawText("Score " + face.score, rectF.right, rectF.top, mTextPaint);
            }
            canvas.restore();
        }
    }
    public boolean CheckAuthentication(){
        Bitmap bitmap=get();
        File parent=TempFolderMaker.getDb();
        File[] files=parent.listFiles();
        List<File> fileList=new ArrayList<>();
        for(int i =0; i<files.length; i++)
        {
            File file = files[i];
      /*It's assumed that all file in the path are in supported type*/
            String filePath = file.getPath();
            if(filePath.endsWith(".jpg")){
                fileList.add(file);
            }
        }
        return false;
    }
    public Bitmap get(){
        return this.getDrawingCache();
    }
}