package cn.swang.entity;

import java.io.Serializable;

/**
 * Created by sw on 2015/9/9.
 */
public class BackMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    // 状态 true成功 。false失败
    private boolean state;
    // 内容
    private Object message;

    public BackMessage(boolean state, Object message) {
        super();
        this.state = state;
        this.message = message;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

}
