package cn.swang.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Slider;

import cn.swang.R;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.ShareBitmapUtils;

/**
 * A login screen that offers login via email/password.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener{


    private TextInputLayout mTitleLayout;
    private EditText mTitleEt;
    private CheckBox mDateTitleCb, mAppCb;
    private Slider mImgSizeSlider;
    private SharedPreferences mSharePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mTitleLayout=(TextInputLayout)findViewById(R.id.setting_share_title);
        mTitleLayout.setHint(getString(R.string.settings_share_picture_add_title_hint));
        mTitleEt = (EditText)findViewById(R.id.share_add_title_edittext);
        mDateTitleCb = (CheckBox)findViewById(R.id.share_title_cb);
        mAppCb = (CheckBox)findViewById(R.id.share_app_tag_cb);
        mImgSizeSlider = (Slider)findViewById(R.id.share_pic_size_slider);
        mSharePreference = getSharedPreferences(ShareBitmapUtils.SHARE_DIARY_SHARE_PREFERENCE, Context.MODE_PRIVATE);
        boolean flag=mSharePreference.getBoolean(ShareBitmapUtils.IS_SHOW_TITLE_DATE,true);
        mDateTitleCb.setChecked(flag);
        if(flag){
            mDateTitleCb.setText(getString(R.string.settings_is_showing));
        }else {
            mDateTitleCb.setText(getString(R.string.settings_is_not_showing));
        }
        mDateTitleCb.setOnClickListener(this);
        boolean appFlag=mSharePreference.getBoolean(ShareBitmapUtils.IS_SHOW_END_TAG,true);
        mAppCb.setChecked(appFlag);
        if(appFlag){
            mAppCb.setText(getString(R.string.settings_is_showing));
        }else {
            mAppCb.setText(getString(R.string.settings_is_not_showing));
        }
        mAppCb.setOnClickListener(this);
        //
        float img_size = mSharePreference.getFloat(ShareBitmapUtils.SHOW_IMAGE_SIZE,100f);
        mImgSizeSlider.setValue(img_size, true);
        mImgSizeSlider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, boolean b, float v, float v1, int i, int i1) {
                try{
                    mSharePreference.edit().putFloat(ShareBitmapUtils.SHOW_IMAGE_SIZE,mImgSizeSlider.getValue()).commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_title_cb:
                mSharePreference.edit().putBoolean(ShareBitmapUtils.IS_SHOW_TITLE_DATE,mDateTitleCb.isChecked()).commit();
                if(mDateTitleCb.isChecked()){
                    mDateTitleCb.setText(getString(R.string.settings_is_showing));
                }else {
                    mDateTitleCb.setText(getString(R.string.settings_is_not_showing));
                }
                break;
            case R.id.share_app_tag_cb:
                mSharePreference.edit().putBoolean(ShareBitmapUtils.IS_SHOW_END_TAG,mAppCb.isChecked()).commit();
                if(mAppCb.isChecked()){
                    mAppCb.setText(getString(R.string.settings_is_showing));
                }else {
                    mAppCb.setText(getString(R.string.settings_is_not_showing));
                }
                break;
        }
    }
}

