package cn.swang.ui.view;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.app.IConstants;
import cn.swang.utils.AudioManager;
import cn.swang.utils.RecordDialogManager;

/**
 * Created by sw on 2015/9/14.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{

    //state
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDERING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    //distance
    private static final int DISTANCE_Y_CANCEL = 50;


    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;
    private RecordDialogManager mRecordDialogManager;
    private AudioManager mAudioManager;
    private float mTime=0f;
    //if or not touch longclick
    private boolean mReady =false;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }
    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRecordDialogManager = new RecordDialogManager(context);
        String dir = Environment.getExternalStorageDirectory()+ IConstants.AUDIO_RECORD_PATH;
        mAudioManager = AudioManager.getInstanse(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });

    }

    private static final String REQUEST_AUDIO_PERMISION = "request_audio";
    private static final String REQUEST_AUDIO_PERMISION_IS_REQUEST = "request_audio_is_req";
    public void prepare(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!GlobalData.app().getSharedPreferences(REQUEST_AUDIO_PERMISION,Context.MODE_PRIVATE).getBoolean(REQUEST_AUDIO_PERMISION_IS_REQUEST,false)){
                    GlobalData.app().getSharedPreferences(REQUEST_AUDIO_PERMISION,Context.MODE_PRIVATE).edit().putBoolean(REQUEST_AUDIO_PERMISION_IS_REQUEST,true).commit();
                    MediaRecorder recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    File dir = new File(Environment.getExternalStorageDirectory()+ IConstants.AUDIO_RECORD_PATH);
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    File file = new File(dir,"test.amr");
                    recorder.setOutputFile(file.getAbsolutePath());
                    try {
                        recorder.prepare();
                        recorder.start();
                        Thread.sleep(500);
                        recorder.stop();
                        recorder.release();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public interface AudioFinishRecorderListener{
        void onAudioFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener audioFinishRecorderListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        audioFinishRecorderListener=listener;
    }

    private static final int MSG_AUDIO_PREPARED = 0X111;
    private static final int MSG_AUDIO_CHANGED = 0X112;
    private static final int MSG_AUDIO_DIMISS = 0X113;

    private Runnable mGetVoiceLevelRunnable =new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    mTime+=0.1f;
                    mHandler.sendEmptyMessage(MSG_AUDIO_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    mRecordDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_AUDIO_CHANGED:
                    mRecordDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_AUDIO_DIMISS:
                    mRecordDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDERING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording){
                    if(wantToCancel(x,y)){
                        changeState(STATE_WANT_CANCEL);
                    }else {
                        changeState(STATE_RECORDERING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!mReady){
                    reset();
                    return super.onTouchEvent(event);
                }
                if(!isRecording||mTime<0.6f){
                    mRecordDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_AUDIO_DIMISS, 1300);
                }else if(mCurState==STATE_RECORDERING){//record success!
                    mRecordDialogManager.dimissDialog();
                    mAudioManager.release();
                    //callback
                    if(audioFinishRecorderListener!=null){
                        audioFinishRecorderListener.onAudioFinish(mTime,mAudioManager.getCurrentFilePath());
                    }
                }else if(mCurState==STATE_WANT_CANCEL){
                    mRecordDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }

                reset();
                break;
            case MotionEvent.ACTION_CANCEL:
                if(isRecording){
                    mRecordDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
        }

        return super.onTouchEvent(event);
    }

    private void reset() {
        mReady=false;
        isRecording=false;
        changeState(STATE_NORMAL);
        mTime=0f;
    }

    private boolean wantToCancel(int x, int y) {
        if(x<0||x>getWidth()){
            return true;
        }
        if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL){
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if(mCurState!=state){
            mCurState=state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDERING:
                    setBackgroundResource(R.drawable.btn_recorder_recordering);
                    setText(R.string.str_recorder_recording);
                    if(isRecording){
                        //
                        mRecordDialogManager.recording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recordering);
                    setText(R.string.str_recorder_want_cancel);
                    mRecordDialogManager.wantToCancel();
                    break;
            }
        }
    }


}
