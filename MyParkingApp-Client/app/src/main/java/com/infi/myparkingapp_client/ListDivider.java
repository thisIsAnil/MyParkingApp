package com.infi.myparkingapp_client;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Infi on 4/30/2016.
 */
public class ListDivider extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS={
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST= LinearLayoutManager.HORIZONTAL;
    public static  final int VERTICAL_LIST=LinearLayoutManager.VERTICAL;

    private Drawable mDivider;
    private int mOrientation;
    public ListDivider(Context context,int orientation){
        final TypedArray a=context.obtainStyledAttributes(ATTRS);
        mDivider=a.getDrawable(0);
        a.recycle();
        SetOrientation(orientation);
    }
    public void SetOrientation(int or){
        if(or!=HORIZONTAL_LIST&&or!=VERTICAL_LIST){
            throw new IllegalArgumentException("Invalid Orientation");
        }
        mOrientation=or;
    }
    @Override
    public void onDrawOver(Canvas c,RecyclerView parent,RecyclerView.State state){
        if(mOrientation==VERTICAL_LIST){
            drawVertical(c,parent);
        }else
        {
            drawHorizontal(c,parent);
        }
    }

    public void drawVertical(Canvas c,RecyclerView parent){

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);}
    }
    public void drawHorizontal(Canvas c,RecyclerView parent){

        final int top=parent.getPaddingTop();
        final int bottom=parent.getWidth()-parent.getPaddingBottom();

        final int childCount=parent.getChildCount();
        for(int i=0;i<childCount;i++){
            final View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();

            final int left=child.getLeft()+params.leftMargin;
            final  int right=left+mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }

}
