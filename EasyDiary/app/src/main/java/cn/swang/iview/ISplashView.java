package cn.swang.iview;

import android.view.animation.Animation;

public interface ISplashView {

    void animateBackgroundImage(Animation animation);

    void initializeViews(String versionName, String copyright, int backgroundResId);

    void navigateToHomePage();
}
