package edu.sysu.showtime.watcher;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import edu.sysu.showtime.APP;
import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.GiftCmdInfo;
import edu.sysu.showtime.bean.GiftInfo;
import edu.sysu.showtime.bean.MsgInfo;
import edu.sysu.showtime.customview.BottomControlView;
import edu.sysu.showtime.customview.ChatMsgListView;
import edu.sysu.showtime.customview.ChatView;
import edu.sysu.showtime.customview.DanmuView;
import edu.sysu.showtime.customview.GiftDanmuRepeatView;
import edu.sysu.showtime.customview.GiftFullView;
import edu.sysu.showtime.customview.SizeChangeRelativeLayout;
import edu.sysu.showtime.gift.GiftPagerSelectDialog;
import edu.sysu.showtime.model.Constants;
import edu.sysu.showtime.request.GetWatchersRequest;
import edu.sysu.showtime.request.JoinRoomRequest;
import edu.sysu.showtime.request.QuitRoomRequest;
import edu.sysu.showtime.title.TitleView;
import edu.sysu.showtime.utils.HttpRequestUtils;
import tyrantgit.widget.HeartLayout;

public class WatcherLiveActivity extends AppCompatActivity {
    private SizeChangeRelativeLayout parent_layout;
    private AVRootView avRootView;
    private TitleView titleView;
    private BottomControlView bottom_ontrol_view;
    private ChatView chatView;
    private ChatMsgListView chatMsgListView;
    private DanmuView danmuView;
    private GiftDanmuRepeatView giftView;
    private GiftFullView giftFullView;

    private HeartLayout heartLayout;
    private Timer heartTimer = new Timer();
    private Random heartRandom = new Random();

    private int roomId;
    private String hostId;

