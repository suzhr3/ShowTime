package edu.sysu.showtime.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.MsgInfo;
import edu.sysu.showtime.utils.ImgUtils;

//自定义单个的弹幕控件
public class DanmuItemView extends RelativeLayout {
    private ImageView userHeadImg;
    private TextView userName;
    private TextView content;

    private Animation animation;

    public DanmuItemView(Context context) {
        super(context);
        init();
    }
    public DanmuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public DanmuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu_item, this, true);
        intViews();
        //加载动画
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.danmu_item_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                DanmuItemView.this.setVisibility(VISIBLE);     //动画开始，显示这个弹幕
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束，隐藏这个弹幕，并回调通知DanmuView当前这个弹幕可以被使用了
                DanmuItemView.this.setVisibility(INVISIBLE);
                if (avaliableListener != null) {
                    avaliableListener.onAvaliable();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void intViews() {
        userHeadImg = findViewById(R.id.user_headPic);
        userName = findViewById(R.id.user_name);
        content = findViewById(R.id.chat_content);
    }

    //显示单个弹幕信息
    public void showDanmuMsg(MsgInfo danmuInfo){
        String faceUrl = danmuInfo.senderFaceUrl;
        if (TextUtils.isEmpty(faceUrl)) {
            ImgUtils.loadRound(R.drawable.default_head_pic, userHeadImg);
        } else {
            ImgUtils.loadRound(faceUrl, userHeadImg);
        }
        userName.setText(danmuInfo.senderName);
        content.setText(danmuInfo.chatContent);

        //启动弹幕移动的动画
        //在动画监听里面做处理，调用post保证在动画结束之后再start，从而解决start之后直接end的情况
        this.post(new Runnable() {
            @Override
            public void run() {
                DanmuItemView.this.startAnimation(animation);
            }
        });
    }

    public interface OnAvaliableListener {
        public void onAvaliable();
    }
    private OnAvaliableListener avaliableListener;

    public void setOnAvaliableListener(OnAvaliableListener l) {
        avaliableListener = l;
    }
}
