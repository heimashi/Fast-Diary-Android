package cn.swang.dao;

import android.net.Uri;

import java.util.List;

import cn.swang.entity.DayCard;

/**
 * Created by sw on 2015/9/11.
 */
public interface DayCardDao {
    String TABLE_NAME = "daycards";
    String COLUMN_NAME_DAY_ID = "day_id";
    String COLUMN_NAME_YEAR= "year";
    String COLUMN_NAME_MOUTH = "mouth";
    String COLUMN_NAME_DAY = "day";
    String COLUMN_NAME_DAYIMAGEPATH="dayImagePath";
    Uri DAY_CARD_URI = Uri.parse("content://cn.swang.dao.DayCardDao/daycard");

    long insert(DayCard dayCard);
    void update(DayCard dayCard);
    void delete(long dayid);
    List<DayCard> fecthAll();
    DayCard findByParams(int year,int mouth, int day);
    DayCard findById(long dayId);
}
