package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    // used when creating the view in the code
    public BoxDrawingView(Context context) {
        super(context);
    }
    // used when creating the view from the XML
    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        ListIterator<Box> iterator = mBoxen.listIterator();
        while(iterator.hasNext()){
            bundle.putSerializable(
                    String.valueOf(iterator.nextIndex()), iterator.next()
            );
        }
        bundle.putInt("length", mBoxen.size());
        bundle.putParcelable("superState", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;

            for(int i = 0;i < bundle.getInt("length");i++){
                mBoxen.add((Box) bundle.getSerializable(String.valueOf(i)));
            }

            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            if (mCurrentBox.getRotation() != null){
                canvas.rotate(mCurrentBox.getRotation().floatValue());
                canvas.restore();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF current = null;
        PointF second = null;
        String action = "";
        for (int i=0;i<event.getPointerCount();i++) {
            if(event.getPointerId(i)==0){
                Log.i(TAG, "onTouchEvent: 1");
                current = new PointF(event.getX(i), event.getY(i));
            }
            if(event.getPointerId(i)==1){
                Log.i(TAG, "onTouchEvent: 2");
                second = new PointF(event.getX(i), event.getY(i));
            }
        }
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // Reset drawing state
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (current != null){
                    mCurrentBox.setCurrent(current);
                    Log.i(TAG, "second: " + current.x + " " + current.y);
                }
                if (second != null){
                    PointF boxOrigin = mCurrentBox.getOrigin();
                    PointF pointerOrigin = mCurrentBox.getPointerOrigin();
                    double slope1 = (boxOrigin.y-pointerOrigin.y) / (boxOrigin.x-pointerOrigin.x);
                    double slope2 = (boxOrigin.y-second.y) / (boxOrigin.x-second.x);
                    double rot = Math.atan2(slope2-slope1, 1+slope1*slope2);
                    mCurrentBox.setRotation(rot);
                    Log.i(TAG, "rotation: " + Math.toDegrees(rot));
                    Log.i(TAG, "second: " + second.x + " " + second.y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP";

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN";
                mCurrentBox.setPointerOrigin(second);

                break;
        }

        //Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }
}
