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

    public void saveNote(NoteCard noteCard) {
        new SaveNoteAsyncTask(noteCard).execute();
    }
    public void updateNote(NoteCard noteCard){
        new UpdateNoteAsyncTask(noteCard).execute();
    }

    public void loadDayCardByDayId(long dayId,LoadDayCardListener loadDayCardListener){
        new LoadDayCardByIDAsyncTask(dayId,loadDayCardListener).execute();
    }

    public void searchNote(String input,OnSearchNoteListener onSearchNoteListener){
        new SearchNoteAsyncTask(input,onSearchNoteListener).execute();
    }

    public void deleteNote(List<NoteCard> noteCardList,OnDeleteNoteListener onDeleteNoteListener) {
        new DeleteNoteAsyncTask(noteCardList,onDeleteNoteListener).execute();
    }

    public void deleteDayCard(List<DayCard> dayCardList, DeleteDayCardListener deleteDayCardListener) {
        new DeleteDayCardAsyncTask(dayCardList,deleteDayCardListener).execute();
    }

    public void loadTodayNote(DayCard dayCard, LoadTodayNoteListener loadNoteListener) {
        new LoadTodayDiaryAsyncTask(dayCard, loadNoteListener).execute();
    }

    public void loadAllDiary(LoadDiaryListener loadDiaryListener) {
        new LoadAllDiaryAsyncTask(loadDiaryListener).execute();
    }

    class LoadTodayDiaryAsyncTask extends AsyncTask<Void, Void, List<NoteCard>> {

        private LoadTodayNoteListener loadNoteListener;
        private DayCard dayCard;
        private long day_id=-1;

        public LoadTodayDiaryAsyncTask(DayCard dayCard, LoadTodayNoteListener loadNoteListener) {
            this.loadNoteListener = loadNoteListener;
            this.dayCard = dayCard;
        }

        @Override
        protected List<NoteCard> doInBackground(Void... params) {
            if(dayCard.getDay_id()==-1){
                dayCard = dayCardDao.findByParams(dayCard.getYear(), dayCard.getMouth(), dayCard.getDay());
            }
            if (dayCard == null||dayCard.getDay_id()==-1) return null;
            day_id=dayCard.getDay_id();
            return noteCardDao.findAllByDayid(dayCard);
        }

        @Override
        protected void onPostExecute(List<NoteCard> noteCards) {
            super.onPostExecute(noteCards);
            loadNoteListener.onLoadNoteSuccess(noteCards,day_id);
        }
    }
    public interface LoadTodayNoteListener {
        void onLoadNoteSuccess(List<NoteCard> list, long day_id);
    }

    public interface LoadDayCardListener {
        void onLoadDayCardSuccess(DayCard dayCard);
    }

    class LoadDayCardByIDAsyncTask extends AsyncTask<Void, Void, DayCard> {

        private LoadDayCardListener loadDayCardListener;
        private long day_id;

        public LoadDayCardByIDAsyncTask(long id,LoadDayCardListener loadDayCardListener) {
            this.loadDayCardListener = loadDayCardListener;
            this.day_id=id;
        }

        @Override
        protected DayCard doInBackground(Void... params) {
            DayCard dayCard = dayCardDao.findById(day_id);
            dayCard.setNoteSet(noteCardDao.findAllByDayid(dayCard));
            return dayCard;
        }

        @Override
        protected void onPostExecute(DayCard dayCard) {
            super.onPostExecute(dayCard);
            loadDayCardListener.onLoadDayCardSuccess(dayCard);
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

    public interface OnDeleteNoteListener {
        void onDeleteSuccess();
    }

    class DeleteNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        List<NoteCard> noteCardList;
        OnDeleteNoteListener onDeleteNoteListener;

        public DeleteNoteAsyncTask(List<NoteCard> noteCard,OnDeleteNoteListener onDeleteNoteListener) {
            this.noteCardList = noteCard;
            this.onDeleteNoteListener=onDeleteNoteListener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (noteCardList == null||noteCardList.size()==0) return null;
            DayCard dayCard = dayCardDao.findById(noteCardList.get(0).getDay_id());
            for(NoteCard noteCard: noteCardList){
                if(!TextUtils.isEmpty(dayCard.getDayImagePath())&&dayCard.getDayImagePath()   .equals(noteCard.getImgPath())){
                    dayCard.setDayImagePath("");
                }
                noteCardDao.delete(noteCard);
            }
            List<NoteCard> lists = noteCardDao.findAllByDayid(dayCard);
            if (lists == null || lists.size() == 0) {
                dayCardDao.delete(dayCard.getDay_id());
            }else {
                for(NoteCard note:lists){
                    if(!TextUtils.isEmpty(note.getImgPath())){
                        dayCard.setDayImagePath(note.getImgPath());
                        break;
                    }
                }
                dayCardDao.update(dayCard);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(onDeleteNoteListener!=null){
                onDeleteNoteListener.onDeleteSuccess();
            }
        }
    }

    public interface OnSearchNoteListener {
        void onSearchFinish(List<NoteCard> list);
    }
    class SearchNoteAsyncTask extends AsyncTask<Void,Void,List<NoteCard>>{
        String input;
        OnSearchNoteListener onSearchNoteListener;
        public SearchNoteAsyncTask(String input,OnSearchNoteListener onSearchNoteListener){
            this.input=input;
            this.onSearchNoteListener=onSearchNoteListener;
        }
        @Override
        protected List<NoteCard> doInBackground(Void... params) {
            if(TextUtils.isEmpty(input)) return null;
            return noteCardDao.searchNoteCard(input);
        }
        @Override
        protected void onPostExecute(List<NoteCard> list) {
            super.onPostExecute(list);
            if(onSearchNoteListener!=null){
                onSearchNoteListener.onSearchFinish(list);
            }
        }
    }

    class UpdateNoteAsyncTask extends AsyncTask<Void,Void,Void>{
        NoteCard noteCard;
        public UpdateNoteAsyncTask(NoteCard noteCard){
            this.noteCard=noteCard;
        }
        @Override
        protected Void doInBackground(Void... params) {
            noteCardDao.update(noteCard);
            return null;
        }
    }

    class SaveNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        NoteCard noteCard;
        public SaveNoteAsyncTask(NoteCard noteCard) {
            this.noteCard = noteCard;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (noteCard == null) return null;
            DayCard dayCard = null;
            if(noteCard.getDay_id()==-1){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(noteCard.getDate());
                int year = calendar.get(Calendar.YEAR);
                int mouth = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                dayCard = dayCardDao.findByParams(year, mouth, day);
                if (dayCard == null) {
                    dayCard = new DayCard(year, mouth, day);
                    long t = dayCardDao.insert(dayCard);
                    dayCard.setDay_id(t);
                }
                noteCard.setDay_id(dayCard.getDay_id());
            }
            noteCardDao.insert(noteCard);
            if (!TextUtils.isEmpty(noteCard.getImgPath())) {
                if(dayCard==null){
                    dayCard=dayCardDao.findById(noteCard.getDay_id());
                }
                dayCard.setDayImagePath(noteCard.getImgPath());
                dayCardDao.update(dayCard);
            }
            return null;
        }
    }

}


