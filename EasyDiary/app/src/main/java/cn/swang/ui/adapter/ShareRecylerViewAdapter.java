package cn.swang.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.activity.DetailActivity;
import cn.swang.ui.activity.ShareDayCardActivity;
import cn.swang.utils.ShareBitmapUtils;

public class ShareRecylerViewAdapter extends RecyclerView.Adapter<ShareRecylerViewAdapter.ViewHolder> implements ShareBitmapUtils.ConvertDayCardListener{

    private Context mContext;
    private List<DayCard> datas;

    public ShareRecylerViewAdapter(Context mContext, List<DayCard> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public void setDatas(List<DayCard> datas){
        this.datas=datas;
    }

    @Override
    public ShareRecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share, parent, false);
        return new ViewHolder(view);
    }

    boolean isGeneratingBitmap = false;

    @Override
    public void onBindViewHolder(final ShareRecylerViewAdapter.ViewHolder holder, final int position) {
        final DayCard card = datas.get(position);
        int count = card.getNoteSet().size();
        String str = String.format(GlobalData.app().getString(R.string.share_day_card_count_title), count);
        String title = card.getYear()+"/"+card.getMouth()+"/"+card.getDay();
        holder.mTextView.setText(title);
        holder.mCountView.setText(str);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, DetailActivity.class);
//                DayCard dayCard = new DayCard();
//                dayCard.setDay_id(card.getDay_id());
//                intent.putExtra(StaggeredAdapter.INTENT_DAYCARD_EXTRAS, dayCard);
//                intent.putExtra(DetailActivity.START_ACTIVITY_DAY_CARD_NEED_UPDATE,true);
//                mContext.startActivity(intent);
                if (!isGeneratingBitmap) {
                    isGeneratingBitmap = true;
                    Snackbar.make(v, mContext.getString(R.string.detail_activity_generate_bitmap), Snackbar.LENGTH_SHORT).show();
                    ShareBitmapUtils bitmapUtils = new ShareBitmapUtils();
                    bitmapUtils.convertDayCardBitmap(ShareRecylerViewAdapter.this, card);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onConvertSuccess(String imagePath, DayCard dayCard) {
        isGeneratingBitmap = false;
        Intent intent = new Intent(mContext, ShareDayCardActivity.class);
        intent.putExtra(ShareDayCardActivity.SHARE_IMAGE_PATH, imagePath);
        intent.putExtra(ShareDayCardActivity.SHARE_DAYCARD_PRE, dayCard);
        mContext.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTextView,mCountView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView = (TextView) view.findViewById(R.id.share_daycard_tv);
            mCountView = (TextView) view.findViewById(R.id.share_daycard_size_tv);
        }
    }
}



