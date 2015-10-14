package cn.swang.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.umeng.fb.FeedbackAgent;

import java.util.Map;

import cn.swang.R;
import cn.swang.app.IConstants;
import cn.swang.entity.BackMessage;
import cn.swang.entity.NoteCard;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.CommonUtils;
import cn.swang.utils.ParamsHashMap;

/**
 * A login screen that offers login via email/password.
 */
public class FeedbackActivity extends BaseActivity {

    private EditText mEditText;
    private TextInputLayout mTextInputLayout;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        if(isLessThanKitkat){
            LinearLayout root = (LinearLayout)findViewById(R.id.root_view);
            root.setPadding(0,0,0,0);
        }
        mTextInputLayout = (TextInputLayout)findViewById(R.id.feedback_text_input_layout);
        mTextInputLayout.setHint(getString(R.string.feedback_tint));
        mEditText=(EditText)findViewById(R.id.feedback_edit_text);
        mTextView = (TextView)findViewById(R.id.umeng_feedback);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackAgent agent = new FeedbackAgent(FeedbackActivity.this);
                agent.startFeedbackActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    private final String FEEDBACK_URL = IConstants.WEBCONTEXT_SAE+"feedback/save"+IConstants.REQUEST_SUFFIX;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_feedback_send:
                final String msg = mEditText.getText().toString().trim();
                if(TextUtils.isEmpty(msg)){
                    Toast.makeText(FeedbackActivity.this, getString(R.string.empty_tips), Toast.LENGTH_SHORT).show();
                    return true;
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST,FEEDBACK_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(TextUtils.isEmpty(response)){
                                    Toast.makeText(FeedbackActivity.this, getString(R.string.error_tips), Toast.LENGTH_SHORT).show();
                                }else {
                                    Gson gson=new Gson();
                                    BackMessage backMessage = gson.fromJson(response, BackMessage.class);
                                    if(backMessage.isState()&&!TextUtils.isEmpty(backMessage.getMessage().toString())){
                                        Toast.makeText(FeedbackActivity.this, backMessage.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(FeedbackActivity.this, getString(R.string.error_tips), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }, errorListener()) {
                    @Override
                    protected Map<String, String> getParams() {
                        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        WifiInfo info = wifi.getConnectionInfo();
                        return new ParamsHashMap().with("mac",info.getMacAddress())
                                .with("message",msg);
                    }
                };
                executeHttpRequest(stringRequest);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

