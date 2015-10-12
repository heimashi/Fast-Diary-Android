package cn.swang.ui.activity;

import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.dao.DbService;
import cn.swang.dao.NoteCardDao;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.adapter.StaggeredAdapter;
import cn.swang.ui.base.BaseActivity;
import cn.swang.ui.view.MyDialog;
import cn.swang.utils.NoteDialogManager;
import cn.swang.utils.ShareBitmapUtils;
import cn.swang.utils.MediaManager;

public class DetailActivity extends BaseActivity implements DbService.LoadDayCardListener, NoteDialogManager.NoteDialogHandle,MyDialog.DialogDismissCallBack, ShareBitmapUtils.ConvertDayCardListener, DbService.LoadTodayNoteListener, RecyclerViewAdapter.OnItemLongClickListener, View.OnClickListener {


    private RecyclerView mRecyclerView;
    private ImageView mDayCardImageView;
    private FloatingActionButton mShareDayCardFab;
    private RecyclerViewAdapter adapter;
    private DbService dbService;
    private List<Integer> noteCardList = new ArrayList<Integer>();
    List<RecyclerViewAdapter.NoteCardWrapper> datas = new ArrayList<RecyclerViewAdapter.NoteCardWrapper>();
    private DayCard dayCard;
    private ContentObserver contentObserver = null;
    public static final String START_ACTIVITY_DAY_CARD_NEED_UPDATE = "start_need_update_day_card";
    private boolean isStartNeedUpdate = false;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.scale_in2, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dayCard = (DayCard) getIntent().getSerializableExtra(StaggeredAdapter.INTENT_DAYCARD_EXTRAS);
        if (dayCard == null) {
            finish();
            return;
        }
        isStartNeedUpdate = getIntent().getBooleanExtra(START_ACTIVITY_DAY_CARD_NEED_UPDATE,false);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        dbService = new DbService(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recycler_view);
        mShareDayCardFab = (FloatingActionButton) findViewById(R.id.detail_share_daycard_fab);
        mDayCardImageView = (ImageView) findViewById(R.id.detail_daycard_iv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(isStartNeedUpdate){
            dbService.loadDayCardByDayId(dayCard.getDay_id(),this);
        }else {
            init();
        }

        adapter = new RecyclerViewAdapter(DetailActivity.this, datas);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(this);
        mShareDayCardFab.setOnClickListener(this);
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri.toString().equals(NoteCardDao.NOTE_CARD_URI_STRING + dayCard.getDay_id())) {
                    dbService.loadTodayNote(dayCard, DetailActivity.this);
                }
            }
        };
        getContentResolver().registerContentObserver(NoteCardDao.NOTE_CARD_URI, true, contentObserver);

    }

    private void init(){
        String SHOW_TIME = dayCard.getYear() + "/" + dayCard.getMouth() + "/" + dayCard.getDay();
        collapsingToolbar.setTitle(SHOW_TIME);
        if (!TextUtils.isEmpty(dayCard.getDayImagePath())) {
            String imageUri = ImageDownloader.Scheme.FILE.wrap(dayCard.getDayImagePath());
            ImageLoader.getInstance().displayImage(imageUri, mDayCardImageView);
        }else {
            mDayCardImageView.setBackgroundResource(R.drawable.daycard_image_default);
        }
        datas.clear();
        for (NoteCard note : dayCard.getNoteSet()) {
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(note));
        }
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
        if (contentObserver != null) {
            getContentResolver().unregisterContentObserver(contentObserver);
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.scale_out);
    }

    @Override
    protected boolean useActivityAnimation() {
        return false;
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


    NoteDialogManager noteDialogManager = null;

    @Override
    public void onItemLongClick(View v, int position) {
        datas.get(position).setIsSelected(true);
        noteDialogManager = new NoteDialogManager(this, datas.get(position).getNoteCard());
        noteDialogManager.showNoteLongClickDialog();
        noteDialogManager.setDismissCallBack(this);
        noteDialogManager.setNoteDialogHandle(this);
        adapter.notifyDataSetChanged();
    }

    boolean isGeneratingBitmap = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_share_daycard_fab:
                if (!isGeneratingBitmap) {
                    isGeneratingBitmap = true;
                    Snackbar.make(v, getString(R.string.detail_activity_generate_bitmap), Snackbar.LENGTH_SHORT).show();
                    ShareBitmapUtils bitmapUtils = new ShareBitmapUtils();
                    bitmapUtils.convertDayCardBitmap(this, dayCard);
                }
                break;
        }
    }



    @Override
    public void onLoadNoteSuccess(List<NoteCard> list, long day_id) {
        if (list == null || list.size() == 0) {
            finish();
            return;
        }
        datas.clear();
        for (NoteCard noteCard : list) {
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(noteCard));
        }
        dayCard.setNoteSet(list);
        adapter.notifyDataSetChanged();
        //mRecyclerView.scrollToPosition(datas.size() - 1);
    }

    @Override
    public void onConvertSuccess(String imgPath,DayCard dayCard) {
        isGeneratingBitmap = false;
        Intent intent = new Intent(this, ShareDayCardActivity.class);
        intent.putExtra(ShareDayCardActivity.SHARE_IMAGE_PATH, imgPath);
        intent.putExtra(ShareDayCardActivity.SHARE_DAYCARD_PRE, dayCard);
        startActivity(intent);
    }

    @Override
    public void deleteNote(NoteCard noteCard) {
        List<NoteCard> list=new ArrayList<NoteCard>();
        list.add(noteCard);
        dbService.deleteNote(list,null);
    }

    private static final int REQUEST_CODE_UPDATE_DIARY = 1444;

    @Override
    public void updateNote(NoteCard noteCard) {
        Intent intent= new Intent(this, LongDiaryActivity.class);
        intent.putExtra(LongDiaryActivity.UPDATE_DIARY_EXTRA_STRING,noteCard);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DIARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode==REQUEST_CODE_UPDATE_DIARY){
                if(data!=null){
                    NoteCard card = (NoteCard)data.getSerializableExtra(LongDiaryActivity.UPDATED_NEW_DIARY_EXTRA_STRING);
                    if(card!=null){
                        dbService.updateNote(card);
                    }
                }
            }
        }
    }

    @Override
    public void handleDialogDismiss() {
        for(RecyclerViewAdapter.NoteCardWrapper item:datas){
            if(item.isSelected()){
                item.setIsSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDayCardSuccess(DayCard dayCard) {
        this.dayCard=dayCard;
        init();
        adapter.notifyDataSetChanged();
        isStartNeedUpdate=false;
    }
}
