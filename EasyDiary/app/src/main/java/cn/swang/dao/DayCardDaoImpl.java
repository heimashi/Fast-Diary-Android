package cn.swang.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cn.swang.entity.DayCard;

/**
 * Created by sw on 2015/9/11.
 */
public class DayCardDaoImpl implements DayCardDao {

    private final SQLiteOpenHelper sqLiteOpenHelper;
    private static final String WHERE_ID_CLAUSE = DayCardDao.COLUMN_NAME_DAY_ID + " = ?";

    public DayCardDaoImpl(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper=sqLiteOpenHelper;
    }

    @Override
    public void insert(DayCard dayCard) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DayCardDao.COLUMN_NAME_YEAR, dayCard.getYear());
            values.put(DayCardDao.COLUMN_NAME_MOUTH, dayCard.getMouth());
            values.put(DayCardDao.COLUMN_NAME_DAY, dayCard.getDay());
            values.put(DayCardDao.COLUMN_NAME_DAYIMAGEPATH,dayCard.getDayImagePath());
            long rowId = database.insert(DayCardDao.TABLE_NAME, null, values);
            dayCard.setDay_id(rowId);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public void update(DayCard dayCard) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DayCardDao.COLUMN_NAME_YEAR, dayCard.getYear());
            values.put(DayCardDao.COLUMN_NAME_MOUTH, dayCard.getMouth());
            values.put(DayCardDao.COLUMN_NAME_DAY, dayCard.getDay());
            values.put(DayCardDao.COLUMN_NAME_DAYIMAGEPATH,dayCard.getDayImagePath());
            String[] whereArgs = {String.valueOf(dayCard.getDay_id())};
            database.update(DayCardDao.TABLE_NAME, values, WHERE_ID_CLAUSE, whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public void delete(long dayid) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            String[] whereArgs = {String.valueOf(dayid)};
            database.delete(DayCardDao.TABLE_NAME, WHERE_ID_CLAUSE, whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public List<DayCard> fecthAll() {
        ArrayList<DayCard> result = null;
        Cursor cursor = null;
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        try {
            String[] columns = {DayCardDao.COLUMN_NAME_DAY_ID,
                    DayCardDao.COLUMN_NAME_YEAR,
                    DayCardDao.COLUMN_NAME_MOUTH,
                    DayCardDao.COLUMN_NAME_DAY,
                    DayCardDao.COLUMN_NAME_DAYIMAGEPATH};
            String orderby = DayCardDao.COLUMN_NAME_YEAR + " desc,"+DayCardDao.COLUMN_NAME_MOUTH+" desc,"+DayCardDao.COLUMN_NAME_DAY+" desc";
            cursor = database.query(DayCardDao.TABLE_NAME, columns, null, null, null, null, orderby);
            result = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                DayCard dayCard = new DayCard();
                dayCard.setDay_id(cursor.getLong(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_DAY_ID)));
                dayCard.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_YEAR)));
                dayCard.setMouth(cursor.getInt(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_MOUTH)));
                dayCard.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_DAY)));
                dayCard.setDayImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_DAYIMAGEPATH)));
                result.add(dayCard);
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
    public DayCard findByParams(int year, int mouth, int day) {
        DayCard dayCard = null;
        Cursor cursor = null;
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        try {
            String[] columns = {DayCardDao.COLUMN_NAME_DAY_ID,
                    DayCardDao.COLUMN_NAME_YEAR,
                    DayCardDao.COLUMN_NAME_MOUTH,
                    DayCardDao.COLUMN_NAME_DAY,
                    DayCardDao.COLUMN_NAME_DAYIMAGEPATH};
            String WHERE_CLAUSE = DayCardDao.COLUMN_NAME_YEAR+ " = ? AND "+DayCardDao.COLUMN_NAME_MOUTH+ " = ? AND "+DayCardDao.COLUMN_NAME_DAY+ " = ?";
            String[] whereArgs = {String.valueOf(year),String.valueOf(mouth),String.valueOf(day)};
            cursor = database.query(DayCardDao.TABLE_NAME, columns, WHERE_CLAUSE, whereArgs, null, null, null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                if ( !cursor.isAfterLast()) {
                    dayCard=new DayCard();
                    dayCard.setDay_id(cursor.getLong(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_DAY_ID)));
                    dayCard.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_YEAR)));
                    dayCard.setMouth(cursor.getInt(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_MOUTH)));
                    dayCard.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_DAY)));
                    dayCard.setDayImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DayCardDao.COLUMN_NAME_DAYIMAGEPATH)));
                }
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
        return dayCard;
    }
}
