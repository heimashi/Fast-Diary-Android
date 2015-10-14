package cn.swang.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.swang.R;
import cn.swang.app.GlobalData;

/**
 * Created by sw on 2015/9/14.
 */
public class FastDiaryTipsView extends LinearLayout{

    private ImageView mImageView, mImageView2;
    private TextView mTextView, mTextView2;

    private final Timer timer = new Timer();
    private TimerTask task;
    private Handler handler;
    Animation inAnim,inAnim2,outAnim,outAnim2;

    public FastDiaryTipsView(Context context) {
        this(context, null);
    }


    public FastDiaryTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.empty_layout_2, this, true);
        mImageView = (ImageView) findViewById(R.id.empty_image_view);
        mTextView = (TextView) findViewById(R.id.empty_text_view);
        mImageView2 = (ImageView) findViewById(R.id.empty_image_view2);
        mTextView2 = (TextView) findViewById(R.id.empty_text_view2);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 111:
                        updateState();
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(111);
                    }
                };
                timer.schedule(task, 2000, 5000);
            }
        }).start();

        inAnim = AnimationUtils.loadAnimation(GlobalData.app(), R.anim.image_rotate);
        inAnim2 = AnimationUtils.loadAnimation(GlobalData.app(), R.anim.fade_in);
        outAnim = AnimationUtils.loadAnimation(GlobalData.app(), R.anim.image_rotate_back);
        outAnim2 = AnimationUtils.loadAnimation(GlobalData.app(), R.anim.fade_out);

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateState();
            }
        });
        mImageView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateState();
            }
        });
    }

    //ArrayList<String> array = new ArrayList<String>();
    String array[] = {GlobalData.app().getString(R.string.empty_tips1),GlobalData.app().getString(R.string.empty_tips2),GlobalData.app().getString(R.string.empty_tips3),GlobalData.app().getString(R.string.empty_tips4),GlobalData.app().getString(R.string.empty_tips5),GlobalData.app().getString(R.string.empty_tips6),GlobalData.app().getString(R.string.empty_tips7),GlobalData.app().getString(R.string.about_fast_diary_tip4)};

    int count = 0;
    public void updateState(){
        int size = array.length;
        if(count%2==0){
            mTextView2.setText(array[count++]);
            mTextView2.setVisibility(VISIBLE);
            mImageView2.setVisibility(VISIBLE);
            mTextView2.startAnimation(inAnim2);
            mImageView2.startAnimation(inAnim);
            mTextView.startAnimation(outAnim2);
            mImageView.startAnimation(outAnim);
            mTextView.setVisibility(GONE);
            mImageView.setVisibility(GONE);
        }else {
            mTextView.setText(array[count++]);
            mTextView.setVisibility(VISIBLE);
            mImageView.setVisibility(VISIBLE);
            mTextView.startAnimation(inAnim2);
            mImageView.startAnimation(inAnim);
            mTextView2.startAnimation(outAnim2);
            mImageView2.startAnimation(outAnim);
            mTextView2.setVisibility(GONE);
            mImageView2.setVisibility(GONE);
        }
        if(count==size){
            count=0;
        }
    }

}
