package cn.swang.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import cn.swang.R;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.activity.DetailActivity;
import cn.swang.utils.DisplayUtils;
import cn.swang.utils.ImageLoaderHelper;

public class StaggeredAdapter extends RecyclerView.Adapter<StaggeredAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<DayCardWrapper> datas;
    private int baseHeight = 120;
    private int height = 80;
    public static final String INTENT_DAYCARD_EXTRAS = "intent_daycard_extras";

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public StaggeredAdapter(Context mContext, List<DayCardWrapper> datas) {
        this.mContext = mContext;
        this.datas=datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public StaggeredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_past, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final StaggeredAdapter.ViewHolder holder, final int position) {
        DayCardWrapper dayCardWrapper=datas.get(position);
        DayCard dayCard =dayCardWrapper.getDayCard();
        final View view = holder.mView;
        if(onItemLongClickListener!=null){
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(v,holder.getAdapterPosition());
                    return false;
                }
            });
        }
        String dayString = dayCard.getYear()+"/"+dayCard.getMouth()+"/"+dayCard.getDay();
        holder.mTextView.setText(dayString);
        updateNoteView(holder, dayCard.getNoteSet());
        if(dayCardWrapper.isSelected()){
            ((CardView)holder.mView).setCardBackgroundColor(mContext.getResources().getColor(R.color.alpha_50_sr_color_primary));
        }else {
            ((CardView)holder.mView).setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        if(!TextUtils.isEmpty(dayCard.getDayImagePath())){
            holder.mImageItem.setVisibility(View.VISIBLE);
            String imageUrl = ImageDownloader.Scheme.FILE.wrap(dayCard.getDayImagePath());
            ImageLoader.getInstance().displayImage(imageUrl,holder.mImageItem, ImageLoaderHelper.getInstance(mContext).getDisplayOptions(4));
//            holder.mImageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //holder.mImageItem.setImageResource(R.drawable.image_loading);
//            ImageLoader.getInstance().loadImage(imageUrl, ImageLoaderHelper.getInstance(mContext).getDisplayOptions(),new SimpleImageLoadingListener(){
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    if(loadedImage!=null){
//                        //Bitmap croppedBitmap = ThumbnailUtils.extractThumbnail(loadedImage, DisplayUtils.dip2px(295), DisplayUtils.dip2px(211));
//                        //Bitmap roundedCropped = DisplayUtils.getRoundedCornerBitmap(loadedImage, 50);
//                        holder.mImageItem.setImageBitmap(loadedImage);
//                    }
//                }
//            });
        }else {
            holder.mImageItem.setVisibility(View.GONE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", 20, 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra(INTENT_DAYCARD_EXTRAS, datas.get(holder.getAdapterPosition()).getDayCard());
                        mContext.startActivity(intent);
                    }
                });
                animator.start();
            }
        });
    }

    void updateNoteView(ViewHolder holder,List<NoteCard> mList){
        int index = 0, t=0;
        TextView tvList[] = {holder.mTvItem1,holder.mTvItem2,holder.mTvItem3,holder.mTvItem4,holder.mTvItem5,holder.mTvItem6};
        while (mList!=null&&index<mList.size()&&t<6){
            if(!TextUtils.isEmpty(mList.get(index).getContent())){
                tvList[t].setVisibility(View.VISIBLE);
                tvList[t++].setText(mList.get(index).getContent());
            }else if(!TextUtils.isEmpty(mList.get(index).getVoicePath())){
                tvList[t].setVisibility(View.VISIBLE);
                tvList[t++].setText("语音 "+mList.get(index).getVoiceLength()+"''");
            }
            index++;
        }
        for(int i=t; i<6; i++){
            tvList[i].setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTextView;
        public TextView mTvItem1;
        public TextView mTvItem2;
        public TextView mTvItem3;
        public TextView mTvItem4;
        public TextView mTvItem5;
        public TextView mTvItem6;
        public ImageView mImageItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView= (TextView)view.findViewById(R.id.today_note_tv);
            mTvItem1=(TextView)view.findViewById(R.id.note_item_1);
            mTvItem2=(TextView)view.findViewById(R.id.note_item_2);
            mTvItem3=(TextView)view.findViewById(R.id.note_item_3);
            mTvItem4=(TextView)view.findViewById(R.id.note_item_4);
            mTvItem5=(TextView)view.findViewById(R.id.note_item_5);
            mTvItem6=(TextView)view.findViewById(R.id.note_item_6);
            mImageItem=(ImageView)view.findViewById(R.id.note_item_image_view);
        }
    }

    public static class DayCardWrapper {
        private DayCard dayCard;
        private boolean isSelected;

        public DayCardWrapper(DayCard dayCard) {
            this.dayCard = dayCard;
            this.isSelected = false;
        }

        public DayCard getDayCard() {
            return dayCard;
        }

        public void setDayCard(DayCard dayCard) {
            this.dayCard = dayCard;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

}
