package edu.sysu.showtime.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

import edu.sysu.showtime.R;
import edu.sysu.showtime.model.Constants;

public class ChatView extends LinearLayout {
    private CheckBox chatMode;
    private EditText chatContentEdt;
    private TextView send;

    public ChatView(Context context) {
        super(context);
        init();
    }
    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat, this, true);
        initViews();
        setListener();
    }

    private void initViews() {
        chatMode = findViewById(R.id.switch_chat_type);
        chatContentEdt = findViewById(R.id.chat_content_edit);
        send = findViewById(R.id.chat_send);
    }

    private void setListener() {
        chatMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chatContentEdt.setHint("发送弹幕聊天消息");
                } else {
                    chatContentEdt.setHint("和大家聊点什么吧");
                }
            }
        });
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = chatContentEdt.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    //发送聊天消息，返回回调接口
                    if(sendClickListener != null){
                        ILVCustomCmd customCmd = new ILVCustomCmd();
                        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                        customCmd.setParam(content);
                        boolean isDanmu = chatMode.isChecked();
                        if (isDanmu) {
                            customCmd.setCmd(Constants.CHAT_MSG_DANMU);
                        } else {
                            customCmd.setCmd(Constants.CHAT_MSG_LIST);
                        }
                        sendClickListener.onChatSend(customCmd);//调用回调接口，返回要发送的信息customCmd给调用方
                    }
                }
            }
        });
    }
    public interface OnSendClickListener {
        public void onChatSend(ILVCustomCmd customCmd);
    }
    private OnSendClickListener sendClickListener;

    public void setOnChatSendListener(OnSendClickListener l) {
        sendClickListener = l;
    }
}
