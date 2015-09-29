package cn.swang.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.rey.material.widget.Button;

import java.util.Map;

import cn.swang.R;
import cn.swang.app.IConstants;
import cn.swang.entity.NoteCard;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.CommonUtils;
import cn.swang.utils.ParamsHashMap;

/**
 * A login screen that offers login via email/password.
 */
public class LongDiaryActivity extends BaseActivity {

    public static final String LONG_DIARY_EXTRA_STRING = "long_diary_extra_string";
    public static final String UPDATE_DIARY_EXTRA_STRING = "update_diary_extra_string";
    public static final String UPDATED_NEW_DIARY_EXTRA_STRING = "updated_new_diary_extra_string";
    private EditText mEditText;
    private NoteCard mNoteCard=null;
    private boolean isUpdateDiaryState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_diary);
        mEditText=(EditText)findViewById(R.id.input_diary_multi_edit_text);
        mNoteCard=(NoteCard)getIntent().getSerializableExtra(UPDATE_DIARY_EXTRA_STRING);
        if(mNoteCard!=null){
            isUpdateDiaryState = true;
            mEditText.setText(mNoteCard.getContent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_long_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_save_long_diary:
                CommonUtils.hideKeyboard(mEditText);
                String tmp = mEditText.getText().toString();
                if(TextUtils.isEmpty(tmp)){
                    Snackbar.make(mEditText, getString(R.string.note_content_can_not_be_empty), Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent();
                if(isUpdateDiaryState){
                    mNoteCard.setContent(tmp);
                    intent.putExtra(UPDATED_NEW_DIARY_EXTRA_STRING,mNoteCard);
                }else {
                    intent.putExtra(LONG_DIARY_EXTRA_STRING,tmp);
                }
                setResult(RESULT_OK,intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

