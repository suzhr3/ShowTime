package edu.sysu.showtime.host;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.util.Random;
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
import edu.sysu.showtime.livelist.HostOperateStatus;
import edu.sysu.showtime.model.Constants;
import edu.sysu.showtime.request.JoinRoomRequest;
import edu.sysu.showtime.request.QuitRoomRequest;
import edu.sysu.showtime.title.TitleView;
import tyrantgit.widget.HeartLayout;

public class HostLiveActivity extends AppCompatActivity {
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
    private Random heartRandom = new Random();

    public HostOperateStatus hostOperateStatus;

    private int roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);

        initViews();
        initSDK();
        createRoom();
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
            //因为发送时用的是sendCustommMsg，所以这里只实现该接收自定义消息的回调方法即可
            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {

                String name = userProfile.getNickName();
                if (TextUtils.isEmpty(name)) {
                    name = userProfile.getIdentifier();
                }
                if(cmd.getCmd() == Constants.CHAT_MSG_LIST){
                    String content = cmd.getParam();
                    MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                    chatMsgListView.addMsgInfo(msgInfo);
                } else if (cmd.getCmd() == Constants.CHAT_MSG_DANMU) {
                    //如果是弹幕模式的话，除了将信息发送到chatMsgListView列表外，还需要显示成弹幕
                    String content = cmd.getParam();
                    MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                    chatMsgListView.addMsgInfo(msgInfo);
                    danmuView.addDanmuMsgInfo(msgInfo);
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_ENTER) {
                    //用户进入直播，显示进入消息，并添加到顶部RecyclerView中显示
                    titleView.addWatcher(userProfile);
                    //mVipEnterView.showVipEnter(userProfile);
                    String content = "进入直播间";
                    MsgInfo msgInfo = new MsgInfo(id, name, userProfile.getFaceUrl(), content);
                    chatMsgListView.addMsgInfo(msgInfo);
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_LEAVE) {
                    //显示用户离开消息
                    titleView.removeWatcher(userProfile);
                    String content = "离开直播间";
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
                    } else if (giftInfo.type == GiftInfo.Type.ContinueGift) {
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

    private int getRandomColor() {
        int color = Color.rgb(heartRandom.nextInt(255), heartRandom.nextInt(255), heartRandom.nextInt(255));
        return color;
    }

    private void initViews() {
        //整个父布局，为其设置屏幕显示尺寸变化监听事件
        parent_layout = findViewById(R.id.size_change_layout);
        avRootView = findViewById(R.id.avRootView);
        titleView = findViewById(R.id.title_view);
        bottom_ontrol_view = findViewById(R.id.bottom_control_view);
        ImageView gift = bottom_ontrol_view.findViewById(R.id.gift);
        gift.setVisibility(View.INVISIBLE);
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
                bottom_ontrol_view.setVisibility(View.GONE);
                chatView.setVisibility(View.VISIBLE);
            }
        });
        //设置底部自定义的控制栏的点击监听回调
        bottom_ontrol_view.setOnControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChatClick() {
                //切换为显示聊天操作栏，并自动弹出软键盘
                bottom_ontrol_view.setVisibility(View.GONE);
                chatView.setVisibility(View.VISIBLE);
                showKeyBoard();
            }
            @Override
            public void onCloseClick() {
                //关闭直播
                quitLive();
            }
            @Override
            public void onOptionClick(View view) {
                //显示选择操作对话框
                showControlDialog(view);
            }
            @Override
            public void onGiftClick() {
                //主播端没有发送礼物功能
            }
        });
        chatView.setOnChatSendListener(new ChatView.OnSendClickListener() {
            @Override
            public void onChatSend(ILVCustomCmd customCmd) {
                //接收发送按钮监听回调接口返回的数据customCmd，并发送聊天消息
                sendMsg(customCmd);
            }
        });
    }

    public HostControlDialog controlDialog = null;

    //显示选择操作对话框
    private void showControlDialog(View view) {
        final ImageView option = bottom_ontrol_view.findViewById(R.id.option);
        if(controlDialog == null){
            controlDialog = new HostControlDialog(this);
            controlDialog.setOnControlClickListener(new HostControlDialog.OnControlClickListener() {
                @Override
                public void onBeautyClick() {
                    //点击美颜
                    option.setImageResource(R.drawable.icon_op_open);
                    hostOperateStatus.switchBeauty();
                }
                @Override
                public void onFlashClick() {
                    //点击闪光灯
                    option.setImageResource(R.drawable.icon_op_open);
                    hostOperateStatus.switchFlash();
                }
                @Override
                public void onVoiceClick() {
                    //点击静音
                    option.setImageResource(R.drawable.icon_op_open);
                    hostOperateStatus.switchVoice();
                }
                @Override
                public void onCameraClick() {
                    //点击翻转照相机
                    option.setImageResource(R.drawable.icon_op_open);
                    hostOperateStatus.switchCamera();
                }
            });
            controlDialog.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    option.setImageResource(R.drawable.icon_op_open);
                }
            });
        }
        //更新控制栏的图标
        controlDialog.updateViewIcon(hostOperateStatus.isBeautyOn, hostOperateStatus.isFlashOn, hostOperateStatus.isVoiceOn);
        controlDialog.showDialogOnBottom(view);
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

    //创建直播房间
    private void createRoom() {
        roomId = getIntent().getIntExtra("roomId", -1);
        if(roomId < 0){
            Toast.makeText(this.getApplicationContext(), "房间号不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        //创建房间配置项
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(ILiveLoginManager.getInstance().getMyUserId()).
                controlRole("LiveMaster")   //角色设置，设置为主播
                .autoFocus(true)
                //.autoMic(hostOperateStatus.isVoiceOn)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收
        //创建直播房间
        ILVLiveManager.getInstance().createRoom(roomId, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.i("test", "创建房间成功");
                titleView.setHostHeadPicImg(APP.getApp().getSelfProfile());
                hostOperateStatus = new HostOperateStatus();
                //开始发送定时心跳包
                APP.getApp().startHeartBeatTimer(roomId);
                //调用后台接口更新房间信息
                JoinRoomRequest request = new JoinRoomRequest();
                String url = request.getRequestUrl(roomId, APP.getApp().getSelfProfile().getIdentifier());
                request.request(url);
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.i("test", "创建房间失败");
                Toast.makeText(HostLiveActivity.this, "创建直播失败！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        quitLive();
    }
    private void quitLive() {
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
                        Log.i("test", "退出成功");
                        logout();
                    }
                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Log.i("test", "退出失败1");
                        logout();
                    }
                });
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.i("test", "退出失败2");
                logout();
            }
        });

        //发送退出消息给服务器，并停止心跳定时程序
        QuitRoomRequest request = new QuitRoomRequest();
        String url = request.getRequestUrl(roomId, APP.getApp().getSelfProfile().getIdentifier());
        request.request(url);

        APP.getApp().stopHeartBeatTimer();
    }

    private void logout() {
        Toast.makeText(HostLiveActivity.this, "退出直播房间", Toast.LENGTH_SHORT).show();
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
        if(controlDialog != null){
            controlDialog.dismiss();
        }
        if(titleView.adapter.userInfoDialog != null){
            titleView.adapter.userInfoDialog.dismiss();
        }
        super.onDestroy();
    }
}
