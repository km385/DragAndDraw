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

            if (box.getRotation() != null){
                double angle = box.getRotation();
                float px = (box.getOrigin().x+box.getCurrent().x)/2;
                float py = (box.getOrigin().y+box.getCurrent().y)/2;
                canvas.save();
                canvas.rotate((float) angle, px, py);
                canvas.drawRect(left, top, right, bottom, mBoxPaint);
                canvas.restore();
            }else {
                canvas.drawRect(left, top, right, bottom, mBoxPaint);
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
                current = new PointF(event.getX(i), event.getY(i));
            }
            if(event.getPointerId(i)==1){
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
                }
                if (second != null){
                    PointF boxOrigin = mCurrentBox.getOrigin();
                    PointF pointerOrigin = mCurrentBox.getPointerOrigin();
                    double slope1 = Math.atan2(pointerOrigin.y-boxOrigin.y, pointerOrigin.x-boxOrigin.x);
                    double slope2 = Math.atan2(second.y-boxOrigin.y, second.x-boxOrigin.x);
                    double rot = Math.toDegrees(slope2-slope1);
                    if (rot < 0) rot += 360;
                    mCurrentBox.setRotation(rot);
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

        Log.i(TAG, action);

        return true;
    }
}
