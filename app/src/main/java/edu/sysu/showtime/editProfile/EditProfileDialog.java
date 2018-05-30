package edu.sysu.showtime.editProfile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.sysu.showtime.R;
import edu.sysu.showtime.widget.TransParentDialog;

public class EditProfileDialog extends TransParentDialog {
    private String titleStr;
    private TextView title_tv;
    private EditText content;
    private Button ok_bt;

    //因为这个对话框是自定义的，因此确定按钮的回调监听器也要自己实现
    public interface OnOKListener {
        void onOk(String title, String content);
    }
    private OnOKListener onOKListener;
    public void setOnOKListener(OnOKListener listener) {
        onOKListener = listener;
    }

    //自定义对话框的构造函数
    public EditProfileDialog(Activity activity){
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_profile, null, false);
        title_tv = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
        ok_bt = view.findViewById(R.id.ok);
        //设置确定按钮监听器
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentStr = content.getText().toString();
                //将信息回调给主调用方EditProfileActivity
                if (onOKListener != null) {
                    onOKListener.onOk(titleStr, contentStr);
                }
                hideDialog();
            }
        });

        setContentViewToCustomDialog(view);   //将这个布局设置到自定义的对话框界面上
        //设置对话框的高度是整个窗体的80%宽，高度是wrap_content
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    //弹出编辑信息对话框
    public void showEditDialog(String title, int resId, String defaultContent) {
        titleStr = title;
        title_tv.setText("请输入" + title);
        content.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        content.setText(defaultContent);
        super.showDialog();     //显示父类中自定义的透明对话框
    }


}
