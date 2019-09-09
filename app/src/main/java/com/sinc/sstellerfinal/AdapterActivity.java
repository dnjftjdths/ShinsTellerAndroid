package com.sinc.sstellerfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AdapterActivity extends AppCompatActivity {

    Adapter adapter;
    ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_main);
        viewPager = (ViewPager) findViewById(R.id.view);
        adapter = new Adapter(this);
        viewPager.setAdapter(adapter);

    }

    public void goPage(View view){
        switch (view.getId()){
            case R.id.btn_1: viewPager.setCurrentItem(0); break;
            case R.id.btn_2: viewPager.setCurrentItem(1); break;
            case R.id.btn_3: viewPager.setCurrentItem(2); break;
        }

    }


    public void closeTuto(){
        goMainActivity(viewPager);
    }


    public void goMainActivity(View view){
        Intent intent = new Intent(this, com.sinc.sstellerfinal.MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            goMainActivity(viewPager);
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }
}
