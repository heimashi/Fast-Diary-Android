package cn.swang.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by sw on 2015/9/29.
 */
public class MyDialog extends Dialog {

    private Window window = null;

    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setAnimation(int id){
        window = getWindow(); //得到对话框
        window.setWindowAnimations(id); //设置窗口弹出动画
    }

    public interface DialogDismissCallBack{
        void handleDialogDismiss();
    }

    private DialogDismissCallBack mCallBack=null;

    public void setDialogDismissCallBack(DialogDismissCallBack callBack){
        mCallBack=callBack;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mCallBack!=null){
            mCallBack.handleDialogDismiss();
        }
    }
}
