package cn.swang.ui.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import cn.swang.R;
import cn.swang.utils.RequestManager;
import cn.swang.utils.SmartBarUtils;

/**
 * Created by sw on 2015/9/7.
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected String LOG_TAG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(useActivityAnimation()){
            overridePendingTransition(R.anim.right_in,R.anim.left_out);
        }
        super.onCreate(savedInstanceState);
        LOG_TAG = this.getClass().getSimpleName();

        //SmartBarUtils.hide(getWindow().getDecorView());
        setTranslucentStatus(isApplyStatusBarTranslucency());
        if (isApplyKitKatTranslucency()) {
            setSystemBarTintDrawable(getResources().getDrawable(R.drawable.sr_primary));
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = ButterKnife.findById(this, R.id.toolbar);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected boolean isApplyKitKatTranslucency() {
        return true;
    }


    /**
     * use SytemBarTintManager
     *
     * @param tintDrawable
     */
    protected void setSystemBarTintDrawable(Drawable tintDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("test","setSystemBarTintDrawable:"+Build.VERSION.SDK_INT);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            if (tintDrawable != null) {
                mTintManager.setStatusBarTintEnabled(true);
                mTintManager.setTintDrawable(tintDrawable);
            } else {
                mTintManager.setStatusBarTintEnabled(false);
                mTintManager.setTintDrawable(null);
            }
        }

    }


    protected boolean isApplyStatusBarTranslucency(){
        return true;
    };


    protected boolean isLessThanKitkat = true;

    /**
     * set status bar translucency
     *
     * @param on
     */
    protected void setTranslucentStatus(boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isLessThanKitkat = false;
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    protected void startActivityByMyself(Class<?> clazz,boolean isfinish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if(isfinish) finish();
    }

    protected void executeHttpRequest(Request<?> request) {
        RequestManager.addRequest(request, LOG_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestManager.cancelAll(LOG_TAG);

    }

    @Override
    public void finish() {
        super.finish();
        if(useActivityAnimation()){
            overridePendingTransition(0,R.anim.scale_out);
        }
    }

    protected boolean useActivityAnimation() {
        return true;
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BaseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }
}
