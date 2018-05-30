package edu.sysu.showtime.createlive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMUserProfile;

import edu.sysu.showtime.APP;
import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.CreateRoomParam;
import edu.sysu.showtime.bean.RoomInfo;
import edu.sysu.showtime.host.HostLiveActivity;
import edu.sysu.showtime.utils.HttpRequestUtils;
import edu.sysu.showtime.utils.ImgUtils;
import edu.sysu.showtime.utils.PicChooseUtils;

public class CreateRoomActivity extends AppCompatActivity {
    private View coverView;
    private ImageView coverImg;
    private TextView coverTipTxt;
    private EditText roomTitleEdt;
    private TextView roomNumber;
    private TextView createRoomBtn;

    private String coverPicUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        initViews();
        setListener();
        setTitleBar();
    }
    private void initViews() {
        coverView = findViewById(R.id.cover_framelayout);
        coverImg = findViewById(R.id.coverImg);
        coverTipTxt = findViewById(R.id.set_cover_tip);
        roomTitleEdt = findViewById(R.id.room_title);
        roomNumber = findViewById(R.id.room_number);
        createRoomBtn = findViewById(R.id.create_room_btn);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.cover_framelayout){
                choosePic();            //切换封面
            } else if(id == R.id.create_room_btn){
                createRoom();           //创建直播房间
            }
        }
    };
    private void setListener() {
        coverView.setOnClickListener(clickListener);        //切换封面
        createRoomBtn.setOnClickListener(clickListener);    //创建直播房间
    }
    private void setTitleBar() {
        Toolbar titlebar = findViewById(R.id.titlebar);
        titlebar.setTitle("开始我的直播");
        titlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(titlebar);
    }

    private PicChooseUtils picChooseUtils = null;
    //从相册或者相机选择图片
    private void choosePic() {
        if (picChooseUtils == null) {
            picChooseUtils = new PicChooseUtils(CreateRoomActivity.this, "coverPic");
            picChooseUtils.setOnChooseResultListener(new PicChooseUtils.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) { //封面选择成功，并且成功上传到了七牛云上存储
                    coverPicUrl = url;
                    ImgUtils.load(url, coverImg);
                    coverTipTxt.setVisibility(View.GONE);
                }
                @Override
                public void onFail(final String msg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateRoomActivity.this, "封面更新失败：" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        picChooseUtils.showPicChooseDialog();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //在打开相册（调用Intent）获取到数据后，返回的数据会跳到这里，因为picChooseUtils不是Activity，
        //所以picChooseUtils中收不到返回的图片数据。但是这里获取到的数据是要传给picChooseUtils
        //使用的，因此这里判断是否要给picChooseUtils使用，只需看它是否为null即可。
        //如果是要给picChooseUtils用的话，则调用它的getResult来获取返回的数据
        if(picChooseUtils != null){
            picChooseUtils.getReturnResult(requestCode, resultCode, data);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //向服务器请求创建房间
    private void createRoom() {
        if(coverPicUrl == null){
            Toast.makeText(CreateRoomActivity.this, "请设置直播封面", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(roomTitleEdt.getText())){
            Toast.makeText(CreateRoomActivity.this, "请设置直播标题", Toast.LENGTH_SHORT).show();
        } else{
            //1.先去请求服务器，获取新的roomId
            CreateRoomParam param = new CreateRoomParam();
            TIMUserProfile selfProfile = APP.getApp().getSelfProfile();
            if(selfProfile != null){
                String nickName = selfProfile.getNickName();
                param.userId = selfProfile.getIdentifier();
                param.userName = TextUtils.isEmpty(nickName) ? selfProfile.getIdentifier() : nickName;
                param.userHeadPic = selfProfile.getFaceUrl();
                param.liveTitle = roomTitleEdt.getText().toString();
                param.liveCoverPic = coverPicUrl;

                //创建房间，设置回调监听时传入RoomInfo作为泛型参数
                CreateRoomRequest request = new CreateRoomRequest();
                request.setOnResultListener(new HttpRequestUtils.OnResultListener<RoomInfo>() {
                    @Override
                    public void onFail(int code, String msg) {  //创建失败
                        Toast.makeText(CreateRoomActivity.this, "创建房间失败：" + msg + "\n响应码：" + code, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(RoomInfo roomInfo) {
                        //2.请求创建房间成功之后，跳转到直播界面
                        Intent intent = new Intent();
                        intent.setClass(CreateRoomActivity.this, HostLiveActivity.class);
                        intent.putExtra("roomId", roomInfo.roomId);
                        startActivity(intent);
                        finish();
                    }
                });
                String requestUrl = request.getRequestUrl(param);
                request.request(requestUrl);
            }else{
                Toast.makeText(CreateRoomActivity.this, "个人信息已过时，请重新登录！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(picChooseUtils != null){
            picChooseUtils.dismiss();       //释放对话框资源
        }
    }
}
