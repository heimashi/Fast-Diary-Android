package cn.swang.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sw on 2015/9/10.
 */
public class NoteCard implements Serializable{

    private static final long serialVersionUID = -831930284387787342L;

    private long id;
    private String content;
    private Date date;
    private String imgPath;
    private String voicePath;
    private int voiceLength;
    private long day_id;

    public int getVoiceLength(){return voiceLength;}

    public void setVoiceLength(int voiceLength){
        this.voiceLength=voiceLength;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public long getDay_id() {
        return day_id;
    }

    public void setDay_id(long day_id) {
        this.day_id = day_id;
    }
}
