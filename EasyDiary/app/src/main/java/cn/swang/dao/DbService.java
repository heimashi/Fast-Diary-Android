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

    public void deleteNote(List<NoteCard> noteCardList, DeleteNoteListener deleteNoteListener) {
        new DeleteNoteAsyncTask(noteCardList,deleteNoteListener).execute();
    }

    public void deleteDayCard(List<DayCard> dayCardList, DeleteDayCardListener deleteDayCardListener) {
        new DeleteDayCardAsyncTask(dayCardList,deleteDayCardListener).execute();
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

    public interface DeleteDayCardListener{
        void onDeleteDayCardSuccess(List<DayCard> list);
    }

    class DeleteDayCardAsyncTask extends AsyncTask<Void, Void, List<DayCard>> {
        List<DayCard> dayCardList;
        DeleteDayCardListener dayCardListener;

        public DeleteDayCardAsyncTask(List<DayCard> dayCardList, DeleteDayCardListener dayCardListener) {
            this.dayCardList = dayCardList;
            this.dayCardListener = dayCardListener;
        }

        @Override
        protected List<DayCard> doInBackground(Void... params) {
            if (dayCardList == null||dayCardList.size()==0) return null;
            for(DayCard mDay: dayCardList){
                for(NoteCard mNote: mDay.getNoteSet()){
                    noteCardDao.delete(mNote);
                }
                dayCardDao.delete(mDay.getDay_id());
            }
            List<DayCard> datas = dayCardDao.fecthAll();
            for (DayCard dayCard : datas) {
                dayCard.setNoteSet(noteCardDao.findAllByDayid(dayCard));
            }
            return datas;
        }

        @Override
        protected void onPostExecute(List<DayCard> list) {
            super.onPostExecute(list);
            if (dayCardListener != null) {
                dayCardListener.onDeleteDayCardSuccess(list);
            }
        }
    }

    public interface DeleteNoteListener {
        void onDeleteSuccess(List<NoteCard> list);

    }

    class DeleteNoteAsyncTask extends AsyncTask<Void, Void, List<NoteCard>> {
        List<NoteCard> noteCardList;
        DeleteNoteListener noteListener;

        public DeleteNoteAsyncTask(List<NoteCard> noteCard, DeleteNoteListener noteListener) {
            this.noteCardList = noteCard;
            this.noteListener = noteListener;
        }

        @Override
        protected List<NoteCard> doInBackground(Void... params) {
            if (noteCardList == null||noteCardList.size()==0) return null;
            DayCard dayCard = new DayCard();
            dayCard.setDay_id(noteCardList.get(0).getDay_id());
            for(NoteCard noteCard: noteCardList){
                noteCardDao.delete(noteCard);
            }
            List<NoteCard> lists = noteCardDao.findAllByDayid(dayCard);
            if (lists == null || lists.size() == 0) {
                dayCardDao.delete(dayCard.getDay_id());
            }
            return lists;
        }

        @Override
        protected void onPostExecute(List<NoteCard> list) {
            super.onPostExecute(list);
            if (noteListener != null) {
                noteListener.onDeleteSuccess(list);
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


