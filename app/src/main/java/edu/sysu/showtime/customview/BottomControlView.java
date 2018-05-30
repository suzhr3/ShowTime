package edu.sysu.showtime.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import edu.sysu.showtime.R;

//自定义直播时底部聊天控制条控件，在HostLiveActivity的布局中使用
public class BottomControlView extends RelativeLayout {
    private ImageView chat;
    private ImageView close;
    private ImageView gift;
    private ImageView option;

    public BottomControlView(Context context) {
        super(context);
        init();
    }
    public BottomControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public BottomControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //将单个view和该自定义RelativeLayout类绑定在一起
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_control, this, true);
        initViews();
        setListener();
    }
    private void initViews() {
        chat = findViewById(R.id.chat);
        close = findViewById(R.id.close);
        gift = findViewById(R.id.gift);
        option = findViewById(R.id.option);
    }
    private void setListener() {
        chat.setOnClickListener(clickListener);
        close.setOnClickListener(clickListener);
        gift.setOnClickListener(clickListener);
        option.setOnClickListener(clickListener);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.chat) {
                // 回调接口，显示聊天操作栏
                if (controlListener != null) {
                    controlListener.onChatClick();
                }
            } else if (view.getId() == R.id.close) {
                // 回调接口，关闭直播
                if (controlListener != null) {
                    controlListener.onCloseClick();
                }
            } else if (view.getId() == R.id.gift) {
                // 回调接口，显示礼物选择九宫格
                if (controlListener != null) {
                    controlListener.onGiftClick();
                }
            } else if (view.getId() == R.id.option) {
                if (controlListener != null) {
                    controlListener.onOptionClick(view);
                    option.setImageResource(R.drawable.icon_op_close);
                }
            }
        }
    };

    //事件监听回调接口
    public interface OnControlListener {
        public void onChatClick();
        public void onCloseClick();
        public void onGiftClick();
        public void onOptionClick(View view);
    }
    private OnControlListener controlListener = null;

    public void setOnControlListener(OnControlListener l) {
        controlListener = l;
    }
}
