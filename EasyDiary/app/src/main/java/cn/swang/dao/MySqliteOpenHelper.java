package cn.swang.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sw on 2015/9/11.
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "easydiary.db";
    private static MySqliteOpenHelper instance;
    private final Context context;

    private static final String DAYCARD_TABLE_CREATE = "CREATE TABLE "
            + DayCardDao.TABLE_NAME + " ("
            + DayCardDao.COLUMN_NAME_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DayCardDao.COLUMN_NAME_YEAR + " INTEGER NOT NULL, "
            + DayCardDao.COLUMN_NAME_MOUTH + " INTEGER NOT NULL, "
            + DayCardDao.COLUMN_NAME_DAY + " INTEGER NOT NULL, "
            + DayCardDao.COLUMN_NAME_DAYIMAGEPATH + " TEXT); ";

    private static final String NOTECARD_TABLE_CREATE = "CREATE TABLE "
            + NoteCardDao.TABLE_NAME + " ("
            + NoteCardDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NoteCardDao.COLUMN_NAME_CONTENT + " TEXT, "
            + NoteCardDao.COLUMN_NAME_IMGPATH + " TEXT, "
            + NoteCardDao.COLUMN_NAME_VOICEPATH + " TEXT, "
            + NoteCardDao.COLUMN_NAME_VOICELENGTH + " INTEGER, "
            + NoteCardDao.COLUMN_NAME_DATE + " BIGINT NOT NULL, "
            + NoteCardDao.COLUMN_NAME_DAY_ID + " INTEGER NOT NULL); ";


    private MySqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static MySqliteOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MySqliteOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DAYCARD_TABLE_CREATE);
        db.execSQL(NOTECARD_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        context.deleteDatabase(DATABASE_NAME);
        onCreate(db);
    }
}
