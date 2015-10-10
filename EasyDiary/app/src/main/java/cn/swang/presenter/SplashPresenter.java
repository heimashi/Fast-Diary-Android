package cn.swang.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.app.IConstants;
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
                0);//android:icon="@mipmap/ic_action_scan"
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

    DbService dbService=null;
    private void initData(final Context mContext){
        dbService=new DbService(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(GlobalData.app().getSharedPreferences(INIT_DIARY_DATA_PREFERENCE, Context.MODE_PRIVATE).getBoolean(INIT_DIARY_DATA_PREFERENCE_STRING,true)){
                    GlobalData.app().getSharedPreferences(INIT_DIARY_DATA_PREFERENCE,Context.MODE_PRIVATE).edit().putBoolean(INIT_DIARY_DATA_PREFERENCE_STRING, false).commit();
                    String filePath1=null,filePath2=null;
                    try {
                        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fast_diary_img1);
                        Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fast_diary_img2);
                        filePath1 = saveBitmapToSDCard(bitmap1, "diary_img1");
                        filePath2 = saveBitmapToSDCard(bitmap2,"diary_img2");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //day1
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.YEAR, 2015);
                    cal.set(Calendar.MONTH, 9);
                    cal.set(Calendar.DAY_OF_MONTH, 3);
                    Date date1 = cal.getTime();
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip6), date1);
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip8), date1);

                    //day2
                    cal.set(Calendar.DAY_OF_MONTH, 2);
                    Date date2 = cal.getTime();
                    if(filePath1!=null){
                        sendPictureNoteMsg(filePath1, date2);
                    }
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip7), date2);
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip2), date2);
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip1), date2);
                    if(filePath2!=null){
                        sendPictureNoteMsg(filePath2, date2);
                    }
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip4), date2);
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip3), date2);

                    //day3
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date date3 = cal.getTime();
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip5),date3);
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip7), date3);
                    sendTextNoteMsg(mContext.getString(R.string.about_fast_diary_tip9), date3);
                    if(filePath1!=null){
                        sendPictureNoteMsg(filePath1,date3);
                    }
                }
            }
        }).start();

    }

    public String  saveBitmapToSDCard(Bitmap mBitmap,String fileName)  {
        File dir = new File(Environment.getExternalStorageDirectory()+ IConstants.SHARE_PHOTO_PATH);
        if(!dir.exists()){
            dir.mkdir();
        }
        File f = new File( Environment.getExternalStorageDirectory()+ IConstants.SHARE_PHOTO_PATH+fileName + ".jpg");
        String filePath= f.getAbsolutePath();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private void sendTextNoteMsg(String content, Date date) {
        NoteCard noteCard = new NoteCard();
        noteCard.setContent(content);
        noteCard.setDate(date);
        noteCard.setDay_id(-1);
        dbService.saveNote(noteCard);
    }

    private void sendPictureNoteMsg(final String filePath,Date date) {
        NoteCard noteCard = new NoteCard();
        noteCard.setImgPath(filePath);
        noteCard.setDate(date);
        noteCard.setDay_id(-1);
        dbService.saveNote(noteCard);
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
