package cn.swang.dao;

import android.net.Uri;

import java.util.List;

import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;

/**
 * Created by sw on 2015/9/11.
 */
public interface NoteCardDao {
    String TABLE_NAME = "notecards";
    String COLUMN_NAME_ID = "id";
    String COLUMN_NAME_CONTENT = "content";
    String COLUMN_NAME_DATE = "date";
    String COLUMN_NAME_IMGPATH = "imgPath";
    String COLUMN_NAME_VOICEPATH = "voicePath";
    String COLUMN_NAME_DAY_ID = "day_id";
    String COLUMN_NAME_VOICELENGTH = "voiceLength";
    Uri NOTE_CARD_URI = Uri.parse("content://cn.swang.dao.NoteCardDao/notecard");
    String NOTE_CARD_URI_STRING = "content://cn.swang.dao.NoteCardDao/notecard/";

    void insert(NoteCard noteCard);

    void update(NoteCard noteCard);

    void delete(NoteCard noteCard);

    List<NoteCard> fecthAll();

    List<NoteCard> findAllByDayid(DayCard dayCard);
}
