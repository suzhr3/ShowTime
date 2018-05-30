package edu.sysu.showtime.title;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.TIMUserProfile;

import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.utils.ImgUtils;

public class TitleView extends LinearLayout {
    private ImageView hostHeadPicImg;   //主播头像
    private TextView watchersNumView;   //观看人数
    private RecyclerView watcherListView; //观众列表
    public TitleViewAdapter adapter;

    private int watcherNum = 0;
    private String hostId;  //主播id

    public TitleView(Context context) {
        super(context);
        init();
    }
    public TitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public TitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_title, this, true);
        initViews();
    }

    private void initViews() {
        hostHeadPicImg = findViewById(R.id.host_headPic);
        watchersNumView = findViewById(R.id.watchers_num);
        hostHeadPicImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击了主播头像，显示主播个人信息对话框
                showUserInfoDialog(hostId);
            }
        });

        watcherListView = findViewById(R.id.watcher_list);
        initRecyclerView();
    }

    private void initRecyclerView() {
        //设置RecyclerView在计算时以固定的大小去计算，提高效率
        watcherListView.setHasFixedSize(true);
        //设置RecyclerView的布局管理器方向为横向
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        watcherListView.setLayoutManager(layoutManager);

        adapter = new TitleViewAdapter(getContext());
        watcherListView.setAdapter(adapter);
    }

    public void setHostHeadPicImg(TIMUserProfile userProfile) {
        if(userProfile == null){
            ImgUtils.loadRound(R.drawable.default_head_pic, hostHeadPicImg);
        }else {
            hostId = userProfile.getIdentifier();
            String faceUrl = userProfile.getFaceUrl();
            if (TextUtils.isEmpty(faceUrl)) {
                ImgUtils.loadRound(R.drawable.default_head_pic, hostHeadPicImg);
            } else {
                ImgUtils.loadRound(faceUrl, hostHeadPicImg);
            }
        }
    }

    //加一个观众
    public void addWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            adapter.addWatcher(userProfile);
            watcherNum++;
            watchersNumView.setText(watcherNum + "人正在看");
        }
    }

    //加一群观众
    public void addWatchers(List<TIMUserProfile> userProfileList){
        if(userProfileList != null){
            adapter.addWatchers(userProfileList);
            watcherNum+= userProfileList.size();
            watchersNumView.setText(watcherNum + "人正在看");
        }
    }

    //移除一个观众
    public void removeWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            adapter.removeWatcher(userProfile);
            watcherNum--;
            watchersNumView.setText(watcherNum + "人正在看");
        }
    }

    private void showUserInfoDialog(String userId) {
        adapter.showUserInfoDialog(userId);
    }
}
