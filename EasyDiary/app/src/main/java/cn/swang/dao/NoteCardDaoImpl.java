package cn.swang.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.swang.app.GlobalData;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;

/**
 * Created by sw on 2015/9/11.
 */
public class NoteCardDaoImpl implements  NoteCardDao{

    private final SQLiteOpenHelper sqLiteOpenHelper;
    private static final String WHERE_ID_CLAUSE = NoteCardDao.COLUMN_NAME_ID + " = ?";
    private static final String WHERE_DAY_ID_CLAUSE = NoteCardDao.COLUMN_NAME_DAY_ID + " = ?";

    public NoteCardDaoImpl(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper=sqLiteOpenHelper;
    }

    @Override
    public void insert(NoteCard noteCard) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(NoteCardDao.COLUMN_NAME_CONTENT, noteCard.getContent());
            values.put(NoteCardDao.COLUMN_NAME_DATE, noteCard.getDate().getTime());
            values.put(NoteCardDao.COLUMN_NAME_IMGPATH, noteCard.getImgPath());
            values.put(NoteCardDao.COLUMN_NAME_VOICEPATH, noteCard.getVoicePath());
            values.put(NoteCardDao.COLUMN_NAME_VOICELENGTH,noteCard.getVoiceLength());
            values.put(NoteCardDao.COLUMN_NAME_DAY_ID, noteCard.getDay_id());
            long rowId = database.insert(NoteCardDao.TABLE_NAME, null, values);
            noteCard.setId(rowId);
            database.setTransactionSuccessful();
            GlobalData.app().getContentResolver().notifyChange(Uri.parse(NOTE_CARD_URI_STRING+noteCard.getDay_id()),null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public void update(NoteCard noteCard) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(NoteCardDao.COLUMN_NAME_CONTENT, noteCard.getContent());
            values.put(NoteCardDao.COLUMN_NAME_DATE, noteCard.getDate().getTime());
            values.put(NoteCardDao.COLUMN_NAME_IMGPATH, noteCard.getImgPath());
            values.put(NoteCardDao.COLUMN_NAME_VOICEPATH, noteCard.getVoicePath());
            values.put(NoteCardDao.COLUMN_NAME_VOICELENGTH,noteCard.getVoiceLength());
            values.put(NoteCardDao.COLUMN_NAME_DAY_ID, noteCard.getDay_id());
            String[] whereArgs = {String.valueOf(noteCard.getId())};
            database.update(NoteCardDao.TABLE_NAME, values, WHERE_ID_CLAUSE, whereArgs);
            database.setTransactionSuccessful();
            GlobalData.app().getContentResolver().notifyChange(Uri.parse(NOTE_CARD_URI_STRING + noteCard.getDay_id()), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public void delete(NoteCard noteCard) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            String[] whereArgs = {String.valueOf(noteCard.getId())};
            database.delete(NoteCardDao.TABLE_NAME, WHERE_ID_CLAUSE, whereArgs);
            database.setTransactionSuccessful();
            GlobalData.app().getContentResolver().notifyChange(Uri.parse(NOTE_CARD_URI_STRING + noteCard.getDay_id()), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public List<NoteCard> fecthAll() {
        ArrayList<NoteCard> result = null;
        Cursor cursor = null;
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        try {
            String[] columns = {NoteCardDao.COLUMN_NAME_ID,
                    NoteCardDao.COLUMN_NAME_DATE,
                    NoteCardDao.COLUMN_NAME_CONTENT,
                    NoteCardDao.COLUMN_NAME_IMGPATH,
                    NoteCardDao.COLUMN_NAME_VOICEPATH,
                    NoteCardDao.COLUMN_NAME_VOICELENGTH,
                    NoteCardDao.COLUMN_NAME_DAY_ID};
            cursor = database.query(NoteCardDao.TABLE_NAME, columns, null, null, null, null, null);
            result = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                NoteCard noteCard = new NoteCard();
                noteCard.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_ID)));
                noteCard.setDate(new Date(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_DATE)));
                noteCard.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_CONTENT)));
                noteCard.setImgPath(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_IMGPATH)));
                noteCard.setVoicePath(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_VOICEPATH)));
                noteCard.setVoiceLength(cursor.getInt(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_VOICELENGTH)));
                noteCard.setDay_id(cursor.getLong(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_DAY_ID)));
                result.add(noteCard);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                    cursor=null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            database.close();
        }
        return result;
    }

    @Override
    public List<NoteCard> searchNoteCard(String input) {
        ArrayList<NoteCard> result = null;
        Cursor cursor = null;
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        try {
            String[] columns = {NoteCardDao.COLUMN_NAME_ID,
                    NoteCardDao.COLUMN_NAME_DATE,
                    NoteCardDao.COLUMN_NAME_CONTENT,
                    NoteCardDao.COLUMN_NAME_IMGPATH,
                    NoteCardDao.COLUMN_NAME_VOICEPATH,
                    NoteCardDao.COLUMN_NAME_VOICELENGTH,
                    NoteCardDao.COLUMN_NAME_DAY_ID};
            String[] whereArgs = {"%"+input+"%"};
            String WHERE_CLAUSE = NoteCardDao.COLUMN_NAME_CONTENT + " like ?";
            cursor = database.query(NoteCardDao.TABLE_NAME, columns, WHERE_CLAUSE, whereArgs, null, null, null);
            result = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                NoteCard noteCard = new NoteCard();
                noteCard.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_ID)));
                noteCard.setDate(new Date(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_DATE)));
                noteCard.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_CONTENT)));
                noteCard.setImgPath(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_IMGPATH)));
                noteCard.setVoicePath(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_VOICEPATH)));
                noteCard.setVoiceLength(cursor.getInt(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_VOICELENGTH)));
                noteCard.setDay_id(cursor.getLong(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_DAY_ID)));
                result.add(noteCard);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                    cursor=null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            database.close();
        }
        return result;
    }

    @Override
    public List<NoteCard> findAllByDayid(DayCard dayCard) {
        ArrayList<NoteCard> result = null;
        Cursor cursor = null;
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        try {
            String[] columns = {NoteCardDao.COLUMN_NAME_ID,
                    NoteCardDao.COLUMN_NAME_DATE,
                    NoteCardDao.COLUMN_NAME_CONTENT,
                    NoteCardDao.COLUMN_NAME_IMGPATH,
                    NoteCardDao.COLUMN_NAME_VOICEPATH,
                    NoteCardDao.COLUMN_NAME_VOICELENGTH,
                    NoteCardDao.COLUMN_NAME_DAY_ID};
            String[] whereArgs = {String.valueOf(dayCard.getDay_id())};
            cursor = database.query(NoteCardDao.TABLE_NAME, columns, WHERE_DAY_ID_CLAUSE, whereArgs, null, null, null);
            result = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                NoteCard noteCard = new NoteCard();
                noteCard.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_ID)));
                noteCard.setDate(new Date(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_DATE)));
                noteCard.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_CONTENT)));
                noteCard.setImgPath(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_IMGPATH)));
                noteCard.setVoicePath(cursor.getString(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_VOICEPATH)));
                noteCard.setVoiceLength(cursor.getInt(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_VOICELENGTH)));
                noteCard.setDay_id(cursor.getLong(cursor.getColumnIndexOrThrow(NoteCardDao.COLUMN_NAME_DAY_ID)));
                result.add(noteCard);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                    cursor=null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            database.close();
        }
        return result;
    }

}
