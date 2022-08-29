package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;

import java.io.Serializable;

public class Box implements Serializable {
    private PointF mOrigin;
    private PointF mCurrent;
    private PointF mPointerOrigin;
    private Double mRotation;

    public Box(PointF origin) {
        mOrigin = origin;
        mRotation = null;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getPointerOrigin() {
        return mPointerOrigin;
    }

    public void setPointerOrigin(PointF pointerOrigin) {
        mPointerOrigin = pointerOrigin;
    }

    public Double getRotation() {
        return mRotation;
    }

    public void setRotation(Double rotation) {
        mRotation = rotation;
    }
}
