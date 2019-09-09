package com.sinc.sstellerfinal;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

public class FullscreenableChromeClient extends WebChromeClient {
    private Activity mActivity = null;

    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private FrameLayout mFullscreenContainer;
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private TextView tex;
    private ActionBar actionBar;

    public FullscreenableChromeClient(Activity activity, ActionBar actionBar) {
        this.mActivity = activity;

        this.actionBar = actionBar;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
             //////////////////////////////////////////////////////////// 전체화면 버튼 누르면 들어옴
//            tex.setText("되려나?");
              //////////////////////////////////////////////////////////
            this.actionBar.show();
            mOriginalOrientation = mActivity.getRequestedOrientation();
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(mActivity);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);

            ///////////////////////////////////////////////////////////////////////
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//            );
//            tex.setLayoutParams(lp);
//            decor.addView(tex);
            ///////////////////////////////////////////////////////////////////
            mCustomView = view;
            setFullscreen(true);
            mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);

        }

        super.onShowCustomView(view, callback);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        this.onShowCustomView(view, callback);

    }

    @Override
    public void onHideCustomView() { //축소 누르면 들어옴
        if (mCustomView == null) {
            return;
        }

        setFullscreen(false);
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        mActivity.setRequestedOrientation(mOriginalOrientation);

    }

    private void setFullscreen(boolean enabled) {  //전체화면 버튼만 누르면 누르면 들어옴

        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }

    private static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
        }
        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
}
