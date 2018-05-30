package edu.sysu.showtime.editProfile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import edu.sysu.showtime.R;
import edu.sysu.showtime.widget.TransParentDialog;

public class EditGenderDialog extends TransParentDialog {
    private RadioButton male;
    private RadioButton female;
    private Button ok_bt;
    public interface OnOKListener {
        void onOk(boolean isMale);
    }
    private OnOKListener onChangeGenderListener;
    public void setOnOKListener(OnOKListener listener) {
        onChangeGenderListener = listener;
    }

    public EditGenderDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_gender, null, false);
        male = view.findViewById(R.id.male);
        female = view.findViewById(R.id.female);
        ok_bt = view.findViewById(R.id.ok);
        //设置确定按钮监听器
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMaleChecked = male.isChecked();
                if (onChangeGenderListener != null) {
                    onChangeGenderListener.onOk(isMaleChecked);
                }
                hideDialog();
            }
        });
        super.setContentViewToCustomDialog(view);
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    //弹出修改性别
    public void showChangeGenderDialog(boolean isMale) {
        male.setChecked(isMale);
        female.setChecked(!isMale);
        super.showDialog();
    }
}
