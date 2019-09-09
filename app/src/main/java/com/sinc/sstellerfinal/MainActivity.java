package com.sinc.sstellerfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class MainActivity extends AppCompatActivity { //AppCompatActivity

    private final Handler handler = new Handler();

    private WebView mWebView;
    private CustomerDialog customerDialog;
    private LoadingDialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        webviewSet();
        customerDialogInit();
        loadingDialogInit();
    }


    private void webviewSet() {
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSupportZoom(false);//
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        //mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.setNetworkAvailable(true);

        mWebView.loadUrl("http://10.149.179.116:8088/index.sst");//10.149.179.93
        //mWebView.loadUrl("http://www.naver.com");

        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.addJavascriptInterface(new AndroidBridge(), "goVideo");


        //alert안될경우 넣어준다
        final Context myApp = this;

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
//                new AlertDialog.Builder(myApp)
//                        .setTitle("AlertDialog")
//                        .setMessage(message)
//                        .setPositiveButton(android.R.string.ok,
//                                new AlertDialog.OnClickListener()
//                                {
//                                    public void onClick(DialogInterface dialog, int which)
//                                    {
//                                        result.confirm();
//                                    }
//
//                                })
//                        .setCancelable(false)
//                        .create()
//                        .show();
//                return true;
                loadingDialog.show();
                result.confirm();
                return true;
            };
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                /*
                 * 화면꽉찬 WebView로 바꾸기
                 * invalidate를 안해주게 되면
                 * webview에 스크롤바만 생기게 되서 화면이 보기 안좋게 되어 버린다.
                 * 그러므로 꼭 invalidate를 해줘야 한다.
                 */

                mWebView.invalidate();

            }

        });



    }


    public void customerDialogInit(){
        customerDialog = new CustomerDialog(this, positiveListener,negativeListener);

    }

    public void loadingDialogInit(){
        loadingDialog = new LoadingDialog(this);

    }

    private View.OnClickListener positiveListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            android.os.Process.killProcess(android.os.Process.myPid());
            customerDialog.dismiss();
        }
    };

    private View.OnClickListener negativeListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            customerDialog.dismiss();
        }
    };


    //익명클래스로 javascript에서 보낸 값 전달 받음
    //setMessage 의 경우
    //  mWebView.addJavascriptInterface(new AndroidBridge(), "testname") 여기서의 이름과 동일해야 함
    private class AndroidBridge {
        @JavascriptInterface
        public void getVideoPath(final String arg, final  String title, final String price, final String timeStamp) {

            //Toast.makeText(MainActivity.this, timeStamp, Toast.LENGTH_SHORT).show();
            handler.post(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>videoNAme>>>>>>>>>>>>"+arg);
                    intent.putExtra("timeStamp",timeStamp);
                    intent.putExtra("videoName", arg);
                    intent.putExtra("title", title);
                    intent.putExtra("price",price);
                    loadingDialog.dismiss();
                    startActivity(intent);

                }
            });
        }
    }




    public class WebViewClientClass extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return  true;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true ;
        }else if( (keyCode == KeyEvent.KEYCODE_BACK) && !mWebView.canGoBack() ){
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK :
                    customerDialog.show();
                    break ;
            }
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }
}
