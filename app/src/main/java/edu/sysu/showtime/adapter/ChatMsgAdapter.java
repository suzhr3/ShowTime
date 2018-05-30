package edu.sysu.showtime.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.MsgInfo;
import edu.sysu.showtime.utils.ImgUtils;

//聊天列表ChatMsgListView的适配器
public class ChatMsgAdapter extends BaseAdapter {
    private Context context;
    private List<MsgInfo> msgList = new ArrayList<MsgInfo>();

    public ChatMsgAdapter(Context context, List<MsgInfo> msgList){
        this.context = context;
        this.msgList = msgList;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }
    @Override
    public Object getItem(int pos) {
        return msgList.get(pos);
    }
    @Override
    public long getItemId(int pos) {
        return pos;
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_msg_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.bindData(msgList.get(pos));
        return convertView;
    }

    public void addMsgInfo(MsgInfo info) {
        msgList.add(info);
        //在增加新的数据后，通知适配器去更新它其中的数据，从而会调用getView重新绘制数据，
        // 在getView中就会调用ViewHolder的bindData方法来重新绑定数据
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private ImageView faceImg;
        private TextView content;

        public ViewHolder(View itemView) {
            faceImg = itemView.findViewById(R.id.face_imageview);
            content = itemView.findViewById(R.id.chat_content);
        }

        public void bindData(MsgInfo info) {
            //1. 设置发送消息者头像
            String faceUrl = info.senderFaceUrl;
            if (TextUtils.isEmpty(faceUrl)) {
                ImgUtils.loadRound(R.drawable.default_head_pic, faceImg);
            } else {
                ImgUtils.loadRound(faceUrl, faceImg);
            }

            //2. 设置发送消息者发送的消息的颜色
            SpannableStringBuilder finalMsg = new SpannableStringBuilder("");

            {   //给用户昵称设置颜色
                String nameStr = info.senderName + ":";
                int start1 = 0;
                int end1 = nameStr.length();
                SpannableStringBuilder name = new SpannableStringBuilder(nameStr);
                name.setSpan(new ForegroundColorSpan(Color.parseColor("#23BE9F")),
                        start1, end1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                finalMsg.append(name);
            }

            {   //给发送的内容设置颜色
                String contentStr = info.chatContent;
                int start2 = 0;
                int end2 = contentStr.length();
                SpannableStringBuilder content = new SpannableStringBuilder(contentStr);
                content.setSpan(new ForegroundColorSpan(Color.WHITE),
                        start2, end2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                finalMsg.append(content);
            }
            content.setText(finalMsg);
        }
    }
}
