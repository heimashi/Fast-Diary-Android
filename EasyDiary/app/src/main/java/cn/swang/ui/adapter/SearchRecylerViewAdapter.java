package cn.swang.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.rey.material.app.ThemeManager;

import java.util.List;

import cn.swang.R;
import cn.swang.entity.NoteCard;
import cn.swang.ui.activity.DetailActivity;

public class SearchRecylerViewAdapter extends RecyclerView.Adapter<SearchRecylerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<NoteCard> datas;

    public SearchRecylerViewAdapter(Context mContext, List<NoteCard> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public void setDatas(List<NoteCard> datas){
        this.datas=datas;
    }

    @Override
    public SearchRecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchRecylerViewAdapter.ViewHolder holder, final int position) {
        final NoteCard card = datas.get(position);
        holder.mTextView.setText(card.getContent());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                //intent.putExtra(INTENT_DAYCARD_EXTRAS, datas.get(holder.getAdapterPosition()).getDayCard());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView = (TextView) view.findViewById(R.id.search_note_tv);
        }
    }
}



