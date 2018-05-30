package edu.sysu.showtime.host;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import edu.sysu.showtime.R;
import edu.sysu.showtime.widget.TransParentNoDimDialog;

public class HostControlDialog extends TransParentNoDimDialog {
    private TextView beautyView;
    private TextView flashView;
    private TextView voiceView;
    private TextView cameraView;

    private int dialogWidth;
    private int dialogHeight;

    public HostControlDialog(Activity activity) {
        super(activity);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_host_control, null, false);
        super.setContentViewToCustomDialog(view);

        beautyView = view.findViewById(R.id.beauty);
        flashView = view.findViewById(R.id.flash_light);
        voiceView = view.findViewById(R.id.voice);
        cameraView = view.findViewById(R.id.camera);

        //设置点击监听器
        beautyView.setOnClickListener(clickListener);
        flashView.setOnClickListener(clickListener);
        voiceView.setOnClickListener(clickListener);
        cameraView.setOnClickListener(clickListener);

        //测量空间的大小，使用UNSPECIFIED字段，表明不确定控件大小，控件是多大就多大
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        dialogWidth = view.getMeasuredWidth();
        dialogHeight = view.getMeasuredHeight();
        setWidthAndHeight(dialogWidth, dialogHeight);
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(listener != null) {
                if (v.getId() == R.id.beauty) {
                    listener.onBeautyClick();
                }else if(v.getId() == R.id.flash_light){
                    listener.onFlashClick();
                }else if(v.getId() == R.id.voice){
                    listener.onVoiceClick();
                }else if(v.getId() == R.id.camera){
                    listener.onCameraClick();
                }
            }
            hideDialog();   //点击了某个选项后隐藏掉操作条对话框
        }
    };

    public interface OnControlClickListener{
        public void onBeautyClick();
        public void onFlashClick();
        public void onVoiceClick();
        public void onCameraClick();
    }
    private OnControlClickListener listener;
    public void setOnControlClickListener(OnControlClickListener l){
        listener = l;
    }

    //供外界调用，修改3个图标的显示内容
    public void updateViewIcon(boolean beautyOn, boolean flashOn, boolean voiceOn) {
        if (beautyOn) {
            beautyView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_beauty_on, 0, 0, 0);
            beautyView.setText("关美颜");
        } else {
            beautyView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_beauty_off, 0, 0, 0);
            beautyView.setText("开美颜");
        }

        if (flashOn) {
            flashView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_flashlight_on, 0, 0, 0);
            flashView.setText("关闪光");
        } else {
            flashView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_flashlight_off, 0, 0, 0);
            flashView.setText("开闪光");
        }

        if (voiceOn) {
            voiceView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mic_on, 0, 0, 0);
            voiceView.setText("关声音");
        } else {
            voiceView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mic_off, 0, 0, 0);
            voiceView.setText("开声音");
        }
    }

    // 设置操作类在屏幕底部上显示，这里的view是bottle_control_view中的向上箭头，
    // 首先获取这个箭头的位置，然后用于设置弹出的主播选项对话框的位置在其之上
    public void showDialogOnBottom(View view) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);  //默认window布局就是位于左上角开始，可省略
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = viewLocation[0] - (dialogWidth - view.getWidth()) / 2;
        params.y = viewLocation[1] - dialogHeight - view.getHeight();
        params.alpha = 0.7f;
        dialog.getWindow().setAttributes(params);   //设置对话框显示的坐标位置

        showDialog();
    }
}
