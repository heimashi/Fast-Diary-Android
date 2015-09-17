package cn.swang.dao;

import java.util.List;

import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;

public interface LoadDiaryListener {
    void onLoadDiarySuccess(List<DayCard> list);
    void onLoadDiaryFailed();
}
