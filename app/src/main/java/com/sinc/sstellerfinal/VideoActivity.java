package com.sinc.sstellerfinal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class VideoActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private WebView mWebview ;
    ImageView backPressed, go_btn;
    TextView videoName, videoPrice;
    String videoPath, title, price;
    /////////////////////////////STT용
    private static final int REQUEST_CODE = 1234;

    //////////////////////////slide bar 용
    //슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;
    //슬라이드 레이아웃
    LinearLayout slidingPage01;
    LinearLayout slidePageLeft;
    ScrollView scrollView;
    ImageView go_stt;
    LinearLayout timeStampLayer;
    Button timeStampText;

    //////////////////////////// stt 용
    Intent intent;
    SpeechRecognizer mRecognizer  = SpeechRecognizer.createSpeechRecognizer(VideoActivity.this);
    TextView sttResult, sttExam, sttNoResult;
    RelativeLayout sttWindow;
    ImageView mic_08, mic_db;
    final int PERMISSION = 1;
    GlideDrawableImageViewTarget gifImage;
    ImageView sttClose;
    LinearLayout sttBackWindow;
    ////////////////////////// search용
    RelativeLayout searchWindow;
    ClearEditText searchText;
    TextView searchNoResult;
    LinearLayout searchBackWindow;
    ImageView searchClose, go_search;
    InputMethodManager imm;
    ////////////////////////// 숫자형식
    DecimalFormat myFormatter = new DecimalFormat("###,###");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ///////////////////////intent 전환용
        Intent intent = new Intent(this.getIntent());
        videoPath = intent.getStringExtra("videoName");
        String timeStamp = intent.getStringExtra("timeStamp");
        title = intent.getStringExtra("title");
        price = myFormatter.format(Integer.parseInt(intent.getStringExtra("price")));
        /////////////////////////////////////////////
        sidebarInit(timeStamp);
        webviewSet(videoPath);
        initialize();

        /////STT용
        sttInit();
        //////Search용
        searchInit();


    }

    private void sttInit(){
        mic_08 = findViewById(R.id.mic_08);
        gifImage = new GlideDrawableImageViewTarget(mic_08);
        Glide.with(this).load(R.drawable.mic_09).into(gifImage);

        if(Build.VERSION.SDK_INT >= 23){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        sttResult = findViewById(R.id.sttResult);
        sttExam = findViewById(R.id.sttExam);
        sttWindow = findViewById(R.id.sttWindow);
        sttNoResult = findViewById(R.id.sttNoResult);
        sttClose = findViewById(R.id.sttClose);
        mic_db = findViewById(R.id.mic_db);
        sttBackWindow = findViewById(R.id.sttBackWindow);


        sttClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeStt(view);
            }
        });
        sttBackWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        go_stt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setTimeHandler(view);
                sttBackWindow.setVisibility(View.VISIBLE);
                sttWindow.setVisibility(View.VISIBLE);
                mRecognizer.setRecognitionListener(listener);
                startStt();

            }

        });

    }

    public void closeStt(View view){
        setTimeHandler(view);
        sttBackWindow.setVisibility(View.GONE);
        sttWindow.setVisibility(View.GONE);
        mRecognizer.cancel();
    }

    public void startStt(){
        sttResult.setText("");
        mic_08.setOnClickListener(null);
        sttNoResult.setVisibility(View.GONE);
        sttExam.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.mic_09).into(gifImage);
        mRecognizer.startListening(intent);
    }


    public void searchInit(){
        searchWindow = findViewById(R.id.searchWindow);
        searchText = findViewById(R.id.searchText);
        searchNoResult = findViewById(R.id.searchNoResult);
        searchBackWindow = findViewById(R.id.searchBackWindow);
        searchClose = findViewById(R.id.searchClose);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearch(view);
            }
        });
        searchBackWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        go_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeHandler(view);
                searchText.setText("");
                searchNoResult.setVisibility(View.GONE);
                searchBackWindow.setVisibility(View.VISIBLE);
                searchWindow.setVisibility(View.VISIBLE);
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_SEARCH :
                        startSearch(textView);
                        break;
                }

                return true;
            }
        });

    }

    public void closeSearch(View view){
        imm.hideSoftInputFromWindow(searchText.getWindowToken(),0);
        setTimeHandler(view);
        searchBackWindow.setVisibility(View.GONE);
        searchWindow.setVisibility(View.GONE);
    }

    public void startSearch(View view){

        if(searchText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), "검색어를 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        imm.hideSoftInputFromWindow(searchText.getWindowToken(),0);
        searchNoResult.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "북마크 검색중입니다.", Toast.LENGTH_LONG).show();
        mWebview.loadUrl("javascript:getSttResult('"+videoPath+"','"+searchText.getText()+"')");
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            System.out.println(">>>>>>>>>>onBeginningOfSpeech>>>>");
            sttExam.setVisibility(View.GONE);
            sttResult.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRmsChanged(float v) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + v);
            if(v > 9){
                mic_db.setImageResource(R.drawable.mic_09_2);
            }else if(v > 5){
                mic_db.setImageResource(R.drawable.mic_09_1);
            }else{
                mic_db.setImageResource(R.drawable.sttback);
            }
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            System.out.println(">>>>>>>>>>>>>>>>>>>>onEndOfSpeech>>>>");

        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            goAgain();
        }



        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            sttResult.setText(matches.get(0));

            Toast.makeText(VideoActivity.this, "북마크 검색중입니다.", Toast.LENGTH_SHORT).show();
            mWebview.loadUrl("javascript:getSttResult('"+videoPath+"','"+matches.get(0)+"')");

        }

        @Override
        public void onPartialResults(Bundle bundle) {

            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = matches.get(matches.size()-1);
            /*for(int i = 0; i < matches.size(); i++){
                System.out.println(">>>>>>>>>>>>>onPartialResults>>>>>>>>>>>>"+i+">>>>>>"+matches.get(i));
            }*/
            System.out.println(">>>>>>>>>>>>>onPartialResults>>>>>>>>>>>> "+word);

            sttResult.setText(word);

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    public void goAgain(){

        mic_08.setImageResource(R.drawable.sttback);
        mic_08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStt();
            }
        });
    }

    private void sidebarInit(String timeStamp){
        slidingPage01 = (LinearLayout)findViewById(R.id.slidingPage01);
        slidePageLeft = (LinearLayout)findViewById(R.id.slidePageLeft);
        scrollView = findViewById(R.id.scrollView);
        timeStampText = findViewById(R.id.timeStampText);
        /////////////////////////////

        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);

