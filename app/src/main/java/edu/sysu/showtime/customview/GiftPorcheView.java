package edu.sysu.showtime.customview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.TIMUserProfile;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.GiftInfo;
import edu.sysu.showtime.utils.ImgUtils;

public class GiftPorcheView extends LinearLayout {
    private ImageView userHeadPic;
    private TextView userName;
    private TextView giftName;
    private ImageView wheelBack;
    private ImageView wheelFront;

    private AnimationDrawable wheelBackDrawable;
    private AnimationDrawable wheelFrontDrawable;

    private Animation carInAnim;
    private Animation carOutAnim;

    private boolean avaliable = true;

    public GiftPorcheView(Context context) {
        super(context);
        init();
    }
    public GiftPorcheView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GiftPorcheView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_porche_item, this, true);
        initViews();
    }

    private void initViews() {
        userHeadPic = findViewById(R.id.user_headPic);
        userName = findViewById(R.id.user_name);
        giftName = findViewById(R.id.gift_name);
        wheelBack = findViewById(R.id.wheel_back);
        wheelFront = findViewById(R.id.wheel_front);

        wheelBackDrawable = (AnimationDrawable) wheelBack.getDrawable();   //取得背景的Drawable对象
        wheelBackDrawable.setOneShot(false);    //设置执行次数，无限循环
        wheelFrontDrawable = (AnimationDrawable) wheelFront.getDrawable(); //取得背景的Drawable对象
        wheelFrontDrawable.setOneShot(false);   //设置执行次数，无限循环

        setVisibility(INVISIBLE);
    }

    public void showPorchGift(TIMUserProfile userProfile){
        bindData(userProfile);
        post(new Runnable() {
            @Override
            public void run() {
                createAnim();   //动态创建动画，而不像之前那样从xml文件中静态加载
                startAnimation(carInAnim);  //开始动画
            }
        });
    }
    private void createAnim() {
        int width = getWidth();
        int left = getLeft();
        carInAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0 - (width + left) * 1.0f / width, //from X
                Animation.RELATIVE_TO_SELF, 0,//to X
                Animation.RELATIVE_TO_SELF, -1,//fromY
                Animation.RELATIVE_TO_SELF, 0 //to Y
        );
        carInAnim.setDuration(2000);
        carInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
                wheelBackDrawable.start();      //开始轮胎背景图的循环动画
                wheelFrontDrawable.start();
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation(carOutAnim);     //2s后才执行移出动画
                    }
                }, 2000);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        carOutAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, //from X
                Animation.RELATIVE_TO_SELF, (width + left) * 1.0f / width,//to X
                Animation.RELATIVE_TO_SELF, 0,//fromY
                Animation.RELATIVE_TO_SELF, 1 //to Y
        );
        carOutAnim.setDuration(2000);
        carOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(INVISIBLE);
                wheelBackDrawable.stop();   //停止轮胎背景图的循环动画
                wheelFrontDrawable.stop();

                avaliable = true;
                if (onAvaliableListener != null) {
                    onAvaliableListener.onAvaliable();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void bindData(TIMUserProfile userProfile) {
        String faceUrl = userProfile.getFaceUrl();
        if (TextUtils.isEmpty(faceUrl)) {
            ImgUtils.loadRound(R.drawable.default_head_pic, userHeadPic);
        } else {
            ImgUtils.loadRound(faceUrl, userHeadPic);
        }

        String name = userProfile.getNickName();
        if (TextUtils.isEmpty(name)) {
            name = userProfile.getIdentifier();
        }
        userName.setText(name);

        giftName.setText("送了一个"+ GiftInfo.Gift_BaoShiJie.name);
    }

    public boolean isAvaliable() {
        return getVisibility() == INVISIBLE;
    }

    private OnAvaliableListener onAvaliableListener;

    public void setOnAvaliableListener(OnAvaliableListener l) {
        onAvaliableListener = l;
    }

    public interface OnAvaliableListener {
        void onAvaliable();
    }

}
