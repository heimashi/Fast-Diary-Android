package cn.swang.ui.fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import android.view.MotionEvent;
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
import cn.swang.dao.DayCardDao;
import cn.swang.dao.DbService;
import cn.swang.dao.NoteCardDao;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.activity.LongDiaryActivity;
import cn.swang.ui.adapter.RecyclerViewAdapter;
import cn.swang.ui.base.BaseFragment;
import cn.swang.ui.view.AudioRecorderButton;
import cn.swang.ui.view.MyDialog;
import cn.swang.utils.CommonUtils;
import cn.swang.utils.MediaManager;
import cn.swang.utils.NoteDialogManager;

public class ListFragment extends BaseFragment implements NoteDialogManager.NoteDialogHandle, MyDialog.DialogDismissCallBack, DbService.LoadTodayNoteListener, View.OnClickListener, RecyclerViewAdapter.OnItemLongClickListener {


    private static final int REQUEST_CODE_PICK_IMAGE = 1000;
    private static final int REQUEST_CODE_CAMERA = 1111;
    private static final int REQUEST_CODE_LONG_DIARY = 1222;
    private static final int REQUEST_CODE_UPDATE_DIARY = 1333;
    private long mDayId = -1;
    private LinearLayout mLinearLayout,mEmptyView;
    private RecyclerView mRecyclerView;
    private TextInputLayout mInputView;
    private EditText mContentEt;
    private List<RecyclerViewAdapter.NoteCardWrapper> datas = new ArrayList<RecyclerViewAdapter.NoteCardWrapper>();
    private RecyclerViewAdapter adapter;
    private DbService dbService;
    private ImageView mSentBtn;
    private TextView mSentTv;
    private File cameraFile;
    private DayCard mdayCard;
    private FloatingActionButton mFab1, mFab2, mFab3;
    private ImageView audioChoiceBtn;
    private FrameLayout fabContainer;
    private AudioRecorderButton audioRecorderButton;
    private boolean isFabViewShowing = false;
    private boolean isRecordeStateSelected = false;
    //private List<Integer> noteCardList = new ArrayList<Integer>();
    private ContentObserver contentObserver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLinearLayout =
                (LinearLayout) inflater.inflate(R.layout.list_fragment, container, false);
        mRecyclerView = (RecyclerView) mLinearLayout.findViewById(R.id.recycler_view);
        mEmptyView = (LinearLayout)mLinearLayout.findViewById(R.id.list_empty_view);
        mInputView = (TextInputLayout) mLinearLayout.findViewById(R.id.input_layout);
        fabContainer = (FrameLayout) mLinearLayout.findViewById(R.id.other_btn_view);
        mFab1 = (FloatingActionButton) mLinearLayout.findViewById(R.id.list_fab1);
        mFab2 = (FloatingActionButton) mLinearLayout.findViewById(R.id.list_fab2);
        mFab3 = (FloatingActionButton) mLinearLayout.findViewById(R.id.list_fab3);
        audioRecorderButton = (AudioRecorderButton) mLinearLayout.findViewById(R.id.audio_recorder_bt);
        mInputView.setHint(getString(R.string.prompt_tint));
        mContentEt = mInputView.getEditText();
        audioChoiceBtn = (ImageView) mLinearLayout.findViewById(R.id.audio_btn_choice);
        mSentBtn = (ImageView) mLinearLayout.findViewById(R.id.send_bt);
        mSentTv = (TextView) mLinearLayout.findViewById(R.id.send_bt_tv);
        if (mInputView != null && mInputView.getEditText() != null) {
            mInputView.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        mSentBtn.setVisibility(View.GONE);
                        mSentTv.setVisibility(View.VISIBLE);
                    } else {
                        mSentBtn.setVisibility(View.VISIBLE);
                        mSentTv.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        audioChoiceBtn.setOnClickListener(this);
        mSentTv.setOnClickListener(this);
        mSentBtn.setOnClickListener(this);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);
        mFab3.setOnClickListener(this);
        return mLinearLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        adapter = new RecyclerViewAdapter(getActivity(), datas);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(this);
        audioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onAudioFinish(float seconds, String filePath) {
                sendAudio(filePath, (int) seconds);
            }
        });
        fabContainer.setOnClickListener(this);
        dbService = new DbService(getContext());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mdayCard = new DayCard(year, month, day);
        mdayCard.setDay_id(mDayId);
        dbService.loadTodayNote(mdayCard, this);
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri.toString().equals(NoteCardDao.NOTE_CARD_URI_STRING + mDayId) || mDayId == -1) {
                    DayCard tdayCard = new DayCard();
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(new Date());
                    tdayCard.setYear(calendar1.get(Calendar.YEAR));
                    tdayCard.setMouth(calendar1.get(Calendar.MONTH) + 1);
                    tdayCard.setDay(calendar1.get(Calendar.DAY_OF_MONTH));
                    tdayCard.setDay_id(mDayId);
                    dbService.loadTodayNote(tdayCard, ListFragment.this);
                }
            }
        };
        getActivity().getContentResolver().registerContentObserver(NoteCardDao.NOTE_CARD_URI, true, contentObserver);
    }

    @Override
    public void onStart() {
        super.onStart();
        sendMoveMotionEvent(mRecyclerView, GlobalData.screenWidth - 100, GlobalData.screenHeight - 200);
    }

    private void sendMoveMotionEvent(View view, float x, float y) {
        //Instrumentation instrumentation = new Instrumentation();
        try {
            final MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN, x, y, 0);
            view.dispatchTouchEvent(downEvent);
            //view.onTouchEvent(downEvent);
            downEvent.recycle();
            y -= 150;
            final MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE, x, y, 0);
            view.dispatchTouchEvent(moveEvent);
            moveEvent.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecordeStateUnSelected() {
        isRecordeStateSelected = false;
        audioChoiceBtn.setImageResource(R.drawable.chat_img_bottom);
        mInputView.setVisibility(View.VISIBLE);
        audioRecorderButton.setVisibility(View.GONE);
    }

    private void setRecordeStateSelected() {
        isRecordeStateSelected = true;
        audioChoiceBtn.setImageResource(R.drawable.text_img_bottom);
        mInputView.setVisibility(View.GONE);
        audioRecorderButton.setVisibility(View.VISIBLE);
    }

    private void setFabContainerComeIn() {
        CommonUtils.hideKeyboard(mInputView.getEditText());
        Animation bt_animation = AnimationUtils.loadAnimation(getContext(), R.anim.bt_rotate);
        mSentBtn.startAnimation(bt_animation);
        isFabViewShowing = true;
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_in);
        fabContainer.setVisibility(View.VISIBLE);
        fabContainer.startAnimation(animation);
    }

    private void setFabContainerOut() {
        Animation bt_animation = AnimationUtils.loadAnimation(getContext(), R.anim.bt_rotate_back);
        mSentBtn.startAnimation(bt_animation);
        isFabViewShowing = false;
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_out);
        fabContainer.startAnimation(animation);
        fabContainer.setVisibility(View.GONE);
    }


    NoteDialogManager noteDialogManager = null;

    @Override
    public void onItemLongClick(View v, int position) {
        datas.get(position).setIsSelected(true);
        noteDialogManager = new NoteDialogManager(getContext(), datas.get(position).getNoteCard());
        noteDialogManager.showNoteLongClickDialog();
        noteDialogManager.setDismissCallBack(this);
        noteDialogManager.setNoteDialogHandle(this);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void handleDialogDismiss() {
        for (RecyclerViewAdapter.NoteCardWrapper item : datas) {
            if (item.isSelected()) {
                item.setIsSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void writeLongDiary() {
        Intent intent = new Intent(getActivity(), LongDiaryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LONG_DIARY);
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

        cameraFile = new File(Environment.getExternalStorageDirectory() + IConstants.TAKE_PHOTO_PATH + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_LONG_DIARY) {
                if (data != null) {
                    String content = data.getStringExtra(LongDiaryActivity.LONG_DIARY_EXTRA_STRING);
                    sendTextNoteMsg(content);
                }
            } else if (requestCode == REQUEST_CODE_UPDATE_DIARY) {
                if (data != null) {
                    NoteCard card = (NoteCard) data.getSerializableExtra(LongDiaryActivity.UPDATED_NEW_DIARY_EXTRA_STRING);
                    if (card != null) {
                        dbService.updateNote(card);
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
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
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

    private void sendTextNoteMsg(String content) {
        NoteCard noteCard = new NoteCard();
        noteCard.setContent(content);
        noteCard.setDate(new Date());
        noteCard.setDay_id(mDayId);
        dbService.saveNote(noteCard);
    }

    private void sendAudio(String audioPath, int audioLength) {
        NoteCard noteCard = new NoteCard();
        noteCard.setDay_id(mDayId);
        noteCard.setVoicePath(audioPath);
        noteCard.setVoiceLength(audioLength);
        noteCard.setDate(new Date());
        dbService.saveNote(noteCard);
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        NoteCard noteCard = new NoteCard();
        noteCard.setDay_id(mDayId);
        noteCard.setImgPath(filePath);
        noteCard.setDate(new Date());
        dbService.saveNote(noteCard);
    }


    @Override
    public void onLoadNoteSuccess(List<NoteCard> list, long day_id) {
        if (day_id != -1) mDayId = day_id;
        if (list == null) {
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }
        if (list.size() == 0) {
            mDayId = -1;
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }
        datas.clear();
        for (NoteCard noteCard : list) {
            datas.add(new RecyclerViewAdapter.NoteCardWrapper(noteCard));
        }
        adapter.notifyDataSetChanged();
        if (datas.size() > 0) mRecyclerView.scrollToPosition(datas.size() - 1);
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
        if (contentObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(contentObserver);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_bt_tv:
                String content = mContentEt.getText().toString().trim();
                sendTextNoteMsg(content);
                mContentEt.setText("");
                break;
            case R.id.send_bt:
                if (isFabViewShowing) {
                    setFabContainerOut();
                } else {
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
                writeLongDiary();
                break;
            case R.id.audio_btn_choice:
                if (isRecordeStateSelected) {
                    setRecordeStateUnSelected();
                } else {
                    setRecordeStateSelected();
                }
                break;
            case R.id.other_btn_view:
                if (isFabViewShowing) {
                    setFabContainerOut();
                }
                break;
            default:
                if (isFabViewShowing) {
                    setFabContainerOut();
                }
                break;
        }
    }

    @Override
    public void deleteNote(NoteCard noteCard) {
        List<NoteCard> list = new ArrayList<NoteCard>();
        list.add(noteCard);
        dbService.deleteNote(list, null);
    }

    @Override
    public void updateNote(NoteCard noteCard) {
        Intent intent = new Intent(getActivity(), LongDiaryActivity.class);
        intent.putExtra(LongDiaryActivity.UPDATE_DIARY_EXTRA_STRING, noteCard);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DIARY);
    }
}