//        frag_C = new FragmentCategory();
//        frag_M = new FragmentMapping();
//
//        fm = getSupportFragmentManager();
//        tran = fm.beginTransaction();

        setAlltimeClick();

        setBookMark(timeStamp);

    }

    public void setAlltimeClick(){
        timeStampText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "전체 불러오는 중", Toast.LENGTH_LONG).show();
                mWebview.loadUrl("javascript:getAllResult('"+videoPath+"')");
                timeStampText.setOnClickListener(null);
            }
        });
    }

    private void setBookMark(String timeStamp){

        LinearLayout c_con_before = (LinearLayout)findViewById(R.id.timeStampLayer);
        c_con_before.removeAllViewsInLayout();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + timeStamp);


        try{
            JSONArray jsonObject = new JSONArray(timeStamp);

            //JSONArray color = jsonObject.getJSONArray("color");
            //Iterator categoryKeys = jsonObject.keys();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>jsonObject.length()>>>>");
            for(int i =0; i < jsonObject.length(); i++){
                JSONObject joj = jsonObject.getJSONObject(i);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+i+">>>>" + joj.getString("category"));

                FragmentCategory c_layout = new FragmentCategory(getApplicationContext());
                LinearLayout c_con = (LinearLayout)findViewById(R.id.timeStampLayer);

                TextView c_textView = c_layout.findViewById(R.id.categoryText);
                c_textView.setText(joj.getString("category"));
                c_con.addView(c_layout);


                JSONArray jar = joj.getJSONArray("subMappings");
                for(int j = 0; j < jar.length(); j++){
                    final JSONObject jmap = jar.getJSONObject(j);
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+j+">>>>" + jmap.getString("mapWord"));

                    if(jmap.getString("mapWord").equals("")){
                        FragmentMapping m_layout = new FragmentMapping(getApplicationContext());
                        LinearLayout m_con = (LinearLayout)findViewById(R.id.timeStampLayer);
                        TextView m_textView = m_layout.findViewById(R.id.mappingText);
                        m_textView.setText(joj.getString("category"));
                        m_con.addView(m_layout);

                        m_textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    mWebview.loadUrl("javascript:setTimeStamp("+jmap.getJSONArray("timeStamp")+")");
                                    setTimeHandler(view);
                                }catch (Exception e){

                                }

                            }
                        });
                    }else{
                        FragmentMapping m_layout = new FragmentMapping(getApplicationContext());
                        LinearLayout m_con = (LinearLayout)findViewById(R.id.timeStampLayer);
                        TextView m_textView = m_layout.findViewById(R.id.mappingText);
                        m_textView.setText(jmap.getString("mapWord"));
                        m_con.addView(m_layout);

                        m_textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try{
                                    mWebview.loadUrl("javascript:setTimeStamp("+jmap.getJSONArray("timeStamp")+")");
                                    setTimeHandler(view);
                                }catch (Exception e){

                                }
                            }
                        });
                    }



                }
            }

           /* FragmentTimeStamp t_layout = new FragmentTimeStamp(getApplicationContext());
            LinearLayout t_con = (LinearLayout)findViewById(R.id.timeStampLayer);
            final TextView t_textView = t_layout.findViewById(R.id.timeStampText);
            t_textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "All Click", Toast.LENGTH_LONG).show();
                }
            });
            t_con.addView(t_layout);*/



            //JSONArray categories = jsonObject.getJSONArray("category");

            /*while(categoryKeys.hasNext()){
                String category = categoryKeys.next().toString();

                FragmentCategory c_layout = new FragmentCategory(getApplicationContext());
                LinearLayout c_con = (LinearLayout)findViewById(R.id.timeStampLayer);
                TextView c_textView = c_layout.findViewById(R.id.categoryText);
                c_textView.setText(category);
                c_con.addView(c_layout);

                JSONObject jsonData = new JSONObject(jsonObject.getString(category));
                Iterator mappingKeys = jsonData.keys();


                while(mappingKeys.hasNext()){
                    String mapping = mappingKeys.next().toString();

                    FragmentMapping m_layout = new FragmentMapping(getApplicationContext());
                    LinearLayout m_con = (LinearLayout)findViewById(R.id.timeStampLayer);
                    TextView m_textView = m_layout.findViewById(R.id.mappingText);
                    m_textView.setText(mapping);
                    m_con.addView(m_layout);


                    final JSONArray jsonAry = new JSONArray(jsonData.getString(mapping));
                    for(int i = 0; i < jsonAry.length(); i++){
                        FragmentTimeStamp t_layout = new FragmentTimeStamp(getApplicationContext());
                        LinearLayout t_con = (LinearLayout)findViewById(R.id.timeStampLayer);
                        final TextView t_textView = t_layout.findViewById(R.id.timeStampText);
                        t_textView.setText((String)jsonAry.get(i));
                        t_textView.setHint((String)jsonAry.get(i));
                        t_textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mWebview.loadUrl("javascript:setTime("+t_textView.getHint()+")");
                            }
                        });
                        t_con.addView(t_layout);
                    }
                }


            }*/

        }catch (Exception e){

        }
    }



    private void initialize(){
        backPressed = findViewById(R.id.backPressed);
        videoName = findViewById(R.id.videoName);
        videoPrice = findViewById(R.id.videoPrice);
        go_stt = findViewById(R.id.go_stt);
        go_search = findViewById(R.id.go_search);
        go_btn = findViewById(R.id.go_btn);
        timeStampLayer = findViewById(R.id.timeStampLayer);
        videoName.setText(title);
        videoPrice.setText(price + "원");

    }

    public void getSttHandler(View view){
       /* if(isConnected()){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"EX> 검정색 보여줘");
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            Toast.makeText(getApplicationContext(), "Plese Connect to Internet", Toast.LENGTH_LONG).show();
        }*/


    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            videoName.setText(result.get(0));
        }

    }*/

    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    public void touchBlankHandler(View view){
        if(isPageOpen){
            setTimeHandler(view);

        }else{

        }

    }


    public void goBack(View view){
        finish();
    }

    public void setTimeHandler(View view) {

        //닫기
        if(isPageOpen){
            //애니메이션 시작
            go_stt.setEnabled(false);


            slidingPage01.startAnimation(translateRightAnim);
            go_btn.setVisibility(View.VISIBLE);
        }
        //열기
        else{
            int childCount = timeStampLayer.getChildCount();
            for(int i = 0; i<childCount;i++){
                View v = timeStampLayer.getChildAt(i);
                v.setVisibility(View.VISIBLE);
            }

            go_stt.setEnabled(true);
            slidePageLeft.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            timeStampLayer.setVisibility(View.VISIBLE);
            timeStampText.setVisibility(View.VISIBLE);
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage01.startAnimation(translateLeftAnim);


        }

        //mWebview.loadUrl("javascript:setTime(100)");
    }


    //애니메이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if(isPageOpen){
                int childCount = timeStampLayer.getChildCount();
                for(int i = 0; i<childCount;i++){
                    View v = timeStampLayer.getChildAt(i);
                    v.setVisibility(View.GONE);
                }

                scrollView.setVisibility(View.GONE);
                timeStampLayer.setVisibility(View.GONE);
                timeStampText.setVisibility(View.GONE);
                slidingPage01.setVisibility(View.GONE);
                slidePageLeft.setVisibility(View.GONE);

                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else{
                go_btn.setVisibility(View.GONE);
                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {

        }
    }


    private void webviewSet(String videoName) {
        ActionBar actionBar = getSupportActionBar();

        mWebview = (WebView)findViewById(R.id.webview);
        mWebview.loadUrl("http://10.149.179.116:8088/video.sst?videoName="+videoName); //93
        //////////// brower load
        mWebview.setWebViewClient(new WebViewClientClass());
        ////////////script
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setSupportZoom(false); /////
        mWebview.getSettings().setBuiltInZoomControls(false);/////
        mWebview.getSettings().setDisplayZoomControls(false);
        //////
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);

        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebview.invalidate();
            }
        });


        ////
        mWebview.addJavascriptInterface(new AndroidBridge(), "goStt");


        //// window.alert
        mWebview.setWebChromeClient(new WebChromeClient(){  //FullscreenableChromeClient(VideoActivity.this, actionBar
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(VideoActivity.this)
                        .setTitle("dialog")
                        .setMessage(message)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
    }

    public class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    // web->android , android->web
    public class AndroidBridge {
        @JavascriptInterface
        public void gettSttResult(final String timeStamp){

            handler.post(new Runnable() {
                public void run() {
                    if(timeStamp.equals("[]")){

                        if(searchWindow.getVisibility() == View.VISIBLE){
                            searchNoResult.setVisibility(View.VISIBLE);
                            setAlltimeClick();
                        }else if(sttWindow.getVisibility() == View.VISIBLE){
                            sttNoResult.setVisibility(View.VISIBLE);
                            setAlltimeClick();
                            goAgain();
                        }else{

                        }


                        return ;
                    }else{
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + timeStamp);
                        if(searchWindow.getVisibility() == View.VISIBLE) {

                            setBookMark(timeStamp);
                            setTimeHandler(slidingPage01);
                            searchBackWindow.setVisibility(View.GONE);
                            searchWindow.setVisibility(View.GONE);
                            searchNoResult.setVisibility(View.GONE);
                            setAlltimeClick();
                        }else if(sttWindow.getVisibility() == View.VISIBLE){
                            setBookMark(timeStamp);
                            setTimeHandler(slidingPage01);
                            sttBackWindow.setVisibility(View.GONE);
                            sttWindow.setVisibility(View.GONE);
                            sttExam.setVisibility(View.VISIBLE);
                            sttNoResult.setVisibility(View.GONE);
                            sttResult.setText("");
                            sttResult.setVisibility(View.GONE);
                            setAlltimeClick();
                        }else{
                            setBookMark(timeStamp);
                            timeStampText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(VideoActivity.this, "전체보기 상태입니다!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }
            });
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && isPageOpen == true){
            setTimeHandler(sttNoResult);
            return true ;
        }else if((keyCode == KeyEvent.KEYCODE_BACK) && sttWindow.getVisibility() == View.VISIBLE){
            closeStt(sttNoResult);
            return true;
        }else if((keyCode == KeyEvent.KEYCODE_BACK) && searchWindow.getVisibility() == View.VISIBLE){
            closeSearch(sttNoResult);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
