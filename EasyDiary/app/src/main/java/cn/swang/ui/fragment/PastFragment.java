package cn.swang.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.dao.DayCardDao;
import cn.swang.dao.DbService;
import cn.swang.dao.LoadDiaryListener;
import cn.swang.dao.NoteCardDao;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.adapter.StaggeredAdapter;
import cn.swang.ui.base.BaseFragment;
import cn.swang.ui.view.MyDialog;
import cn.swang.utils.DayCardDialogManager;

public class PastFragment extends BaseFragment implements DayCardDialogManager.DayCardDialogHandle,MyDialog.DialogDismissCallBack,LoadDiaryListener, StaggeredAdapter.OnItemLongClickListener, DbService.DeleteDayCardListener {

    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private List<StaggeredAdapter.DayCardWrapper> datas = new ArrayList<StaggeredAdapter.DayCardWrapper>();
    private StaggeredAdapter adapter;
    private DbService dbService;
    private ContentObserver dayCardContentObserver = null;
    private ContentObserver noteCardContentObserver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRelativeLayout =
                (RelativeLayout) inflater.inflate(R.layout.past_fragment, container, false);
        mRecyclerView = (RecyclerView) mRelativeLayout.findViewById(R.id.recycler_view);
        mEmptyView = (LinearLayout) mRelativeLayout.findViewById(R.id.past_empty_view);
        return mRelativeLayout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new StaggeredAdapter(getActivity(), datas);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(this);
        dbService = new DbService(getContext());
        dbService.loadAllDiary(PastFragment.this);

        dayCardContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                dbService.loadAllDiary(PastFragment.this);
            }
        };
        getActivity().getContentResolver().registerContentObserver(DayCardDao.DAY_CARD_URI, true, dayCardContentObserver);
        noteCardContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                dbService.loadAllDiary(PastFragment.this);
            }
        };
        getActivity().getContentResolver().registerContentObserver(NoteCardDao.NOTE_CARD_URI, true, noteCardContentObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != dayCardContentObserver) {
            getActivity().getContentResolver().unregisterContentObserver(dayCardContentObserver);
        }
        if (null != noteCardContentObserver) {
            getActivity().getContentResolver().unregisterContentObserver(noteCardContentObserver);
        }
    }

    @Override
    public void onLoadDiarySuccess(List<DayCard> list) {
        if (list == null || list.size() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
        datas.clear();
        for (DayCard dayCard : list) {
            datas.add(new StaggeredAdapter.DayCardWrapper(dayCard));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDiaryFailed() {

    }
    private DayCardDialogManager dayCardDialogManager;
    @Override
    public void onItemLongClick(View v, int position) {
        datas.get(position).setIsSelected(true);
        dayCardDialogManager = new DayCardDialogManager(getContext(),datas.get(position).getDayCard());
        dayCardDialogManager.showDayCardLongClickDialog();
        dayCardDialogManager.setDismissCallBack(this);
        dayCardDialogManager.setDayCardDialogHandle(this);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteDayCardSuccess(List<DayCard> list) {
        datas.clear();
        for (DayCard dayCard : list) {
            datas.add(new StaggeredAdapter.DayCardWrapper(dayCard));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void handleDialogDismiss() {
        for(StaggeredAdapter.DayCardWrapper item:datas){
            if(item.isSelected()){
                item.setIsSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deleteDayCard(DayCard dayCard) {
        List<DayCard> mlist = new ArrayList<DayCard>();
        mlist.add(dayCard);
        dbService.deleteDayCard(mlist, PastFragment.this);
    }
}
