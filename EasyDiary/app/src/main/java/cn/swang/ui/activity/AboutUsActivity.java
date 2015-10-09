package cn.swang.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import cn.swang.R;
import cn.swang.ui.base.BaseActivity;

/**
 * A login screen that offers login via email/password.
 */
public class AboutUsActivity extends BaseActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mTextView=(TextView)findViewById(R.id.about_us_tv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
//            case R.id.id_feedback_send:
//                //
//
//                finish();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

