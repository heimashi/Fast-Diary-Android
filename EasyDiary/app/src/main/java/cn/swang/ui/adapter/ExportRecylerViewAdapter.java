package cn.swang.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.app.IConstants;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.activity.ShareDayCardActivity;
import cn.swang.utils.CommonUtils;
import cn.swang.utils.ShareBitmapUtils;

public class ExportRecylerViewAdapter extends RecyclerView.Adapter<ExportRecylerViewAdapter.ViewHolder> implements ShareBitmapUtils.ConvertDayCardListener{

    private Context mContext;
    private List<DayCard> datas;

    public ExportRecylerViewAdapter(Context mContext, List<DayCard> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public void setDatas(List<DayCard> datas){
        this.datas=datas;
    }

    @Override
    public ExportRecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share, parent, false);
        ImageView imageView = ((ImageView) view.findViewById(R.id.list_item_img));
        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.save_img_black);
        return new ViewHolder(view);
    }

    boolean isGeneratingBitmap = false;

    @Override
    public void onBindViewHolder(final ExportRecylerViewAdapter.ViewHolder holder, final int position) {
        final DayCard card = datas.get(position);
        int count = card.getNoteSet().size();
        String str = String.format(GlobalData.app().getString(R.string.share_day_card_count_title), count);
        String title = card.getYear()+"/"+card.getMouth()+"/"+card.getDay();
        holder.mTextView.setText(title);
        holder.mCountView.setText(str);
        cacheView = holder.mView;
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
                    bitmapUtils.convertDayCardBitmap(ExportRecylerViewAdapter.this, card, true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String fileOutPutPath = Environment.getExternalStorageDirectory()+ IConstants.SHARE_DIARY_LIST_PATH+card.getYear() + "_"+card.getMouth() + "_"+card.getDay()+"/";
                            int count = 0;
                            for(NoteCard noteCard:card.getNoteSet()){
                                if(!TextUtils.isEmpty(noteCard.getVoicePath())){
                                    count++;
                                    try {
                                        CommonUtils.copyFile(noteCard.getVoicePath(), fileOutPutPath+count+IConstants.AUDIO_RECORD_SUFFIX);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();
                }
            }
        });
    }

    private View cacheView;

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onConvertSuccess(String imagePath) {
        isGeneratingBitmap = false;
        //Toast.makeText(mContext,mContext.getString(R.string.already_save_to_title)+imagePath,Toast.LENGTH_LONG).show();
        Snackbar.make(cacheView, mContext.getString(R.string.already_save_to_title)+imagePath, Snackbar.LENGTH_LONG).show();
//        Intent intent = new Intent(mContext, ShareDayCardActivity.class);
//        intent.putExtra(ShareDayCardActivity.SHARE_IMAGE_PATH, imagePath);
//        mContext.startActivity(intent);
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



