package cn.swang.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.dao.DbService;
import cn.swang.dao.LoadDiaryListener;
import cn.swang.entity.DayCard;
import cn.swang.ui.adapter.StaggeredAdapter;
import cn.swang.ui.base.BaseFragment;

public class PastFragment extends BaseFragment implements LoadDiaryListener {

    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private List<DayCard> datas=new ArrayList<DayCard>();
    private StaggeredAdapter adapter;
    private DbService dbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRelativeLayout =
                (RelativeLayout) inflater.inflate(R.layout.past_fragment, container, false);
        mRecyclerView=(RecyclerView)mRelativeLayout.findViewById(R.id.recycler_view);
        return mRelativeLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        adapter=new StaggeredAdapter(getActivity(),datas);
        mRecyclerView.setAdapter(adapter);
        dbService=new DbService(getContext());
        dbService.loadAllDiary(PastFragment.this);
    }



    @Override
    public void onLoadDiarySuccess(List<DayCard> list) {
        if(list==null||list.size()==0) return;
//        DayCard dayCard = list.get(0);
//        if(dayCard.getYear()== GlobalData.yearNow&&dayCard.getMouth()==GlobalData.monthNow&&dayCard.getDay()==GlobalData.dayNow){
//            list.remove(0);
//        }
        datas.clear();
        datas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDiaryFailed() {

    }
}
