package com.infi.myparkingapp_client;

import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by INFIi on 8/4/2016.
 */
public class AnimateLayout extends Animation{
    private int mWidth;
    private int mStartWidth;
    private View mView;

    public AnimateLayout(View view, int width)
    {
        mView = view;
        mWidth = width;
        mStartWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

            mView.getLayoutParams().width = newWidth;
            mView.requestLayout();
        }
        else {
            int cx = mView.getWidth() / 2;
            int cy = mView.getHeight() / 2;

            float finalRadius = Math.max(mView.getWidth(), mView.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(mView, cx, cy, 0, finalRadius);
            circularReveal.setDuration(1000);

            // make the view visible and start the animation
            mView.setVisibility(View.VISIBLE);
            circularReveal.start();
        }

    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }

}
