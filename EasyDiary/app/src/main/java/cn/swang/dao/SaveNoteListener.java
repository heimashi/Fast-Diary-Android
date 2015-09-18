package cn.swang.dao;

import java.util.List;

import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;

public interface SaveNoteListener{
    void onSaveSuccess(List<NoteCard> list);
    void onSaveFailed();
}
