package com.bignerdranch.android.draganddraw;

import androidx.fragment.app.Fragment;


public class DragAndDrawActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}