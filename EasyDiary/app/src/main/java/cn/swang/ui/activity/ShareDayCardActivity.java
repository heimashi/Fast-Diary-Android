package cn.swang.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

/**
 * Created by sw on 2015/9/13.
 */
public class ShareDayCardActivity extends BaseActivity {

    public static final String SHARE_IMAGE_PATH = "share_image_path";
    private ImageView mImageView;
    private ScrollView mScrollView;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_daycard);
        mScrollView = (ScrollView) findViewById(R.id.share_daycard_scroll_layout);
        mImageView = (ImageView) findViewById(R.id.share_daycard_image);

        imageUrl = getIntent().getStringExtra(SHARE_IMAGE_PATH);
        if (TextUtils.isEmpty(imageUrl)) {
            finish();
            return;
        }
        //String url=ImageDownloader.Scheme.FILE.wrap(imageUrl);
        //ImageLoader.getInstance().displayImage(url, mImageView);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(imageUrl));
//        ImageLoader.getInstance().displayImage(url, mImageView, null, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                super.onLoadingComplete(imageUri, view, loadedImage);
//                Matrix matrix = new Matrix();
//                matrix.postScale(1.8f, 1.8f);
//                Bitmap resizeBmp = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
//                mImageView.setImageBitmap(resizeBmp);
//            }
//        });

//        Bitmap bitmap = ;
//        Matrix matrix = new Matrix();
//        matrix.postScale(1.5f, 1.5f);
//        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        //

//        int mheight = mScrollView.getLayoutParams().height;
//        int mwidth = mScrollView.getLayoutParams().width;
//        //PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("width", 2 * width);
//        //PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("height",2 * height);
//        //ObjectAnimator.ofPropertyValuesHolder(mScrollView,pvhX,pvhY).setDuration(1000).start();
//        //ObjectAnimator.ofPropertyValuesHolder(wrapper, pvhX, pvhY).setDuration(2000).start();
//        ViewWrapper wrapper = new ViewWrapper(mScrollView);
//        ObjectAnimator oA1=ObjectAnimator.ofInt(wrapper, "width", GlobalData.screenWidth).setDuration(2000);
//        ViewWrapper wrapper2 = new ViewWrapper(mImageView);
//        ObjectAnimator oA2=ObjectAnimator.ofInt(wrapper, "width", GlobalData.screenWidth).setDuration(2000);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(oA1).with(oA2);
//        animatorSet.start();
    }

    private static class ViewWrapper {
        private View target;

        public ViewWrapper(View target) {
            this.target = target;
        }

        public int getWidth() {
            return target.getLayoutParams().width;
        }

        public void setWidth(int width) {
            target.getLayoutParams().width = width;
            target.requestLayout();
        }

        public int getHeight() {
            return target.getLayoutParams().height;
        }

        public void setHeight(int height) {
            target.getLayoutParams().height = height;
            target.requestLayout();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_daycard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_confirm_btn:
                Intent intent = new Intent(Intent.ACTION_SEND);
                File f = new File(imageUrl);
                if (f != null && f.exists() && f.isFile()) {
                    intent.setType("image/jpg");
                    Uri u = Uri.fromFile(f);
                    intent.putExtra(Intent.EXTRA_STREAM, u);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, "请选择"));
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
