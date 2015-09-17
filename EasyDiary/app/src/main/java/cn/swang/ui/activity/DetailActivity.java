package cn.swang.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import cn.swang.R;
import cn.swang.entity.DayCard;
import cn.swang.ui.adapter.DetailRecyclerViewAdapter;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.adapter.StaggeredAdapter;
import cn.swang.ui.base.BaseActivity;
import cn.swang.utils.AudioManager;
import cn.swang.utils.ImageLoaderHelper;
import cn.swang.utils.MediaManager;

public class DetailActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ImageView mDayCardImageView;
    private DetailRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        DayCard dayCard = (DayCard) getIntent().getSerializableExtra(StaggeredAdapter.INTENT_DAYCARD_EXTRAS);
        if(dayCard==null) {
            finish();
            return;
        }
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        String SHOW_TIME = dayCard.getYear()+"/"+dayCard.getMouth()+"/"+dayCard.getDay();
        collapsingToolbar.setTitle(SHOW_TIME);

        mRecyclerView = (RecyclerView)findViewById(R.id.detail_recycler_view);
        mDayCardImageView = (ImageView)findViewById(R.id.detail_daycard_iv);
        if(!TextUtils.isEmpty(dayCard.getDayImagePath())){
            String imageUri = ImageDownloader.Scheme.FILE.wrap(dayCard.getDayImagePath());
            ImageLoader.getInstance().displayImage(imageUri,mDayCardImageView);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter=new DetailRecyclerViewAdapter(DetailActivity.this,dayCard.getNoteSet());
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.getInstance().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.getInstance().release();
    }

    public void checkin(View view) {
        adapter.notifyDataSetChanged();
        Snackbar.make(view, "checkin success!", Snackbar.LENGTH_SHORT).show();
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

    /*
    * <!--<android.support.v4.widget.NestedScrollView-->
    <!--android:layout_width="match_parent"-->
    <!--android:layout_height="match_parent"-->
    <!--app:layout_behavior="@string/appbar_scrolling_view_behavior">-->

    <!--<LinearLayout-->
    <!--android:layout_width="match_parent"-->
    <!--android:layout_height="match_parent"-->
    <!--android:orientation="vertical"-->
    <!--android:paddingTop="24dp">-->

    <!--<TextView-->
    <!--android:layout_width="match_parent"-->
    <!--android:layout_height="wrap_content"-->
    <!--android:text="fsfsdflfjdsksfjsdsdf"/>-->
    <!--<android.support.v7.widget.RecyclerView-->
    <!--android:id="@+id/detail_recycler_view"-->
    <!--android:layout_width="match_parent"-->
    <!--android:layout_height="match_parent" />-->

    <!--</LinearLayout>-->

    <!--</android.support.v4.widget.NestedScrollView>-->
    * */
}
