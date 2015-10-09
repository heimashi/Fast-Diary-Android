package cn.swang.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.dao.DbService;
import cn.swang.entity.NoteCard;
import cn.swang.ui.adapter.SearchRecylerViewAdapter;
import cn.swang.ui.base.BaseActivity;

/**
 * Created by sw on 2015/9/13.
 */
public class SearchActivity extends BaseActivity implements Handler.Callback,DbService.OnSearchNoteListener ,View.OnClickListener{

    private static final int MSG_START_PROGRESS = 1000;
    private static final int MSG_STOP_PROGRESS = 1001;

    private static final long START_DELAY = 2000;
    private static final long STOP_DELAY = 200;

    private EditText searchEditText;
    private RecyclerView mRecyclerView;
    private DbService dbService;
    private ImageView backButton, searchButton;
    private LinearLayout mEmptyLayout;
    private SearchRecylerViewAdapter mAdapter;
    private List<NoteCard> mList = new ArrayList<NoteCard>();
    private ProgressView pv_linear;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //overridePendingTransition(R.anim.scale_in2, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchEditText = (EditText) findViewById(R.id.search_text_edit);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_activity_recycler_view);
        backButton = (ImageView)findViewById(R.id.search_back_view);
        searchButton = (ImageView)findViewById(R.id.search_image_view);
        pv_linear = (ProgressView)findViewById(R.id.progress_pv_linear);
        mEmptyLayout = (LinearLayout)findViewById(R.id.list_empty_view);
        mEmptyLayout.setVisibility(View.VISIBLE);
        mHandler = new Handler(this);
        backButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        dbService = new DbService(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new SearchRecylerViewAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pv_linear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if(s.length()<2) return;
                dbService.searchNote(s.toString(), SearchActivity.this);
            }
        });

    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(0, R.anim.scale_out);
//    }

//    @Override
//    protected boolean useActivityAnimation() {
//        return false;
//    }

    @Override
    public void onSearchFinish(List<NoteCard> list) {
        mHandler.sendEmptyMessageDelayed(MSG_STOP_PROGRESS, STOP_DELAY);
        if (list == null) {
            mList.clear();
        }else {
            mList = list;
        }
        if(mList.size()==0){
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else {
            mEmptyLayout.setVisibility(View.GONE);
        }
        mAdapter.setDatas(mList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(MSG_START_PROGRESS, START_DELAY);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_START_PROGRESS:

                break;
            case MSG_STOP_PROGRESS:
                //pv_linear.stop();
                pv_linear.setVisibility(View.GONE);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_back_view:
                finish();
                break;
            case R.id.search_image_view:
                pv_linear.setVisibility(View.VISIBLE);
                dbService.searchNote(searchEditText.getText().toString(),this);
                break;
        }
    }
}
