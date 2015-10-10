package cn.swang.utils;

import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;

import cn.swang.app.IConstants;

/**
 * Created by sw on 2015/9/14.
 */
public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private boolean isPrepared=false;
    private static AudioManager instanse;

    private AudioManager(String dir){
        mDir=dir;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener){
        mListener=listener;
    }

    public static AudioManager getInstanse(String dir){
        if(instanse==null){
            synchronized (AudioManager.class){
                if(instanse==null){
                    instanse=new AudioManager(dir);
                }
            }
        }
        return instanse;
    }

    public void prepareAudio(){
        try {
            isPrepared=false;
            File dir = new File(mDir);
            if(!dir.exists()){
                dir.mkdir();
            }
            String fileName = generateFileName();
            File file = new File(dir,fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.GINGERBREAD_MR1){
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            }else {
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            }
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared=true;
            if(mListener!=null){
                mListener.wellPrepared();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString()+ IConstants.AUDIO_RECORD_SUFFIX;
    }

    public int getVoiceLevel(int maxLevel){
        if(isPrepared){
            try {
                if(mMediaRecorder==null) return 1;
                //1-32767
                return maxLevel*mMediaRecorder.getMaxAmplitude()/32768+1;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 1;
    }

    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder=null;
    }

    public void cancel(){
        release();
        if(mCurrentFilePath!=null){
            File file=new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath=null;
        }
    }
}
