package cn.swang.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

import cn.swang.R;
import cn.swang.dao.DbService;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.adapter.DetailRecyclerViewAdapter;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.adapter.StaggeredAdapter;
import cn.swang.ui.base.BaseActivity;
import cn.swang.ui.fragment.PastFragment;
import cn.swang.utils.AudioManager;
import cn.swang.utils.ImageLoaderHelper;
import cn.swang.utils.MediaManager;

public class DetailActivity extends BaseActivity implements RecyclerViewAdapter.OnItemLongClickListener,View.OnClickListener,DbService.DeleteNoteListener{

    private RecyclerView mRecyclerView;
    private ImageView mDayCardImageView;
    private FloatingActionButton mDeleteFab,mUpdateFab;
    private RecyclerViewAdapter adapter;
    private DbService dbService;
    private List<Integer> noteCardList=new ArrayList<Integer>();
    List<RecyclerViewAdapter.NoteCardWrapper> datas=new ArrayList<RecyclerViewAdapter.NoteCardWrapper>();
    private DayCard dayCard;
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
                adapter.notifyDataSetChanged();
                Snackbar.make(v, "update success!", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDeleteSuccess(List<NoteCard> list) {
        datas.clear();
        noteCardList.clear();
        mDeleteFab.setVisibility(View.GONE);
        sendUpdateNoteIntent(list);
        for(NoteCard noteCard:list){
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(noteCard));
        }
        adapter.notifyDataSetChanged();

    }

    private void sendUpdateNoteIntent(List<NoteCard> mList){
        dayCard.setNoteSet(mList);
        Intent intent = new Intent();
        intent.setAction(PastFragment.UPDATE_NOTE_ACTION);
        intent.putExtra(PastFragment.UPDATE_NOTE_EXTRA, dayCard);
        sendBroadcast(intent);
    }
}
