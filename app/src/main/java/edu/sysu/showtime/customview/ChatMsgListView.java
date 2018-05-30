package edu.sysu.showtime.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.sysu.showtime.adapter.ChatMsgAdapter;
import edu.sysu.showtime.bean.MsgInfo;

//自定义的消息列表控件，已经在内部完成了适配器等的绑定，以后直接拿来使用
public class ChatMsgListView extends ListView {
    private List<MsgInfo> msgList;
    private ChatMsgAdapter adapter;

    public ChatMsgListView(Context context) {
        super(context);
        init();
    }
    public ChatMsgListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ChatMsgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void addMsgInfo(MsgInfo info) {
        if (info != null) {
            adapter.addMsgInfo(info);
            setSelection(msgList.size());   //让该ListView自动滚动到最底部，从而显示最新的数据
        }
    }

    private void init() {
        msgList = new ArrayList<MsgInfo>();
        adapter = new ChatMsgAdapter(getContext(), msgList);
        this.setAdapter(adapter);

        setDividerHeight(0);    //不显示item间的分割线
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
