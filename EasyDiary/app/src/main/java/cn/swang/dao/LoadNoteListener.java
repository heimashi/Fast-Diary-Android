package cn.swang.dao;

import java.util.List;

import cn.swang.entity.NoteCard;

public interface LoadNoteListener {
    void onLoadNoteSuccess(List<NoteCard> list);
    void onLoadNoteFailed();
}
