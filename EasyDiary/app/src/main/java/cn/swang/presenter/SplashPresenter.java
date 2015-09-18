package cn.swang.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Calendar;
import java.util.Date;

import cn.swang.R;
import cn.swang.dao.DbService;
import cn.swang.entity.NoteCard;
import cn.swang.iview.ISplashView;

public class SplashPresenter implements BasePresenter {

    private Context mContext = null;
    private ISplashView mSplashView = null;
    public static final String INIT_DIARY_DATA_PREFERENCE = "init_diary_data_pref";
    public static final String INIT_DIARY_DATA_PREFERENCE_STRING = "init_diary_data_preference_string";

    public SplashPresenter(Context context, ISplashView splashView) {
        if (null == splashView) {
            throw new IllegalArgumentException("Constructor's parameters must not be Null");
        }
        mContext = context;
        mSplashView = splashView;
    }

    @Override
    public void initialized() {
        mSplashView.initializeViews(getVersionName(mContext),
                getCopyright(mContext),
                getBackgroundImageResID());
        initData(mContext);
        mSplashView.navigateToHomePage();
//        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.splash);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mSplashView.navigateToHomePage();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        mSplashView.animateBackgroundImage(animation);
    }

    private void initData(Context mContext){
        if(TextUtils.isEmpty(mContext.getSharedPreferences(INIT_DIARY_DATA_PREFERENCE,Context.MODE_PRIVATE).getString(INIT_DIARY_DATA_PREFERENCE_STRING,""))){
            mContext.getSharedPreferences(INIT_DIARY_DATA_PREFERENCE,Context.MODE_PRIVATE).edit().putString(INIT_DIARY_DATA_PREFERENCE_STRING,"init").commit();
            DbService dbService=new DbService(mContext);
            for(int i=0; i<3; i++){
                NoteCard noteCard = new NoteCard();
                Date date = new Date();
                Date date1 = new Date(date.getTime() + 1 * 24 * 60 * 60 * 1000);
                noteCard.setDate(date1);
                noteCard.setContent(i+"中午发士大夫电风扇分厘卡似的咖啡碱多少了");
                dbService.saveNote(noteCard,null);
            }
            for(int i=0; i<4; i++){
                NoteCard noteCard = new NoteCard();
                Date date = new Date();
                Date date1 = new Date(date.getTime() -6 * 24 * 60 * 60 * 1000);
                noteCard.setDate(date1);
                noteCard.setContent(i + "zhesdfsfsdfsfkflsdlsff sdfjsdlffsljf");
                dbService.saveNote(noteCard,null);
            }
            for(int i=0; i<1; i++){
                NoteCard noteCard = new NoteCard();
                Date date = new Date();
                Date date1 = new Date(date.getTime() - 7 * 24 * 60 * 60 * 1000);
                noteCard.setDate(date1);
                noteCard.setContent(i+"zhesdfsfsdfsfkflsdlsff sdfjsdlffsljf");
                dbService.saveNote(noteCard,null);
            }
            for(int i=0; i<3; i++){
                NoteCard noteCard = new NoteCard();
                Date date = new Date();
                Date date1 = new Date(date.getTime() - 8 * 24 * 60 * 60 * 1000);
                noteCard.setDate(date1);
                noteCard.setContent(i+"zhesdfsfsdfsfkflsdlsff sdfjsdlffsljf");
                dbService.saveNote(noteCard,null);
            }
        }
    }

    public int getBackgroundImageResID() {
        int resId;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour <= 12) {
            resId = R.mipmap.morning;
        } else if (hour > 12 && hour <= 18) {
            resId = R.drawable.afternoon;
        } else {
            resId = R.mipmap.night;
        }
        return resId;
    }

    public String getVersionName(Context context) {
        String versionName = null;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.format(context.getResources().getString(R.string.splash_version), versionName);
    }

    public String getCopyright(Context context) {
        return context.getResources().getString(R.string.splash_copyright);
    }
}
