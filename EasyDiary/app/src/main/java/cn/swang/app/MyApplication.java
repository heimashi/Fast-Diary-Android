package cn.swang.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.swang.thread.ThreadPoolManager;
import cn.swang.utils.ImageLoaderHelper;
import cn.swang.utils.RequestManager;

/**
 * Created by sw on 2015/9/9.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalData.initialize(getApplicationContext());
        ThreadPoolManager.init();
        RequestManager.init(this);
        ImageLoader.getInstance().init(ImageLoaderHelper.getInstance(this).getImageLoaderConfiguration(IConstants.IMAGE_LOADER_CACHE_PATH));
    }


}
