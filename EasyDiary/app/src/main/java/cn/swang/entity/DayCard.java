package cn.swang.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sw on 2015/9/10.
 */
public class DayCard implements Serializable {

    private static final long serialVersionUID = -1L;
    private int year;
    private int mouth;
    private int day;
    private String dayImagePath;
    private long day_id;
    private List<NoteCard> noteSet;

    public DayCard() {
    }

    public DayCard(int year, int mouth, int day) {
        this.year = year;
        this.day = day;
        this.mouth = mouth;
    }

    public String getDayImagePath(){
        return dayImagePath;
    }

    public void setDayImagePath(String dayImagePath){
        this.dayImagePath=dayImagePath;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMouth() {
        return mouth;
    }

    public void setMouth(int mouth) {
        this.mouth = mouth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getDay_id() {
        return day_id;
    }

    public void setDay_id(long day_id) {
        this.day_id = day_id;
    }

    public List<NoteCard> getNoteSet() {
        return noteSet;
    }

    public void setNoteSet(List<NoteCard> noteSet) {
        this.noteSet = noteSet;
    }
}
