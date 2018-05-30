package edu.sysu.showtime.title;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sysu.showtime.R;
import edu.sysu.showtime.utils.ImgUtils;

public class TitleViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<TIMUserProfile> watcherList = new ArrayList<TIMUserProfile>();
    //用map确保只会记录同一个用户进入房间一次
    private Map<String , TIMUserProfile> watcherMap = new HashMap<String , TIMUserProfile>();

    public UserInfoDialog userInfoDialog = null;

    public TitleViewAdapter(Context context){
        this.context = context;
    }

    //初始化ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.adapter_title_watcher_item, parent, false);
        WatcherHolder holder = new WatcherHolder(itemView);
        return holder;
    }

    //将ViewHolder和数据源绑定在一起
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TIMUserProfile userProfile = watcherList.get(position);
        ((WatcherHolder)holder).bindData(userProfile);
    }

    @Override
    public int getItemCount() {
        return watcherList.size();
    }

    public void addWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            boolean inWatcherList = watcherMap.containsKey(userProfile.getIdentifier());
            if(!inWatcherList) {
                watcherList.add(userProfile);
                watcherMap.put(userProfile.getIdentifier(), userProfile);
                notifyDataSetChanged();
            }
        }
    }

    public void addWatchers(List<TIMUserProfile> userProfileList) {
        if (userProfileList != null) {
            for (TIMUserProfile userProfile : userProfileList) {
                if (userProfile != null) {
                    boolean inWatcherList = watcherMap.containsKey(userProfile.getIdentifier());
                    if (!inWatcherList) {
                        watcherList.add(userProfile);
                        watcherMap.put(userProfile.getIdentifier(), userProfile);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void removeWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            TIMUserProfile targetUser = watcherMap.get(userProfile.getIdentifier());
            if (targetUser != null) {
                watcherList.remove(targetUser);
                watcherMap.remove(targetUser.getIdentifier());
                notifyDataSetChanged();
            }
        }
    }



    private class WatcherHolder extends RecyclerView.ViewHolder{
        private ImageView user_headPicImg;

        //初始化布局中的控件
        public WatcherHolder(View itemView) {
            super(itemView);
            user_headPicImg = itemView.findViewById(R.id.user_headPic);
        }

        public void bindData(final TIMUserProfile userProfile) {
            String faceUrl = userProfile.getFaceUrl();
            if (TextUtils.isEmpty(faceUrl)) {
                ImgUtils.loadRound(R.drawable.default_head_pic, user_headPicImg);
            } else {
                ImgUtils.loadRound(faceUrl, user_headPicImg);
            }
            user_headPicImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击用户头像时，显示用户详情对话框
                    showUserInfoDialog(userProfile.getIdentifier());
                }
            });
        }
    }

    public void showUserInfoDialog(String userId) {
        List<String> ids = new ArrayList<String>();
        ids.add(userId);
        TIMFriendshipManager.getInstance().getUsersProfile(ids, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "请求用户信息失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                if(context instanceof Activity) {
                    TIMUserProfile userProfile = timUserProfiles.get(0);
                    userInfoDialog = new UserInfoDialog((Activity) context, userProfile);
                    userInfoDialog.showDialog();
                }
            }
        });
    }
}
