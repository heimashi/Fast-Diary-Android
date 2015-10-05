package cn.swang.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import cn.swang.R;
import cn.swang.ui.base.BaseActivity;

/**
 * A login screen that offers login via email/password.
 */
public class SettingsActivity extends BaseActivity {

    private EditText mEditText;
    private TextInputLayout mTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mTextInputLayout = (TextInputLayout)findViewById(R.id.feedback_text_input_layout);
        mTextInputLayout.setHint(getString(R.string.feedback_tint));
        mEditText=(EditText)findViewById(R.id.feedback_edit_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_feedback_send:
                //

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