    private GiftPagerSelectDialog giftPagerSelectDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watcher_live);

        initViews();
        initSDK();
        joinRoom();
        setListener();
    }
    private void initSDK() {
        //初始化腾讯SDK
        ILVLiveManager.getInstance().setAvVideoView(avRootView);
        ILVLiveConfig liveConfig = APP.getApp().getLiveConfig();
        //设置接收观众消息监听器
        liveConfig.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
                //接收到文本消息
            }
            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                //因为发送时用的是sendCustommMsg，所以这里只实现该接收自定义消息的回调方法即可
                String name = userProfile.getNickName();
                if (TextUtils.isEmpty(name)) {
                    name = userProfile.getIdentifier();
                }
                if(cmd.getCmd() == Constants.CHAT_MSG_LIST){
                    String content = cmd.getParam();
                    MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                    chatMsgListView.addMsgInfo(msgInfo);
                } else if (cmd.getCmd() == Constants.CHAT_MSG_DANMU) {
                    String content = cmd.getParam();
                    MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                    chatMsgListView.addMsgInfo(msgInfo);
                    danmuView.addDanmuMsgInfo(msgInfo);
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_LEAVE) {
                    // 接收到退出房间的信息后，要根据发送该消息的用户userProfile的id
                    // 来看是主播退出房间，还是其他观众退出了房间
                    if (hostId.equals(userProfile.getIdentifier())) {
                        //是主播退出房间
                        quitRoom();
                    } else {
                        //是其他观众退出房间，将其退出信息显示到titleView上
                        titleView.removeWatcher(userProfile);
                        String content = "离开直播间";
                        MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                        chatMsgListView.addMsgInfo(msgInfo);
                    }
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_ENTER) {
                    //某个观众加入了房间，将其退出信息显示到titleView上
                    titleView.addWatcher(userProfile);
                    //mVipEnterView.showVipEnter(userProfile);
                    String content = "进入直播间";
                    MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                    chatMsgListView.addMsgInfo(msgInfo);
                } else if(cmd.getCmd() == Constants.CHAT_GIFT) {
                    //如果收到发礼物消息，在屏幕上显示礼物动画
                    GiftCmdInfo giftCmdInfo = new Gson().fromJson(cmd.getParam(), GiftCmdInfo.class);
                    if(giftCmdInfo == null){
                        return;
                    }
                    String repeatId = giftCmdInfo.repeatId;
                    GiftInfo giftInfo = GiftInfo.getGiftById(giftCmdInfo.giftId);
                    if(giftInfo.giftId == GiftInfo.Gift_Heart.giftId){
                        //发送的是心形礼物，故在本地也显示动画
                        heartLayout.addHeart(getRandomColor());
                    }else if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                        giftView.addGiftDanmuMsgInfo(giftInfo, repeatId, userProfile);
                    } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                        //全屏礼物
                        giftFullView.showGift(giftInfo, userProfile);
                    }
                }
            }
            @Override
            public void onNewOtherMsg(TIMMessage message) {
                //接收到其他消息
            }
        });
    }
    private void initViews() {
        //整个父布局，为其设置屏幕显示尺寸变化监听事件
        parent_layout = findViewById(R.id.size_change_layout);
        avRootView = findViewById(R.id.avRootView);
        titleView = findViewById(R.id.title_view);
        bottom_ontrol_view = findViewById(R.id.bottom_control_view);
        ImageView option = bottom_ontrol_view.findViewById(R.id.option);
        option.setVisibility(View.INVISIBLE);
        chatView = findViewById(R.id.chat_view);
        chatMsgListView = findViewById(R.id.chat_msg_list_view);
        danmuView = findViewById(R.id.danmu_view);
        giftView = findViewById(R.id.gift_view);
        giftFullView = findViewById(R.id.gift_full_view);
        heartLayout = findViewById(R.id.heartLayout);

        //默认显示底部控制栏，不显示操作栏
        bottom_ontrol_view.setVisibility(View.VISIBLE);
        chatView.setVisibility(View.GONE);
    }

    private void setListener() {
        parent_layout.setOnSizeChangeListener(new SizeChangeRelativeLayout.OnSizeChangeListener() {
            @Override
            public void onHideKeyBoard() {  //键盘隐藏时，显示底部控制栏，不显示聊天操作栏
                bottom_ontrol_view.setVisibility(View.VISIBLE);
                chatView.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onShowKeyBoard() {  //键盘弹出时，显示聊天操作栏，不显示底部控制栏
                bottom_ontrol_view.setVisibility(View.INVISIBLE);
                chatView.setVisibility(View.VISIBLE);
            }
        });
        //设置底部自定义的控制栏的点击监听回调
        bottom_ontrol_view.setOnControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChatClick() {
                //切换为显示聊天操作栏，并自动弹出软键盘
                bottom_ontrol_view.setVisibility(View.INVISIBLE);
                chatView.setVisibility(View.VISIBLE);
                showKeyBoard();
            }
            @Override
            public void onCloseClick() {
                //关闭直播
                quitRoom();
            }
            @Override
            public void onGiftClick() {
                bottom_ontrol_view.setVisibility(View.INVISIBLE);
                //在底部显示礼物九宫格
                if (giftPagerSelectDialog == null) {
                    giftPagerSelectDialog = new GiftPagerSelectDialog(WatcherLiveActivity.this);
                    //给九宫格设置监听器，监听回调信息
                    giftPagerSelectDialog.setOnListener(new GiftPagerSelectDialog.OnListener() {
                        @Override
                        public void onHideSendBtn() {
                            Button sendBtn = giftPagerSelectDialog.dialogView.findViewById(R.id.send_gift);
                            sendBtn.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onShowSendBtn() {
                            Button sendBtn = giftPagerSelectDialog.dialogView.findViewById(R.id.send_gift);
                            sendBtn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onSetSendBtnText() {
                            Button sendBtn = giftPagerSelectDialog.dialogView.findViewById(R.id.send_gift);
                            sendBtn.setText("发送");
                        }

                        @Override
                        public void onShowBottomControlView() {
                            bottom_ontrol_view.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onSendBtnClick(ILVCustomCmd customCmd) {
                            sendGiftMsg(customCmd);
                        }
                    });
                }
                giftPagerSelectDialog.showDialogOnBottom();
            }
            @Override
            public void onOptionClick(View view) {
                //观众没有该功能，不需要处理
            }
        });
        chatView.setOnChatSendListener(new ChatView.OnSendClickListener() {
            @Override
            public void onChatSend(ILVCustomCmd customCmd) {
                //接收发送按钮监听回调接口返回的数据customCmd，并发送聊天消息
                sendMsg(customCmd);
            }
        });

        heartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点赞发送心形礼物
                ILVCustomCmd customCmd = new ILVCustomCmd();
                customCmd.setCmd(Constants.CHAT_GIFT);
                customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());

                GiftCmdInfo giftCmdInfo = new GiftCmdInfo();
                giftCmdInfo.giftId = GiftInfo.Gift_Heart.giftId;
                customCmd.setParam(new Gson().toJson(giftCmdInfo));
                sendGiftMsg(customCmd); //调用下面发送自定义礼物消息的方法
            }
        });
    }

    //发送自定义的礼物消息
    private void sendGiftMsg(final ILVCustomCmd customCmd) {
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
            @Override
            public void onSuccess(TIMMessage data) {
                if (customCmd.getCmd() == Constants.CHAT_GIFT) {
                    //界面显示礼物动画，从Json形式的数据中获取原始的GiftCmdInfo数据
                    GiftCmdInfo giftCmdInfo = new Gson().fromJson(customCmd.getParam(), GiftCmdInfo.class);
                    int giftId = giftCmdInfo.giftId;
                    String repeatId = giftCmdInfo.repeatId;
                    GiftInfo giftInfo = GiftInfo.getGiftById(giftId);   //获取到是哪一个礼物
                    if (giftInfo == null) {
                        return;
                    }
                    if(giftInfo.giftId == GiftInfo.Gift_Heart.giftId){
                        //发送的是心形礼物，故在本地也显示动画
                        heartLayout.addHeart(getRandomColor());
                    } else if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                        giftView.addGiftDanmuMsgInfo(giftInfo, repeatId, APP.getApp().getSelfProfile());
                    } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                        //全屏礼物
                        giftFullView.showGift(giftInfo, APP.getApp().getSelfProfile());
                    }
                }
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
            }

        });
    }

    //自动显示软键盘
    private void showKeyBoard() {
        final EditText editText = chatView.findViewById(R.id.chat_content_edit);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        //延迟200ms后再自动弹出软键盘，目的是确保界面加载完成之后再弹出软键盘
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            public void run(){
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        };
        timer.schedule(timerTask, 200);
    }

    //发送聊天信息
    private void sendMsg(final ILVCustomCmd customCmd) {
        //发送消息
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
            @Override
            public void onSuccess(TIMMessage data) {
                String chatContent = customCmd.getParam();
                String userId = APP.getApp().getSelfProfile().getIdentifier();
                String userName = APP.getApp().getSelfProfile().getNickName();
                String faceUrl = APP.getApp().getSelfProfile().getFaceUrl();
                if (TextUtils.isEmpty(userName)) {
                    userName = userId;
                }
                MsgInfo msgInfo = new MsgInfo(userId, userName, faceUrl, chatContent);
                chatMsgListView.addMsgInfo(msgInfo);
                if (customCmd.getCmd() == Constants.CHAT_MSG_DANMU) {
                    //如果是弹幕模式的话，除了将信息发送到chatMsgListView列表外，还需要显示成弹幕
                    danmuView.addDanmuMsgInfo(msgInfo);
                }
                EditText editText = chatView.findViewById(R.id.chat_content_edit);
                editText.setText("");   //发送完一条消息后自动清空输入框
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
            }

        });
    }

    private void startHeartAnim() {
        heartTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                heartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        heartLayout.addHeart(getRandomColor());     //添加一个随机颜色的心形
                    }
                });
            }
        }, 0, 1000); //1秒钟显示一个心形
    }

    private int getRandomColor() {
        int color = Color.rgb(heartRandom.nextInt(255), heartRandom.nextInt(255), heartRandom.nextInt(255));
        return color;
    }

    private void joinRoom() {
        roomId = getIntent().getIntExtra("roomId", -1);
        hostId = getIntent().getStringExtra("hostId");
        if(roomId < 0 || TextUtils.isEmpty(hostId)){
            Toast.makeText(this.getApplicationContext(), "房间号不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        //加入房间配置项
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(hostId)
                .autoCamera(false) //是否自动打开摄像头
                .controlRole("Guest") //角色设置
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
                .autoMic(false);//是否自动打开mic
        //加入房间
        ILVLiveManager.getInstance().joinRoom(roomId, memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //开始心形动画
                startHeartAnim();
                //发送进入直播的消息，主播监听到该消息执行对应操作
                sendEnterRoomMsg();
                //开始发送定时心跳包
                APP.getApp().startHeartBeatTimer(roomId);
                //调用后台接口更新房间信息
                JoinRoomRequest request = new JoinRoomRequest();
                String url = request.getRequestUrl(roomId, APP.getApp().getSelfProfile().getIdentifier());
                request.request(url);
                //显示主播的头像
                updateTitleView();
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(WatcherLiveActivity.this,"直播已结束",Toast.LENGTH_SHORT).show();
                logout();
            }
        });
    }
    private void sendEnterRoomMsg() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_ENTER);
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {

            }
            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void updateTitleView() {
        //通过hostId去腾讯IM服务器获取主播信息
        List<String> list = new ArrayList<String>();
        list.add(hostId);
        TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                //失败
                titleView.setHostHeadPicImg(null);
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                //只有一个主播的信息
                titleView.setHostHeadPicImg(timUserProfiles.get(0));
            }
        });
        // 添加自己的头像到titleView上。
        titleView.addWatcher(APP.getApp().getSelfProfile());

        //请求已经加入房间的其他成员信息
        GetWatchersRequest request = new GetWatchersRequest();
        request.setOnResultListener(new HttpRequestUtils.OnResultListener<Set<String>>() {
            @Override
            public void onFail(int code, String msg) {}
            @Override
            public void onSuccess(Set<String> watchersUserIdSet) {
                if (watchersUserIdSet != null) {
                    List<String> watcherList = new ArrayList<String>();
                    watcherList.addAll(watchersUserIdSet);
                    TIMFriendshipManager.getInstance().getUsersProfile(watcherList, new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {}
                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            //将腾讯IM服务器返回的其他已经在房间中的观众信息添加到本地RecyclerView上
                            titleView.addWatchers(timUserProfiles);
                        }
                    });
                }
            }
        });
        String reqestUrl = request.getRequestUrl(roomId);
        request.request(reqestUrl);
    }

    @Override
    public void onBackPressed() {
        quitRoom();
    }

    private void quitRoom() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_LEAVE);
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(WatcherLiveActivity.this, "退出直播房间", Toast.LENGTH_SHORT).show();
                        logout();
                    }
                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Toast.makeText(WatcherLiveActivity.this,"onError1",Toast.LENGTH_SHORT).show();
                        logout();
                    }
                });
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(WatcherLiveActivity.this,"主播离开，直播结束",Toast.LENGTH_SHORT).show();
                //finish();
                ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        //Toast.makeText(WatcherLiveActivity.this, "退出直播房间", Toast.LENGTH_SHORT).show();
                        logout();
                    }
                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Toast.makeText(WatcherLiveActivity.this,"onError1",Toast.LENGTH_SHORT).show();
                        logout();
                    }
                });
            }
        });

        //发送退出消息给服务器，并停止心跳定时程序
        QuitRoomRequest request = new QuitRoomRequest();
        String url = request.getRequestUrl(roomId, APP.getApp().getSelfProfile().getIdentifier());
        request.request(url);

        APP.getApp().stopHeartBeatTimer();
    }

    private void logout() {
        //ILiveLoginManager.getInstance().iLiveLogout(null);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(giftPagerSelectDialog != null){
            giftPagerSelectDialog.dismiss();
        }
        if(titleView.adapter.userInfoDialog != null){
            titleView.adapter.userInfoDialog.dismiss();
        }
        heartTimer.cancel();
    }
}
