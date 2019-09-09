package com.sinc.sstellerfinal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class FragmentTimeStamp extends LinearLayout {

    public FragmentTimeStamp(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    public FragmentTimeStamp(Context context){
        super(context);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_timestamp, this, true);
    }
}
