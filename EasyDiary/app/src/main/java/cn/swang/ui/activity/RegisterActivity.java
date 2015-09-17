package cn.swang.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.rey.material.widget.Button;

import cn.swang.R;
import cn.swang.app.IConstants;
import cn.swang.entity.BackMessage;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.GsonRequest;
import cn.swang.utils.RequestManager;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends BaseActivity {

    private TextInputLayout mNameView;
    private TextInputLayout mPasswordView, mPasswordView2;
    private View mProgressView;
    private final String REGISTER_URL = IConstants.WEBCONTEXT+"/register"+IConstants.REQUEST_SUFFIX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        mPasswordView2 = (TextInputLayout) findViewById(R.id.til_pwd);
        mPasswordView2.setHint(getString(R.string.prompt_password));
        mPasswordView2.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mPasswordView.getEditText().getText().equals(s)) {
                    mNameView.setError("Passwords are difference");
                    mNameView.setErrorEnabled(true);
                } else {
                    mNameView.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button registerButton = (Button) findViewById(R.id.email_sign_in_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //register


                Response.Listener<String> listener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                };
                executeHttpRequest(new StringRequest(Request.Method.GET, "http://www.baidu.com", listener, errorListener()));
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
                Toast.makeText(this,"register",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

