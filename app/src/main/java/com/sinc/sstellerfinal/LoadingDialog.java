package com.sinc.sstellerfinal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class LoadingDialog extends Dialog {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        layoutParams.dimAmount = 0.8f;
//        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.loading_dialog);

    }

    //생성자 생성
    public LoadingDialog(@NonNull Context context) {

        super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

}
