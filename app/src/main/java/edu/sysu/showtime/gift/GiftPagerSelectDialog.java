package edu.sysu.showtime.gift;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.GiftCmdInfo;
import edu.sysu.showtime.bean.GiftInfo;
import edu.sysu.showtime.model.Constants;
import edu.sysu.showtime.widget.TransParentNoDimDialog;

public class GiftPagerSelectDialog extends TransParentNoDimDialog {
    private ViewPager giftPager;
    private GiftPagerAdapter adapter;
    private ImageView indicator1, indicator2;
    private Button sendGiftBtn;
    private ImageView closeDialogImg;

    public View dialogView;     //为了之后方便动态获取该对话框对应的布局，故保存其布局对应的View

    public GiftPagerSelectDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_gift_select, null, false);
        dialogView = view;
        super.setContentViewToCustomDialog(view);
        super.setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initViews(view);
        init();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                giftDialogListener.onShowBottomControlView();
            }
        });
    }

    //用于在adapter中去触发回调事件
    public interface OnListener {
        void onHideSendBtn();               //隐藏send按钮的回调
        void onShowSendBtn();               //显示send按钮的回调
        void onSetSendBtnText();            //设置send按钮的回调
        void onShowBottomControlView();     //显示底部控制栏的回调
        void onSendBtnClick(ILVCustomCmd customCmd);   //点击了发送按钮的回调
    }
    private OnListener giftDialogListener;

    public void setOnListener(OnListener l) {
        giftDialogListener = l;
        adapter.setListener(giftDialogListener);
    }

    private void init() {
        adapter = new GiftPagerAdapter(activity);
        giftPager.setAdapter(adapter);

        giftPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    indicator1.setImageResource(R.drawable.ind_s);
                    indicator2.setImageResource(R.drawable.ind_uns);
                } else if (position == 1) {
                    indicator1.setImageResource(R.drawable.ind_uns);
                    indicator2.setImageResource(R.drawable.ind_s);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        closeDialogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(giftDialogListener != null){
                    hideDialog();
                    giftDialogListener.onShowBottomControlView();
                }
            }
        });
        sendGiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送礼物消息
                if (TextUtils.isEmpty(adapter.repeatId) || "重新选择了礼物".equals(adapter.repeatId)) {
                    adapter.repeatId = "礼物连续发送开始计时";
                }
                if (giftDialogListener != null) {
                    ILVCustomCmd giftCmd = new ILVCustomCmd();
                    giftCmd.setType(ILVText.ILVTextType.eGroupMsg);
                    giftCmd.setCmd(Constants.CHAT_GIFT);
                    giftCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());

                    GiftCmdInfo giftCmdInfo = new GiftCmdInfo();
                    giftCmdInfo.giftId = adapter.selectGiftInfo.giftId;
                    giftCmdInfo.repeatId = adapter.repeatId;
                    giftCmd.setParam(new Gson().toJson(giftCmdInfo));
                    giftDialogListener.onSendBtnClick(giftCmd);
                    if(adapter.selectGiftInfo.type == GiftInfo.Type.ContinueGift) {
                        startTimer();      //开启连续发送礼物计时器
                    }
                }
            }
        });
    }

    private void startTimer() {
        stopTimer();
        timerHandler.sendEmptyMessage(WHAT_START_TIMER);
    }

    private void stopTimer() {
        timerHandler.removeMessages(WHAT_START_TIMER);
        timerHandler.removeMessages(WHAT_MINUS_TIMER);
        sendGiftBtn.setText("发送");
        adapter.leftTimes = 5;
        adapter.repeatId = "";  //停止计数器，归零计数器
    }
    private static final int WHAT_START_TIMER = 0;
    private static final int WHAT_MINUS_TIMER = 1;

    private Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if("重新选择了礼物".equals(adapter.repeatId)){
                stopTimer();
                return;
            }
            int what = msg.what;
            if (WHAT_START_TIMER == what) {
                sendGiftBtn.setText("发送(" + adapter.leftTimes + "s)");
                timerHandler.sendEmptyMessageDelayed(WHAT_MINUS_TIMER, 1000);
            } else if (WHAT_MINUS_TIMER == what) {
                adapter.leftTimes--;
                if (adapter.leftTimes > 0) {
                    sendGiftBtn.setText("发送(" + adapter.leftTimes + "s)");
                    timerHandler.sendEmptyMessageDelayed(WHAT_MINUS_TIMER, 1000);
                } else {
                    //连续发送礼物周期结束，归零计数器
                    sendGiftBtn.setText("发送");
                    adapter.repeatId = "";
                    adapter.leftTimes = 5;
                }
            }
        }
    };

    private void initViews(View view) {
        giftPager = view.findViewById(R.id.gift_pager);
        indicator1 = view.findViewById(R.id.indicator1);
        indicator2 = view.findViewById(R.id.indicator2);
        sendGiftBtn = view.findViewById(R.id.send_gift);
        closeDialogImg = view.findViewById(R.id.close_dialog);
        //默认情况下的显示
        indicator1.setImageResource(R.drawable.ind_s);
        indicator2.setImageResource(R.drawable.ind_uns);
        sendGiftBtn.setVisibility(View.INVISIBLE);
    }

    //跟PicChooseDialog一样，让这个自定义的对话框显示在屏幕底部
    public void showDialogOnBottom() {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        super.showDialog();
    }
}
