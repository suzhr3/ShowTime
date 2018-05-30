package edu.sysu.showtime;

import android.app.Application;
import android.content.Context;

import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.sysu.showtime.editProfile.CustomProfileField;
import edu.sysu.showtime.request.HeartBeatRequest;
import edu.sysu.showtime.utils.QiNiuUpLoadUtils;

public class APP extends Application {
    private static APP app;     //单例模式
    private TIMUserProfile userProfile;
    private ILVLiveConfig liveConfig;
    public static List<String> customInfos;
    private Timer heartBeatTimer = null;
    private TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initiLiveSDK();
        initQiNiuSDK();
    }

    public static APP getApp(){
        return app;
    }
    public static Context getContext(){
        return app.getApplicationContext();
    }
    private void initiLiveSDK() {
        //iLive腾讯直播SDK初始化
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400084345, 25236);
        //初始化直播场景
        liveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(liveConfig);

        //初始化腾讯个人信息字段，其中包括自定义的字段以及腾讯的基础字段
        customInfos = new ArrayList<String>();
        customInfos.add(CustomProfileField.CUSTOM_GET);
        customInfos.add(CustomProfileField.CUSTOM_SEND);
        customInfos.add(CustomProfileField.CUSTOM_LEVEL);
        customInfos.add(CustomProfileField.CUSTOM_RENZHENG);
        //TIMManager.getInstance().initFriendshipSettings(CustomProfileField.allBaseInfo, customInfos);
    }
    private void initQiNiuSDK() {
        QiNiuUpLoadUtils.init("BCBOg1P9PnzrYTC8Gou_fWSWWZiNmuSEk9usE0Om",
                "Jm3063retBBf4xCRCDBm1_btviYTApxFYQteDlu4",
                "http://p7qx018gt.bkt.clouddn.com/",
                "showtimelive");
    }

    public void setSelfProfile(TIMUserProfile userProfile){
        this.userProfile = userProfile;
    }
    public TIMUserProfile getSelfProfile() {
        return userProfile;
    }
    public ILVLiveConfig getLiveConfig(){
        return liveConfig;
    }



    //每5s发送一次心跳包
    public void startHeartBeatTimer(final int roomId){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                HeartBeatRequest request = new HeartBeatRequest();
                String url = request.getRequestUrl(roomId, APP.getApp().getSelfProfile().getIdentifier());
                request.request(url);
            }
        };
        heartBeatTimer = new Timer();
        heartBeatTimer.scheduleAtFixedRate(timerTask, 0, 5000);
    }

    //取消定时器
    public void stopHeartBeatTimer(){
        if(heartBeatTimer != null){
            heartBeatTimer.cancel();
        }
    }

    //在程序异常终止时会经过下面的方法，从而去取消定时器
    @Override
    public void onTerminate() {
        stopHeartBeatTimer();
        super.onTerminate();
    }
}
