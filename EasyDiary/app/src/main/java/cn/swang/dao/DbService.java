package cn.swang.dao;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.swang.app.GlobalData;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;
import cn.swang.thread.ThreadPoolManager;

/**
 * Created by sw on 2015/9/11.
 */
public class DbService {

    private DayCardDao dayCardDao;
    private NoteCardDao noteCardDao;

    public DbService(Context context) {
        SQLiteOpenHelper sqLiteOpenHelper = MySqliteOpenHelper.getInstance(context);
        this.dayCardDao = new DayCardDaoImpl(sqLiteOpenHelper);
        this.noteCardDao = new NoteCardDaoImpl(sqLiteOpenHelper);
    }

    public void saveNoteWithCache(NoteCard noteCard, SaveNoteListener noteListener) {
        //ThreadPoolManager.execute(new NoteAsyncTask(noteCard,noteListener),ThreadPoolManager.ASYNC_EXECUTOR_LEVEL_LOCAL_IO);
        new SaveNoteWithCacheAsyncTask(noteCard, noteListener).execute();
    }

    public void saveNote(NoteCard noteCard, SaveNoteListener noteListener) {
        new SaveNoteAsyncTask(noteCard, noteListener).execute();
    }

    public void deleteNote(NoteCard noteCard) {

    }

    public void loadDiary(DayCard dayCard, LoadNoteListener loadNoteListener) {
        new LoadDiaryAsyncTask(dayCard, loadNoteListener).execute();
    }

    public void loadAllDiary(LoadDiaryListener loadDiaryListener) {
        new LoadAllDiaryAsyncTask(loadDiaryListener).execute();
    }

    class LoadDiaryAsyncTask extends AsyncTask<Void, Void, List<NoteCard>> {

        private LoadNoteListener loadNoteListener;
        private DayCard dayCard;

        public LoadDiaryAsyncTask(DayCard dayCard, LoadNoteListener loadNoteListener) {
            this.loadNoteListener = loadNoteListener;
            this.dayCard = dayCard;
        }

        @Override
        protected List<NoteCard> doInBackground(Void... params) {
            dayCard = dayCardDao.findByParams(dayCard.getYear(), dayCard.getMouth(), dayCard.getDay());
            if (dayCard == null) return null;
            return noteCardDao.findAllByDayid(dayCard);
        }

        @Override
        protected void onPostExecute(List<NoteCard> noteCards) {
            super.onPostExecute(noteCards);
            loadNoteListener.onLoadNoteSuccess(noteCards);
        }
    }

    class LoadAllDiaryAsyncTask extends AsyncTask<Void, Void, List<DayCard>> {

        private LoadDiaryListener loadDiaryListener;

        public LoadAllDiaryAsyncTask(LoadDiaryListener loadDiaryListener) {
            this.loadDiaryListener = loadDiaryListener;
        }

        @Override
        protected List<DayCard> doInBackground(Void... params) {
            List<DayCard> datas = dayCardDao.fecthAll();
            for (DayCard dayCard : datas) {
                dayCard.setNoteSet(noteCardDao.findAllByDayid(dayCard));
            }
            return datas;
        }

        @Override
        protected void onPostExecute(List<DayCard> dayCards) {
            super.onPostExecute(dayCards);
            loadDiaryListener.onLoadDiarySuccess(dayCards);
        }
    }

    class SaveNoteWithCacheAsyncTask extends AsyncTask<Void, Void, List<NoteCard>> {
        NoteCard noteCard;
        SaveNoteListener noteListener;

        public SaveNoteWithCacheAsyncTask(NoteCard noteCard, SaveNoteListener noteListener) {
            this.noteCard = noteCard;
            this.noteListener = noteListener;
        }

        @Override
        protected List<NoteCard> doInBackground(Void... params) {
            if (noteCard == null || noteListener == null) return null;
            DayCard dayCard = null;
            if (GlobalData.dayCard_id == -1) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(noteCard.getDate());
                int year = calendar.get(Calendar.YEAR);
                int mouth = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                dayCard = dayCardDao.findByParams(year, mouth, day);
                if (dayCard == null) {
                    dayCard = new DayCard(year, mouth, day);
                    dayCardDao.insert(dayCard);
                }
                GlobalData.dayCard_id = dayCard.getDay_id();
            }
            noteCard.setDay_id(GlobalData.dayCard_id);
            noteCardDao.insert(noteCard);
            if (dayCard == null) {
                dayCard = new DayCard(GlobalData.yearNow, GlobalData.monthNow, GlobalData.dayNow);
            }
            dayCard.setDay_id(GlobalData.dayCard_id);
            if (!TextUtils.isEmpty(noteCard.getImgPath())) {
                dayCard.setDayImagePath(noteCard.getImgPath());
                dayCardDao.update(dayCard);
            }
            return noteCardDao.findAllByDayid(dayCard);
        }

        @Override
        protected void onPostExecute(List<NoteCard> list) {
            super.onPostExecute(list);
            if (list == null || list.size() < 1) {
                noteListener.onSaveFailed();
            } else {
                noteListener.onSaveSuccess(list);
            }
        }
    }

    class SaveNoteAsyncTask extends AsyncTask<Void, Void, List<NoteCard>> {
        NoteCard noteCard;
        SaveNoteListener noteListener;

        public SaveNoteAsyncTask(NoteCard noteCard, SaveNoteListener noteListener) {
            this.noteCard = noteCard;
            this.noteListener = noteListener;
        }

        @Override
        protected List<NoteCard> doInBackground(Void... params) {
            if (noteCard == null) return null;
            DayCard dayCard = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(noteCard.getDate());
            int year = calendar.get(Calendar.YEAR);
            int mouth = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dayCard = dayCardDao.findByParams(year, mouth, day);
            if (dayCard == null) {
                dayCard = new DayCard(year, mouth, day);
                dayCardDao.insert(dayCard);
            }
            noteCard.setDay_id(dayCard.getDay_id());
            noteCardDao.insert(noteCard);
            if (!TextUtils.isEmpty(noteCard.getImgPath())) {
                dayCard.setDayImagePath(noteCard.getImgPath());
                dayCardDao.update(dayCard);
            }
            return noteCardDao.findAllByDayid(dayCard);
        }

        @Override
        protected void onPostExecute(List<NoteCard> list) {
            super.onPostExecute(list);
            if (noteListener != null) {
                if (list == null || list.size() < 1) {
                    noteListener.onSaveFailed();
                } else {
                    noteListener.onSaveSuccess(list);
                }
            }
        }
    }
}


