package cn.swang.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.ImageLoaderHelper;
import cn.swang.utils.ShareBitmapUtils;

/**
 * Created by sw on 2015/9/13.
 */
public class ShareDayCardActivity extends BaseActivity {

    public static final String SHARE_IMAGE_PATH = "share_image_path";
    private ImageView mImageView;
    private ScrollView mScrollView;
    private LinearLayout mBottomView;
    private String imageUrl;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation = AnimationUtils.loadAnimation(ShareDayCardActivity.this, R.anim.right_in);
            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.startAnimation(animation);
        }
    };
    private static final int START_ANIMATION_MSG=111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.scale_in2, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_daycard);
        mScrollView = (ScrollView) findViewById(R.id.share_daycard_scroll_layout);
        mImageView = (ImageView) findViewById(R.id.share_daycard_image);
        mBottomView = (LinearLayout)findViewById(R.id.share_bottom_layout);
        imageUrl = getIntent().getStringExtra(SHARE_IMAGE_PATH);
        if (TextUtils.isEmpty(imageUrl)) {
            finish();
            return;
        }
        //String url=ImageDownloader.Scheme.FILE.wrap(imageUrl);
        //ImageLoader.getInstance().displayImage(url, mImageView);

        //max=4096*4096
        int imgMaxWidth = 600;
        int imgMaxHeight = 4000;
        Bitmap bitmap=decodeSampledBitmapFromFile(imageUrl, 600, 3000);
        mImageView.setImageBitmap(bitmap);

        handler.sendEmptyMessageDelayed(START_ANIMATION_MSG, 600);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        if(height>3000){
            Toast.makeText(this,getString(R.string.about_fast_diary_tip1),Toast.LENGTH_SHORT).show();
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            double a= Math.ceil((float) height / (float) reqHeight);;
            double b= Math.ceil((float) width / (float) reqWidth);
            inSampleSize =(int)Math.max(a,b);
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    public void click_confirm_fab(View v){
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f = new File(imageUrl);
        if (f != null && f.exists() && f.isFile()) {
            intent.setType("image/jpg");
            Uri u = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM, u);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "请选择"));
        }
        //finish();
    }


    private static final int SHARE_SETTING_REQUEST_CODE = 3456;

    public void click_setting_fab(View v){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivityForResult(intent,SHARE_SETTING_REQUEST_CODE);
    }
    public void click_cancel_fab(View v){
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        //delete diary file
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File f=new File(imageUrl);
                    if(f.exists()){
                        f.delete();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        overridePendingTransition(0, R.anim.scale_out);
    }

    @Override
    protected boolean isApplyKitKatTranslucency() {
        return false;
    }

    @Override
    protected boolean useActivityAnimation() {
        return false;
    }

}
