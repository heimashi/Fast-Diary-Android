package cn.swang.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.ui.view.MyDialog;

/**
 * Created by sw on 2015/9/14.
 */
public class DayCardDialogManager {

    private MyDialog mDialog;
    private Context mContext;
    private String datas[] = {GlobalData.app().getString(R.string.note_dialog_item1)};
    ;
    private ListView mListView;
    private DayCard mDayCard;

    public DayCardDialogManager(Context context, DayCard dayCard) {
        mContext = context;
        this.mDayCard = dayCard;
    }

    private DayCardDialogAdapter mAdapter = null;

    public interface DayCardDialogHandle {
        void deleteDayCard(DayCard dayCard);
    }

    private DayCardDialogHandle dayCardDialogHandle = null;

    public void setDayCardDialogHandle(DayCardDialogHandle dayCardDialogHandle) {
        this.dayCardDialogHandle = dayCardDialogHandle;
    }

    public void showDayCardLongClickDialog() {
        mDialog = new MyDialog(mContext, R.style.Theme_AudioDialog);
        mDialog.setAnimation(R.style.dialogWindowAnim);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_note_layout, null);
        mDialog.setContentView(view);
        mListView = (ListView) mDialog.findViewById(R.id.dialog_note_list_view);
        mAdapter = new DayCardDialogAdapter();
        mListView.setAdapter(mAdapter);
        mDialog.setCanceledOnTouchOutside(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select = datas[position];
                if (dayCardDialogHandle != null) {
                    dayCardDialogHandle.deleteDayCard(mDayCard);
                }
                dimissDialog();
            }
        });
        mDialog.show();
    }

    public void setDismissCallBack(MyDialog.DialogDismissCallBack callBack) {
        if (mDialog != null) {
            mDialog.setDialogDismissCallBack(callBack);
        }
    }

    public boolean isDialogShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    class DayCardDialogAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_card_dialog, null);
            TextView tv = (TextView) convertView.findViewById(R.id.id_dialog_note_tv);
            tv.setText(datas[position]);
            return convertView;
        }
    }

}
