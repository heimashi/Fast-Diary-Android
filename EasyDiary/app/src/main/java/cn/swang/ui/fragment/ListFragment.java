package cn.swang.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.app.IConstants;
import cn.swang.dao.DbService;
import cn.swang.dao.LoadNoteListener;
import cn.swang.dao.SaveNoteListener;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.base.BaseFragment;
import cn.swang.ui.view.AudioRecorderButton;
import cn.swang.utils.CommonUtils;
import cn.swang.utils.MediaManager;

public class ListFragment extends BaseFragment implements SaveNoteListener,DbService.DeleteNoteListener,LoadNoteListener,View.OnClickListener,RecyclerViewAdapter.OnItemLongClickListener{


    private static final int REQUEST_CODE_PICK_IMAGE = 1000;
    private static final int REQUEST_CODE_CAMERA = 1111;

    private LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private TextInputLayout mInputView;
    private EditText mContentEt;
    private List<RecyclerViewAdapter.NoteCardWrapper> datas=new ArrayList<RecyclerViewAdapter.NoteCardWrapper>();
    private RecyclerViewAdapter adapter;
    private DbService dbService;
    private ImageView mSentBtn;
    private TextView mSentTv;
    private File cameraFile;
    private DayCard mdayCard;
    private FloatingActionButton mFab1,mFab2,mFab3,mDeleteFab;
    private FrameLayout fabContainer;
    private AudioRecorderButton audioRecorderButton;
    private boolean isFabViewShowing = false;
    private boolean isRecordeStateSelected = false;
    private List<Integer> noteCardList=new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLinearLayout =
                (LinearLayout) inflater.inflate(R.layout.list_fragment, container, false);
        mRecyclerView=(RecyclerView)mLinearLayout.findViewById(R.id.recycler_view);
        mInputView=(TextInputLayout)mLinearLayout.findViewById(R.id.input_layout);
        fabContainer = (FrameLayout)mLinearLayout.findViewById(R.id.other_btn_view);
        mFab1=(FloatingActionButton)mLinearLayout.findViewById(R.id.list_fab1);
        mFab2=(FloatingActionButton)mLinearLayout.findViewById(R.id.list_fab2);
        mFab3=(FloatingActionButton)mLinearLayout.findViewById(R.id.list_fab3);
        mDeleteFab=(FloatingActionButton)mLinearLayout.findViewById(R.id.list_delete_fab);
        audioRecorderButton=(AudioRecorderButton)mLinearLayout.findViewById(R.id.audio_recorder_bt);
        mInputView.setHint(getString(R.string.prompt_tint));
        mContentEt=mInputView.getEditText();
        mSentBtn=(ImageView)mLinearLayout.findViewById(R.id.send_bt);
        mSentTv=(TextView)mLinearLayout.findViewById(R.id.send_bt_tv);
        if(mInputView!=null&&mInputView.getEditText()!=null){
            mInputView.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        mSentBtn.setVisibility(View.GONE);
                        mSentTv.setVisibility(View.VISIBLE);
                    }else {
                        mSentBtn.setVisibility(View.VISIBLE);
                        mSentTv.setVisibility(View.GONE);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        mSentTv.setOnClickListener(this);
        mSentBtn.setOnClickListener(this);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);
        mFab3.setOnClickListener(this);
        mDeleteFab.setOnClickListener(this);
        return mLinearLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        adapter=new RecyclerViewAdapter(getActivity(),datas);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(this);
        audioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onAudioFinish(float seconds, String filePath) {
                sendAudio(filePath, (int) seconds);
            }
        });
        fabContainer.setOnClickListener(this);
        dbService=new DbService(getContext());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mdayCard = new DayCard(year,month,day);
        dbService.loadDiary(mdayCard, this);
    }

    private void setRecordeStateUnSelected(){
        isRecordeStateSelected=false;
        mInputView.setVisibility(View.VISIBLE);
        audioRecorderButton.setVisibility(View.GONE);
    }
    private void setRecordeStateSelected(){
        isRecordeStateSelected=true;
        mInputView.setVisibility(View.GONE);
        audioRecorderButton.setVisibility(View.VISIBLE);
    }
    private void setFabContainerComeIn(){
        cancelDeleteNote();
        Animation bt_animation=AnimationUtils.loadAnimation(getContext(),R.anim.bt_rotate);
        mSentBtn.startAnimation(bt_animation);
        isFabViewShowing=true;
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_in);
        fabContainer.setVisibility(View.VISIBLE);
        fabContainer.startAnimation(animation);
    }

    private void setFabContainerOut(){
        cancelDeleteNote();
        Animation bt_animation=AnimationUtils.loadAnimation(getContext(),R.anim.bt_rotate_back);
        mSentBtn.startAnimation(bt_animation);
        isFabViewShowing=false;
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_out);
        fabContainer.startAnimation(animation);
        fabContainer.setVisibility(View.GONE);
    }

    private void cancelDeleteNote(){
        if(mDeleteFab.getVisibility()==View.VISIBLE){
            for(Integer i:noteCardList){
                datas.get(i).setIsSelected(false);
            }
            noteCardList.clear();
            mDeleteFab.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
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
            mDeleteFab.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.right_out));
            mDeleteFab.setVisibility(View.GONE);
        }else {
            if(mDeleteFab.getVisibility()!=View.VISIBLE){
                mDeleteFab.setVisibility(View.VISIBLE);
                mDeleteFab.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.right_in));
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(Environment.getExternalStorageDirectory()+ IConstants.TAKE_PHOTO_PATH + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if(requestCode==REQUEST_CODE_CAMERA){
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            }else if(requestCode==REQUEST_CODE_PICK_IMAGE){
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            }
        }
    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        String st8 = getResources().getString(R.string.cannot_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (TextUtils.isEmpty(picturePath)) {
                Toast toast = Toast.makeText(getContext(), st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getContext(), st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    private void sendTextNoteMsg(String content){
        NoteCard noteCard = new NoteCard();
        noteCard.setContent(content);
        noteCard.setDate(new Date());
        dbService.saveNoteWithCache(noteCard, ListFragment.this);
    }

    private void sendAudio(String audioPath,int audioLength){
        NoteCard noteCard = new NoteCard();
        noteCard.setVoicePath(audioPath);
        noteCard.setVoiceLength(audioLength);
        noteCard.setDate(new Date());
        dbService.saveNoteWithCache(noteCard, this);
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        NoteCard noteCard = new NoteCard();
        noteCard.setImgPath(filePath);
        noteCard.setDate(new Date());
        dbService.saveNoteWithCache(noteCard, this);
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

    @Override
    public void onSaveSuccess(List<NoteCard> list) {
        datas.clear();
        mdayCard.setDayImagePath(GlobalData.dayCardImagePath);
        sendUpdateNoteIntent(list);
        for(NoteCard noteCard:list){
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(noteCard));
        }
        adapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(datas.size() - 1);
    }

    @Override
    public void onSaveFailed() {

    }

    private void sendUpdateNoteIntent(List<NoteCard> mList){
        mdayCard.setNoteSet(mList);
        Intent intent = new Intent();
        intent.setAction(PastFragment.UPDATE_NOTE_ACTION);
        intent.putExtra(PastFragment.UPDATE_NOTE_EXTRA, mdayCard);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onLoadNoteSuccess(List<NoteCard> list) {
        if(list==null||list.size()==0) return;
        datas.clear();
        mdayCard.setDay_id(GlobalData.dayCard_id);
        sendUpdateNoteIntent(list);
        for(NoteCard noteCard:list){
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(noteCard));
        }
        adapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(datas.size() - 1);
    }

    @Override
    public void onLoadNoteFailed() {

    }

    @Override
    public void onPause() {
        super.onPause();
        MediaManager.getInstance().pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaManager.getInstance().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaManager.getInstance().release();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_bt_tv:
                String content = mContentEt.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getContext(),"Empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                sendTextNoteMsg(content);
                mContentEt.setText("");
                break;
            case R.id.send_bt:
                if(isFabViewShowing){
                    setFabContainerOut();
                }else {
                    setFabContainerComeIn();
                }
                break;
            case R.id.list_fab1:
                selectPicFromCamera();
                break;
            case R.id.list_fab2:
                selectPicFromLocal();
                break;
            case R.id.list_fab3:
                if(isRecordeStateSelected){
                    setRecordeStateUnSelected();
                }else {
                    setRecordeStateSelected();
                }
                break;
            case R.id.list_delete_fab:
                List<NoteCard> mlist=new ArrayList<NoteCard>();
                for (Integer i:noteCardList){
                    mlist.add(datas.get(i).getNoteCard());
                }
                dbService.deleteNote(mlist,this);
                break;
            case R.id.other_btn_view:
                if(isFabViewShowing){
                    setFabContainerOut();
                }
                break;
            default:
                if(isFabViewShowing){
                    setFabContainerOut();
                }
                break;
        }
    }


}
