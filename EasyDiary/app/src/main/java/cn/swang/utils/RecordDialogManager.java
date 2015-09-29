package cn.swang.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.swang.R;

/**
 * Created by sw on 2015/9/14.
 */
public class RecordDialogManager {

    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLabel;
    private Context mContext;

    public RecordDialogManager(Context context){
        mContext=context;
    }

    public void showRecordingDialog(){
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);
        mIcon = (ImageView)mDialog.findViewById(R.id.id_recorder_dialog_icon);
        mVoice = (ImageView)mDialog.findViewById(R.id.id_recorder_dialog_voice);
        mLabel = (TextView)mDialog.findViewById(R.id.id_recorder_dialog_label);
        mDialog.show();
    }



    public void recording(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.tv_recorder_want_cancel);
        }
    }

    public void wantToCancel(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.str_recorder_want_cancel);
        }
    }
    public void tooShort(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText(R.string.tv_recorder_too_short);
        }
    }
    public void dimissDialog(){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            mDialog=null;
        }
    }

    public void updateVoiceLevel(int level){
        if(mDialog!=null&&mDialog.isShowing()){
//            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
//            mLabel.setVisibility(View.VISIBLE);
            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
