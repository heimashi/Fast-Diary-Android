package cn.swang.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.dao.DbService;
import cn.swang.dao.LoadDiaryListener;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.adapter.StaggeredAdapter;
import cn.swang.ui.base.BaseFragment;

public class PastFragment extends BaseFragment implements LoadDiaryListener,StaggeredAdapter.OnItemLongClickListener,DbService.DeleteDayCardListener {

    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mDeleteFab;
    private List<StaggeredAdapter.DayCardWrapper> datas=new ArrayList<StaggeredAdapter.DayCardWrapper>();
    private StaggeredAdapter adapter;
    private DbService dbService;
    private List<Integer> dayCardList=new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRelativeLayout =
                (RelativeLayout) inflater.inflate(R.layout.past_fragment, container, false);
        mRecyclerView=(RecyclerView)mRelativeLayout.findViewById(R.id.recycler_view);
        mDeleteFab=(FloatingActionButton)mRelativeLayout.findViewById(R.id.past_delete_day_card_fab);
        return mRelativeLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter=new StaggeredAdapter(getActivity(),datas);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(this);
        dbService=new DbService(getContext());
        dbService.loadAllDiary(PastFragment.this);
        mDeleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DayCard> mlist=new ArrayList<DayCard>();
                for (Integer i:dayCardList){
                    mlist.add(datas.get(i).getDayCard());
                }
                dbService.deleteDayCard(mlist, PastFragment.this);
            }
        });
    }



    @Override
    public void onLoadDiarySuccess(List<DayCard> list) {
        if(list==null||list.size()==0) return;
        datas.clear();
        for(DayCard dayCard:list){
            datas.add(new StaggeredAdapter.DayCardWrapper(dayCard));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDiaryFailed() {

    }

    @Override
    public void onItemLongClick(View v, int position) {
        if(datas.get(position).isSelected()){
            dayCardList.remove((Object)position);
            datas.get(position).setIsSelected(false);
        }else {
            dayCardList.add(position);
            datas.get(position).setIsSelected(true);
        }
        if(dayCardList.size()==0){
            mDeleteFab.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_out));
            mDeleteFab.setVisibility(View.GONE);
        }else {
            if(mDeleteFab.getVisibility()!=View.VISIBLE){
                mDeleteFab.setVisibility(View.VISIBLE);
                mDeleteFab.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.right_in));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteDayCardSuccess(List<DayCard> list) {
        datas.clear();
        dayCardList.clear();
        mDeleteFab.setVisibility(View.GONE);
        for(DayCard dayCard:list){
            datas.add(new StaggeredAdapter.DayCardWrapper(dayCard));
        }
        adapter.notifyDataSetChanged();
    }
}
