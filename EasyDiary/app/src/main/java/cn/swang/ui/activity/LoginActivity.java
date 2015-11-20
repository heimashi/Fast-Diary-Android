package cn.swang.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rey.material.app.ToolbarManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.swang.R;
import cn.swang.app.IConstants;
import cn.swang.entity.BackMessage;
import cn.swang.ui.base.BaseActivity;
import cn.swang.ui.widget.Button;
import cn.swang.ui.widget.TestView;
import cn.swang.utils.GsonRequest;
import cn.swang.utils.ParamsHashMap;
import cn.swang.utils.RequestManager;


import android.os.Bundle;
import android.view.View;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private final String LOGIN_URL = IConstants.WEBCONTEXT+"/login/byphone"+IConstants.REQUEST_SUFFIX;

    private TextInputLayout mNameView;
    private TextInputLayout mPasswordView;
    private View mProgressView;
    private static final String WEIXIN_APP_ID = "wxe8f896d74f27dfd9";
    private static final String secretKey = "d4624c36b6795d1d99dcf0547af5443d";

    private IWXAPI api;

    private void registerToWx(){
        api = WXAPIFactory.createWXAPI(this,WEIXIN_APP_ID,true);
        api.registerApp(WEIXIN_APP_ID);
    }

    private void loginby(){
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    private void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "这里填写标题";
        msg.description = "这里填写内容";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.adj);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession: SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }


    //在需要分享的地方添加代码：
    //wechatShare(0);//分享到微信好友
    //wechatShare(1);//分享到微信朋友圈

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerToWx();
        mNameView=(TextInputLayout)findViewById(R.id.til_name);
        mNameView.setHint(getString(R.string.prompt_name));
        test();
        mNameView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    mNameView.setError("It's too short");
                    mNameView.setErrorEnabled(true);
                } else {
                    mNameView.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPasswordView = (TextInputLayout) findViewById(R.id.til_pwd);
        mPasswordView.setHint(getString(R.string.prompt_password));
        mPasswordView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    mNameView.setError("Password error");
                    mNameView.setErrorEnabled(true);
                } else {
                    mNameView.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //wechatShare(1);
                loginby();
//                clickBtn();
//                StringRequest stringRequest = new StringRequest(Request.Method.POST,LOGIN_URL,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Toast.makeText(LoginActivity.this,response,Toast.LENGTH_SHORT).show();
//                            }
//                        }, errorListener()) {
//                    @Override
//                    protected Map<String, String> getParams() {
//                        return new ParamsHashMap().with("phone",mNameView.getEditText().getText().toString())
//                                .with("pass",mPasswordView.getEditText().getText().toString());
//                    }
//                };
//                executeHttpRequest(stringRequest);
//                GsonRequest<BackMessage> gsonRequest = new GsonRequest<BackMessage>(
//                        Request.Method.POST,
//                        LOGIN_URL,
//                        null,
//                        new TypeToken<BackMessage>() {
//                        }.getType(),
//                        new Response.Listener<BackMessage>() {
//                            @Override
//                            public void onResponse(BackMessage response) {
//                                if(response.isState()){
//                                    finish();
//                                }else{
//                                    Toast.makeText(LoginActivity.this,response.getMessage().toString(),Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        },
//                        errorListener()
//                ){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        return new ParamsHashMap().with("phone",mNameView.getEditText().getText().toString())
//                                .with("pass",mPasswordView.getEditText().getText().toString());
//                    }
//                };
//
//                gsonRequest.setShouldCache(true);
//                RequestManager.getRequestQueue().add(gsonRequest);
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        final String TAG = "test";
        TestView testView = (TestView)findViewById(R.id.test_view);
        testView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();

                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "onTouch ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e(TAG, "onTouch ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "onTouch ACTION_UP");
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
        testView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "setOnClickListener");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_register:
                wechatShare(1);
                //startActivityByMyself(RegisterActivity.class,true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler ui_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    void test(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                ui_handler.sendEmptyMessage(1);

            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                super.run();
                String name = Thread.currentThread().getName();
                Looper.prepare();
                int a = 1+2;
                int b = 3;



                final Handler t_handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                    }
                };
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String name = Thread.currentThread().getName();
                        t_handler.sendEmptyMessage(11);

                    }
                }.start();

                Looper.loop();
            }
        }.start();

    }
}

