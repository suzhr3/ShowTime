package edu.sysu.showtime.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.TIMUserProfile;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.GiftInfo;
import edu.sysu.showtime.utils.ImgUtils;

public class GiftDanmuRepeatItemView extends LinearLayout {
    private ImageView user_headerPic;
    private TextView user_name;
    private TextView gift_name;
    private ImageView gift_img;
    private TextView gift_num;

    private Animation viewInAnim;
    private Animation viewOutAnim;
    private Animation imgViewInAnim;
    private Animation textScaleAnim;

    public GiftDanmuRepeatItemView(Context context) {
        super(context);
        init();
    }
    public GiftDanmuRepeatItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GiftDanmuRepeatItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat_item, this, true);
        initViews();
        initAnim();
    }

    private void initViews() {
        user_headerPic = findViewById(R.id.user_headPic);
        user_name = findViewById(R.id.user_name);
        gift_name = findViewById(R.id.gift_name);
        gift_img = findViewById(R.id.gift_img);
        gift_num = findViewById(R.id.gift_num);
    }

    private void initAnim() {
        viewInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_view_in);
        viewOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_view_out);
        imgViewInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_img_view_in);
        textScaleAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_num_scale);

        viewInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isInAnimation = true;
                setVisibility(VISIBLE);
                gift_img.setVisibility(INVISIBLE);
                gift_num.setVisibility(INVISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        //在view进入显示完成之后，再进行gift_img的动画
                        post(new Runnable() {
                            @Override
                            public void run() {
                                gift_img.startAnimation(imgViewInAnim);
                            }
                        });
                    }
                });
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgViewInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                gift_img.setVisibility(VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        //在view显示完成之后，再进行gift_num的动画
                        post(new Runnable() {
                            @Override
                            public void run() {
                                gift_num.startAnimation(textScaleAnim);
                            }
                        });
                    }
                });
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textScaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                currentGiftNum++;
                leftGiftNum--;
                gift_num.setText("x" + currentGiftNum);
                gift_num.setVisibility(VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (leftGiftNum > 0) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //在view显示完成之后，继续进行gift_num的动画
                            gift_num.startAnimation(textScaleAnim);
                        }
                    });
                } else {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //在view显示完成之后，再进行img的动画
                            startAnimation(viewOutAnim);
                        }
                    });
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(INVISIBLE);
                giftId = -1;
                leftGiftNum = 0;
                currentGiftNum = 0;
                isInAnimation = false;
                if(avaliableListener != null){
                    avaliableListener.onAvaliable();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private int giftId = -1;
    private String repeatId;
    private String userId;
    private int leftGiftNum = 0;
    private int currentGiftNum = 0;
    private boolean isInAnimation = false;

    public void showGiftDanmuMsg(GiftInfo giftDanmuInfo, String repeatId, TIMUserProfile userProfile) {
        if(giftId == -1){
            giftId = giftDanmuInfo.giftId;
            this.repeatId = repeatId;
            userId = userProfile.getIdentifier();
        }
        leftGiftNum++;

        if(isInAnimation){
            return; //如果正在显示动画中，则不再去设置下面的礼物消息初始化属性
        }
        String faceUrl = userProfile.getFaceUrl();
        if (TextUtils.isEmpty(faceUrl)) {
            ImgUtils.loadRound(R.drawable.default_head_pic, user_headerPic);
        } else {
            ImgUtils.loadRound(faceUrl, user_headerPic);
        }

        String nickName = userProfile.getNickName();
        if (TextUtils.isEmpty(nickName)) {
            nickName = userProfile.getIdentifier();
        }
        user_name.setText(nickName);

        gift_name.setText("送出一个" + giftDanmuInfo.name);
        ImgUtils.load(giftDanmuInfo.giftResId, gift_img);
        gift_num.setText("x" + 1);

        //在动画监听里面做处理，调用post保证在动画结束之后再start，从而解决start之后直接end的情况
        post(new Runnable() {
            @Override
            public void run() {
                startAnimation(viewInAnim);
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

    public boolean isMatch(GiftInfo giftInfo, String repeatId, TIMUserProfile profile){
        if(getVisibility() == VISIBLE){
            boolean sameGift = giftId == giftInfo.giftId;
            boolean sameRepeat = this.repeatId.equals(repeatId);
            boolean sameUser = this.userId.equals(profile.getIdentifier());
            boolean canContinue = giftInfo.type == GiftInfo.Type.ContinueGift;
            return sameGift && sameRepeat && sameUser && canContinue;
        } else {
            return false;
        }
    }
    public boolean isAvaliable() {
        return getVisibility() == INVISIBLE;
    }
}
