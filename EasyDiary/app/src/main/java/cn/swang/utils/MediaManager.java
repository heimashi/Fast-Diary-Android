package cn.swang.utils;

import android.media.*;
import android.media.AudioManager;

/**
 * Created by sw on 2015/9/14.
 */
public class MediaManager {

    private static MediaManager instance;

    private MediaManager(){

    }

    public static MediaManager getInstance(){
        if(instance==null){
            synchronized(MediaManager.class){
                if(instance==null){
                    instance=new MediaManager();
                }
            }
        }
        return instance;
    }

    private MediaPlayer mMediaPlayer;

    private boolean isPause;

    public void playSound(String audioPath,MediaPlayer.OnCompletionListener listener){
        try{
            if(mMediaPlayer==null){
                mMediaPlayer=new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mMediaPlayer.reset();
                        return false;
                    }
                });
            }else {
                mMediaPlayer.reset();
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setDataSource(audioPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPause=true;
        }
    }

    public void resume(){
        if(mMediaPlayer!=null&&isPause){
            mMediaPlayer.start();
            isPause=false;
        }
    }

    public void release(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }
}
