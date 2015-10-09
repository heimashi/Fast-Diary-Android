package cn.swang.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.dao.DbService;
import cn.swang.dao.LoadDiaryListener;
import cn.swang.entity.DayCard;
import cn.swang.ui.adapter.ExportRecylerViewAdapter;
import cn.swang.ui.adapter.ShareRecylerViewAdapter;
import cn.swang.ui.base.BaseActivity;

/**
 * Created by sw on 2015/9/13.
 */
public class ExportDiaryActivity extends BaseActivity implements LoadDiaryListener, View.OnClickListener{

    private RecyclerView mRecyclerView;
    private DbService dbService;
    private ExportRecylerViewAdapter mAdapter;
    private List<DayCard> mList = new ArrayList<DayCard>();
    private ProgressView pv_linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_activity_recycler_view);
        pv_linear = (ProgressView)findViewById(R.id.progress_pv_linear);
        dbService = new DbService(this);
        dbService.loadAllDiary(this);
        pv_linear.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new ExportRecylerViewAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    @Override
    public void onLoadDiarySuccess(List<DayCard> list) {
        pv_linear.setVisibility(View.GONE);
        if(list==null||list.size()==0){
            mList.clear();
            mAdapter.notifyDataSetChanged();
            return;
        }
        mList=list;
        mAdapter.setDatas(mList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDiaryFailed() {

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
}
