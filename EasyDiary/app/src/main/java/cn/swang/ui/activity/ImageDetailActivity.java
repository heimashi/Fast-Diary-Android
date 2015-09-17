package cn.swang.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import cn.swang.R;
import cn.swang.ui.base.BaseActivity;

/**
 * Created by sw on 2015/9/13.
 */
public class ImageDetailActivity extends BaseActivity {

    public static final String DETAIL_IMAGE_PATH = "detail_image_path";
    ImageView mImageView;
    String imageUrl;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.scale_in2,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_layout);
        linearLayout = (LinearLayout)findViewById(R.id.images_detail_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageUrl = getIntent().getStringExtra(DETAIL_IMAGE_PATH);
        if(TextUtils.isEmpty(imageUrl)) {
            finish();
            return;
        }
        mImageView=(ImageView)findViewById(R.id.images_detail_iv);
        ImageLoader.getInstance().displayImage(imageUrl, mImageView);
    }

    @Override
    public void finish() {
        super.finish();
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
