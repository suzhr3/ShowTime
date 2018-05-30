package edu.sysu.showtime.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import edu.sysu.showtime.R;

//自定义的透明背景模糊对话框组件
public class TransParentNoDimDialog {
    protected Activity activity;
    public Dialog dialog;

    public TransParentNoDimDialog(Activity activity){
        this.activity = activity;
        dialog = new Dialog(activity, R.style.dialog_nodim);   //创建一个透明的对话框
    }

    public void setContentViewToCustomDialog(View view) {
        dialog.setContentView(view);
    }

    public void setWidthAndHeight(int width, int height) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes(); //先获取到对话框的窗口管理属性
        if (params != null) {
            params.width = width;       //设置宽度
            params.height = height;     //设置高度
            window.setAttributes(params);           //再将窗口管理属性设置回去给对话框
        }
    }

    public void showDialog() {
        dialog.show();
    }
    public void hideDialog() {
        dialog.hide();
    }
    public void dismiss(){
        dialog.dismiss();
    }

}
