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
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.swang.R;
import cn.swang.app.IConstants;
import cn.swang.entity.BackMessage;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.GsonRequest;
import cn.swang.utils.ParamsHashMap;
import cn.swang.utils.RequestManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private final String LOGIN_URL = IConstants.WEBCONTEXT+"/login/byphone"+IConstants.REQUEST_SUFFIX;

    private TextInputLayout mNameView;
    private TextInputLayout mPasswordView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mNameView=(TextInputLayout)findViewById(R.id.til_name);
        mNameView.setHint(getString(R.string.prompt_name));
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
                StringRequest stringRequest = new StringRequest(Request.Method.POST,LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(LoginActivity.this,response,Toast.LENGTH_SHORT).show();
                            }
                        }, errorListener()) {
                    @Override
                    protected Map<String, String> getParams() {
                        return new ParamsHashMap().with("phone",mNameView.getEditText().getText().toString())
                                .with("pass",mPasswordView.getEditText().getText().toString());
                    }
                };
                executeHttpRequest(stringRequest);
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
                startActivityByMyself(RegisterActivity.class,true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

