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
import cn.swang.utils.ShareBitmapUtils;
import cn.swang.utils.MediaManager;

public class DetailActivity extends BaseActivity implements ShareBitmapUtils.ConvertDayCardListener,DbService.LoadTodayNoteListener, DbService.OnDeleteNoteListener,RecyclerViewAdapter.OnItemLongClickListener,View.OnClickListener{

    private RecyclerView mRecyclerView;
    private ImageView mDayCardImageView;
    private FloatingActionButton mDeleteFab,mUpdateFab;
    private RecyclerViewAdapter adapter;
    private DbService dbService;
    private List<Integer> noteCardList=new ArrayList<Integer>();
    List<RecyclerViewAdapter.NoteCardWrapper> datas=new ArrayList<RecyclerViewAdapter.NoteCardWrapper>();
    private DayCard dayCard;
    private ContentObserver contentObserver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dayCard = (DayCard) getIntent().getSerializableExtra(StaggeredAdapter.INTENT_DAYCARD_EXTRAS);
        if(dayCard==null) {
            finish();
            return;
        }
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        String SHOW_TIME = dayCard.getYear()+"/"+dayCard.getMouth()+"/"+dayCard.getDay();
        collapsingToolbar.setTitle(SHOW_TIME);
        dbService=new DbService(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.detail_recycler_view);
        mDeleteFab = (FloatingActionButton)findViewById(R.id.detail_delete_fab);
        mUpdateFab = (FloatingActionButton)findViewById(R.id.detail_update_fab);
        mDayCardImageView = (ImageView)findViewById(R.id.detail_daycard_iv);
        if(!TextUtils.isEmpty(dayCard.getDayImagePath())){
            String imageUri = ImageDownloader.Scheme.FILE.wrap(dayCard.getDayImagePath());
            ImageLoader.getInstance().displayImage(imageUri,mDayCardImageView);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        datas.clear();
        for(NoteCard note:dayCard.getNoteSet()){
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(note));
        }
        adapter=new RecyclerViewAdapter(DetailActivity.this,datas);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(this);
        mDeleteFab.setOnClickListener(this);
        mUpdateFab.setOnClickListener(this);
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if(uri.toString().equals(NoteCardDao.NOTE_CARD_URI_STRING+dayCard.getDay_id())){
                    dbService.loadTodayNote(dayCard,DetailActivity.this);
                }
            }
        };
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        if(datas.get(position).isSelected()){
            noteCardList.remove((Object)position);
            datas.get(position).setIsSelected(false);
        }else {
            noteCardList.add(position);
            datas.get(position).setIsSelected(true);
        }
        if(noteCardList.size()==0){
            mDeleteFab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_out));
            mDeleteFab.setVisibility(View.GONE);
        }else {
            if(mDeleteFab.getVisibility()!=View.VISIBLE){
                mDeleteFab.setVisibility(View.VISIBLE);
                mDeleteFab.startAnimation(AnimationUtils.loadAnimation(this,R.anim.right_in));
            }
        }
        adapter.notifyDataSetChanged();
    }

    boolean isGeneratingBitmap = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_delete_fab:
                List<NoteCard> mlist=new ArrayList<NoteCard>();
                for (Integer i:noteCardList){
                    mlist.add(datas.get(i).getNoteCard());
                }
                dbService.deleteNote(mlist,this);
                break;
            case R.id.detail_update_fab:
                if(!isGeneratingBitmap){
                    isGeneratingBitmap=true;
                    Snackbar.make(v, getString(R.string.detail_activity_generate_bitmap), Snackbar.LENGTH_SHORT).show();
                    ShareBitmapUtils bitmapUtils=new ShareBitmapUtils();
                    bitmapUtils.convertDayCardBitmap(this,dayCard);
                }
                break;
        }
    }

    @Override
    public void onDeleteSuccess() {
        noteCardList.clear();
        mDeleteFab.setVisibility(View.GONE);
    }

    @Override
    public void onLoadNoteSuccess(List<NoteCard> list, long day_id) {
        if(list==null||list.size()==0){
            finish();
            return;
        }
        datas.clear();
        for(NoteCard noteCard:list){
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(noteCard));
        }
        adapter.notifyDataSetChanged();
        //mRecyclerView.scrollToPosition(datas.size() - 1);
    }

    @Override
    public void onConvertSuccess(String imgPath) {
        isGeneratingBitmap=false;
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f = new File(imgPath);
        if (f != null && f.exists() && f.isFile()) {
            intent.setType("image/jpg");
            Uri u = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM, u);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "请选择"));
        }
    }

//    private void sendUpdateNoteIntent(List<NoteCard> mList){
//        dayCard.setNoteSet(mList);
//        Intent intent = new Intent();
//        intent.setAction(PastFragment.UPDATE_NOTE_ACTION);
//        intent.putExtra(PastFragment.UPDATE_NOTE_EXTRA, dayCard);
//        sendBroadcast(intent);
//    }
}
