package edu.sysu.showtime.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sysu.showtime.R;

//继承于自定义的透明对话框
public class PicChooseDialog extends TransParentDialog {
    private TextView camera;
    private TextView picLib;
    private ImageView cancel;

    //打开相册回调接口
    public interface OnDialogClickListener {
        void onCamera();
        void onAlbum();
    }
    private OnDialogClickListener onDialogClickListener;

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        onDialogClickListener = listener;
    }
    //自定义打开相册对话框的构造函数，用于初始化对话框的一些控件的注册以及监听器的设置等
    public PicChooseDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_pic_choose, null, false);
        super.setContentViewToCustomDialog(view);
        setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        camera = view.findViewById(R.id.pic_camera);
        picLib = view.findViewById(R.id.pic_album);
        cancel = view.findViewById(R.id.pic_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
                if (onDialogClickListener != null) {
                    onDialogClickListener.onCamera();
                }
            }
        });
        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
                if (onDialogClickListener != null) {
                    onDialogClickListener.onAlbum();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });
    }

    public void showDialogOnBottom() {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        super.showDialog();
    }
}
