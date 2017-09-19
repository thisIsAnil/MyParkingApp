package com.infi.myparkingapp_client;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

/**
 * Created by INFIi on 9/22/2016.
 */
public class CircularReveal {
    private View rootLayout;
    private int width,height;
    public CircularReveal(View frameLayout){
        this.rootLayout=frameLayout;
        width=rootLayout.getWidth() / 2;
        height=rootLayout.getHeight() / 2;
    }
    public CircularReveal(View view,int width,int height){
        rootLayout=view;this.width=width;this.height=height;
    }
    public void StartCircularReveal(){
        rootLayout.setVisibility(View.INVISIBLE);

        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circularRevealActivity();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }
    public void CloseCircularReveal(){
        rootLayout.setVisibility(View.INVISIBLE);

        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ClosecircularRevealActivity();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }
    @SuppressLint("NewApi")
    public void circularRevealActivity() {

        int cx = width;
        int cy = height;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }
    @SuppressLint("NewApi")
    public void ClosecircularRevealActivity() {

        int cx = width;
        int cy = height;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy,finalRadius, 0 );
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.INVISIBLE);
        circularReveal.start();
    }
}
