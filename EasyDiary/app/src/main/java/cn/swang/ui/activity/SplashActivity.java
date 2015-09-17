package cn.swang.ui.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.swang.R;
import cn.swang.iview.ISplashView;
import cn.swang.presenter.BasePresenter;
import cn.swang.presenter.SplashPresenter;
import cn.swang.ui.base.BaseActivity;

/**
 * Created by sw on 2015/9/7.
 */
public class SplashActivity extends BaseActivity implements ISplashView {

    @Bind(R.id.splash_image)
    ImageView mSplashImage;

    @Bind(R.id.splash_version_name)
    TextView mVersionName;

    @Bind(R.id.splash_copyright)
    TextView mCopyright;

    private BasePresenter mSplashPresenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mSplashPresenter = new SplashPresenter(this, this);
        mSplashPresenter.initialized();
    }

    @Override
    public void initializeViews(String versionName, String copyright, int backgroundResId) {
        mCopyright.setText(copyright);
        mVersionName.setText(versionName);
        mSplashImage.setImageResource(backgroundResId);
    }

    @Override
    public void animateBackgroundImage(Animation animation) {
        mSplashImage.startAnimation(animation);
    }

    @Override
    protected boolean isApplyKitKatTranslucency() {
        return false;
    }

    @Override
    public void navigateToHomePage() {
        startActivityByMyself(HomeActivity.class,true);
    }
}
