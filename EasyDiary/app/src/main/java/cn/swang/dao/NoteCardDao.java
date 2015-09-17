package cn.swang.dao;

import java.util.List;

import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;

/**
 * Created by sw on 2015/9/11.
 */
public interface NoteCardDao {
    public static final String TABLE_NAME = "notecards";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_CONTENT= "content";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_IMGPATH = "imgPath";
    public static final String COLUMN_NAME_VOICEPATH = "voicePath";
    public static final String COLUMN_NAME_DAY_ID = "day_id";
    public static final String COLUMN_NAME_VOICELENGTH = "voiceLength";

    void insert(NoteCard noteCard);
    void update(NoteCard noteCard);
    void delete(NoteCard noteCard);
    List<NoteCard> fecthAll();
    List<NoteCard> findAllByDayid(DayCard dayCard);
}
