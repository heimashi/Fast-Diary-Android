package cn.swang.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.entity.NoteCard;
import cn.swang.ui.activity.DetailActivity;
import cn.swang.ui.activity.ImageDetailActivity;
import cn.swang.utils.ImageLoaderHelper;
import cn.swang.utils.MediaManager;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<NoteCardWrapper> datas;
    private View animView;

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public RecyclerViewAdapter(Context mContext, List<NoteCardWrapper> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_today, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
        final View view = holder.mView;
        if (onItemLongClickListener != null) {
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(v, holder.getAdapterPosition());
                    return false;
                }
            });
        }
        final NoteCardWrapper cardWrapper = datas.get(position);
        final NoteCard card=cardWrapper.getNoteCard();
        if(cardWrapper.isSelected()){
            ((CardView)holder.mView).setCardBackgroundColor(mContext.getResources().getColor(R.color.alpha_50_sr_color_primary));
        }else {
            ((CardView)holder.mView).setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        if (!TextUtils.isEmpty(card.getContent())) {
            holder.mRecoderView.setVisibility(View.GONE);
            holder.mImageView.setVisibility(View.GONE);
            holder.mTextView.setVisibility(View.VISIBLE);
            holder.mTextView.setText(card.getContent());
        } else if (!TextUtils.isEmpty(card.getImgPath())) {
            holder.mRecoderView.setVisibility(View.GONE);
            holder.mTextView.setVisibility(View.GONE);
            holder.mImageView.setVisibility(View.VISIBLE);
            final String imageUrl = ImageDownloader.Scheme.FILE.wrap(card.getImgPath());
            ImageLoader.getInstance().displayImage(imageUrl, holder.mImageView, ImageLoaderHelper.getInstance(mContext).getDisplayOptions());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.mImageView.getVisibility()==View.VISIBLE){
                        Intent intent = new Intent(mContext, ImageDetailActivity.class);
                        intent.putExtra(ImageDetailActivity.DETAIL_IMAGE_PATH, imageUrl);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else if (!TextUtils.isEmpty(card.getVoicePath())) {
            holder.mTextView.setVisibility(View.GONE);
            holder.mImageView.setVisibility(View.GONE);
            holder.mRecoderView.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            sb.append("      ");
            for (int i = 0; i < card.getVoiceLength() && i < 10; i++) {
                sb.append("   ");
            }
            holder.mRecoderTime.setText(sb.toString() + card.getVoiceLength() + "''");
            final String recorderPath = card.getVoicePath();
            holder.mRecoder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (animView != null) {
                        animView.setBackgroundResource(R.drawable.adj);
                        animView = null;
                    }
                    animView = v;
                    animView.setBackgroundResource(R.drawable.audio_play);
                    AnimationDrawable animationDrawable = (AnimationDrawable) animView.getBackground();
                    animationDrawable.start();
                    //
                    MediaManager.getInstance().playSound(recorderPath, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            animView.setBackgroundResource(R.drawable.adj);
                        }
                    });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout mRecoderView;
        public ImageView mRecoder;
        public TextView mRecoderTime;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.today_note_iv);
            mTextView = (TextView) view.findViewById(R.id.today_note_tv);
            mRecoderView = (LinearLayout) view.findViewById(R.id.id_recorder_layout);
            mRecoder = (ImageView) view.findViewById(R.id.id_recorder_anim);
            mRecoderTime = (TextView) view.findViewById(R.id.id_recorder_time);
        }
    }

    public static class NoteCardWrapper {
        private NoteCard noteCard;
        private boolean isSelected;

        public NoteCardWrapper(NoteCard noteCard) {
            this.noteCard = noteCard;
            this.isSelected = false;
        }

        public NoteCard getNoteCard() {
            return noteCard;
        }

        public void setNoteCard(NoteCard noteCard) {
            this.noteCard = noteCard;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }
}



