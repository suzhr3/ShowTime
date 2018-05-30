package edu.sysu.showtime.livelist;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.RoomInfo;
import edu.sysu.showtime.utils.ImgUtils;
import edu.sysu.showtime.watcher.WatcherLiveActivity;

public class LiveListAdapter extends BaseAdapter {
    private Context context;
    private List<RoomInfo> roomList;
    private int[] colorSet = {0x77dd88, 0xd850a2, 0x598fe5};

    public LiveListAdapter(Context context, List<RoomInfo> roomList){
        this.context = context;
        this.roomList = roomList;
    }

    //删除旧的Item数据
    public void removeOldRoomList() {
        roomList.clear();
    }
    //获取新的Item数据
    public void addNewRoomList(List<RoomInfo> newRoomList) {
        if (newRoomList != null) {
            //roomList.clear();
            roomList.addAll(0, newRoomList);
            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        return roomList.size();
    }
    @Override
    public Object getItem(int pos) {
        return roomList.get(pos);
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
                    .inflate(R.layout.item_live_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.bindData(roomList.get(pos), pos);
        return convertView;
    }


    private class ViewHolder {  //管理Item中的所有控件
        View itemView;
        TextView liveTitle;
        TextView roomId;
        ImageView hostHeadPic;
        ImageView liveCover;
        TextView hostName;
        TextView watcherNums;

        public ViewHolder(View view) {
            itemView = view;
            liveTitle = view.findViewById(R.id.live_title);
            roomId = view.findViewById(R.id.room_id);
            hostHeadPic = view.findViewById(R.id.host_headPic);
            liveCover = view.findViewById(R.id.live_cover);
            hostName = view.findViewById(R.id.host_name);
            watcherNums = view.findViewById(R.id.watch_nums);
        }
        public void bindData(final RoomInfo roomInfo, int pos) {
            String room_id = "房间ID："+roomInfo.roomId;
            roomId.setText(room_id);

            //设置主播名称
            String userName = roomInfo.userName;
            if (TextUtils.isEmpty(userName)) {
                userName = roomInfo.userId;
            }
            hostName.setText(userName);

            //设置直播标题
            String liveTitleStr = roomInfo.liveTitle;
            if (TextUtils.isEmpty(liveTitleStr)) {
                this.liveTitle.setText(userName + "的直播");
            } else {
                this.liveTitle.setText(liveTitleStr);
            }
            int colorIndex = pos % 3;
            //liveTitle.setBackground(colorSet[colorIndex]);

            //设置直播封面
            String coverUrl = roomInfo.liveCoverPic;
            if (TextUtils.isEmpty(coverUrl)) {
                ImgUtils.load(R.drawable.default_cover, liveCover);
            } else {
                ImgUtils.load(coverUrl, liveCover);
            }

            //设置主播头像
            String headPicUrl = roomInfo.userHeadPic;
            if (TextUtils.isEmpty(headPicUrl)) {
                ImgUtils.loadRound(R.drawable.default_head_pic, hostHeadPic);
            } else {
                ImgUtils.loadRound(headPicUrl, hostHeadPic);
            }

            //设置观看人数
            int nums = roomInfo.watcherNums;
            String watchText = nums + "人\r\n正在看";
            watcherNums.setText(watchText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(context, WatcherLiveActivity.class);
                    intent.putExtra("roomId", roomInfo.roomId);
                    intent.putExtra("hostId", roomInfo.userId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
