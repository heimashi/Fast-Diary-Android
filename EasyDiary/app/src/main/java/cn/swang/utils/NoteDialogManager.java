package cn.swang.utils;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.app.IConstants;
import cn.swang.dao.DbService;
import cn.swang.entity.NoteCard;
import cn.swang.ui.fragment.ListFragment;
import cn.swang.ui.view.MyDialog;

/**
 * Created by sw on 2015/9/14.
 */
public class NoteDialogManager {

    public static final int TEXT_DIALOG = 111;
    public static final int PHOTO_DIALOG = 112;
    public static final int AUDIO_DIALOG = 113;
    private MyDialog mDialog;
    private Context mContext;
    private String textDatas[] = {GlobalData.app().getString(R.string.note_dialog_item1), GlobalData.app().getString(R.string.note_dialog_item2), GlobalData.app().getString(R.string.note_dialog_item3), GlobalData.app().getString(R.string.note_dialog_item4)};
    private String photoDatas[] = {GlobalData.app().getString(R.string.note_dialog_item1), GlobalData.app().getString(R.string.note_dialog_item3),GlobalData.app().getString(R.string.note_dialog_item5)};
    private String audioDatas[] = {GlobalData.app().getString(R.string.note_dialog_item1),GlobalData.app().getString(R.string.note_dialog_item5)};
    public int DIALOG_STATE;
    private String datas[];
    private ListView mListView;
    private NoteCard mNoteCard;

    public NoteDialogManager(Context context, NoteCard mNoteCard) {
        mContext = context;
        this.mNoteCard = mNoteCard;
        if (!TextUtils.isEmpty(mNoteCard.getContent())) {
            DIALOG_STATE = NoteDialogManager.TEXT_DIALOG;
        } else if (!TextUtils.isEmpty(mNoteCard.getImgPath())) {
            DIALOG_STATE = NoteDialogManager.PHOTO_DIALOG;
        } else {
            DIALOG_STATE = NoteDialogManager.AUDIO_DIALOG;
        }
        switch (DIALOG_STATE) {
            case TEXT_DIALOG:
                datas = textDatas;
                break;
            case PHOTO_DIALOG:
                datas = photoDatas;
                break;
            case AUDIO_DIALOG:
                datas = audioDatas;
                break;
        }
    }

    private NoteDialogAdapter mAdapter = null;

    public interface NoteDialogHandle {
        void deleteNote(NoteCard noteCard);
        void updateNote(NoteCard noteCard);
    }

    private NoteDialogHandle noteDialogHandle=null;

    public void setNoteDialogHandle(NoteDialogHandle handle){
        this.noteDialogHandle=handle;
    }

    public void showNoteLongClickDialog() {
        mDialog = new MyDialog(mContext, R.style.Theme_AudioDialog);
        mDialog.setAnimation(R.style.dialogWindowAnim);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_note_layout, null);
        mDialog.setContentView(view);
        mListView = (ListView) mDialog.findViewById(R.id.dialog_note_list_view);
        mAdapter = new NoteDialogAdapter();
        mListView.setAdapter(mAdapter);
        mDialog.setCanceledOnTouchOutside(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select = datas[position];
                if (select.equals(textDatas[0])) {
                    if(noteDialogHandle!=null){
                        noteDialogHandle.deleteNote(mNoteCard);
                    }
                } else if (select.equals(textDatas[1])) {
                    if(noteDialogHandle!=null){
                        noteDialogHandle.updateNote(mNoteCard);
                    }
                } else if (select.equals(textDatas[2])) {
                    if (DIALOG_STATE == TEXT_DIALOG) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, mNoteCard.getContent());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(Intent.createChooser(intent, "请选择"));
                    } else if (DIALOG_STATE == PHOTO_DIALOG) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        File f = new File(mNoteCard.getImgPath());
                        if (f != null && f.exists() && f.isFile()) {
                            intent.setType("image/jpg");
                            Uri uri = Uri.fromFile(f);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(Intent.createChooser(intent, "请选择"));
                        }
                    }
                } else if (select.equals(textDatas[3])) {
                    ClipboardManager clipboardManager =(ClipboardManager) GlobalData.app().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setText(mNoteCard.getContent());
                    Toast.makeText(mContext,mContext.getString(R.string.already_copy_to_clip),Toast.LENGTH_SHORT).show();
                    //Snackbar.make(,mContext.getString(R.string.already_copy_to_clip),Snackbar.LENGTH_SHORT).show();
                } else if(select.equals(photoDatas[2])){
                    if(DIALOG_STATE==PHOTO_DIALOG){
                        File fromFile = new File(mNoteCard.getImgPath());
                        File toFile = new File(Environment.getExternalStorageDirectory()+IConstants.SHARE_SAVE_PHOTO_PATH+fromFile.getName());
                        try {
                            CommonUtils.copyFile(fromFile, toFile);
                            Toast.makeText(mContext,mContext.getString(R.string.already_save_to_title)+" "+toFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(DIALOG_STATE==AUDIO_DIALOG){
                        File fromFile = new File(mNoteCard.getVoicePath());
                        File toFile = new File(Environment.getExternalStorageDirectory()+IConstants.SHARE_SAVE_AUDIO_PATH+fromFile.getName());
                        try {
                            CommonUtils.copyFile(fromFile, toFile);
                            Toast.makeText(mContext,mContext.getString(R.string.already_save_to_title)+" "+toFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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

    class NoteDialogAdapter extends BaseAdapter {

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
