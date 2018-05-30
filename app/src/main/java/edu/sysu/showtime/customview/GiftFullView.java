package edu.sysu.showtime.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.TIMUserProfile;

import java.util.LinkedList;
import java.util.List;

import edu.sysu.showtime.bean.GiftFullInfo;
import edu.sysu.showtime.bean.GiftInfo;

public class GiftFullView extends RelativeLayout {
    private GiftPorcheView porcheView;
    private boolean isScreenAvaliable = true;
    private List<GiftFullInfo> porcheCachedList = new LinkedList<GiftFullInfo>();

    public GiftFullView(Context context) {
        super(context);
        init();
    }
    public GiftFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GiftFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {

    }

    public void showGift(GiftInfo giftInfo, TIMUserProfile userProfile) {
        if(isScreenAvaliable) {
            if (giftInfo.giftId == GiftInfo.Gift_BaoShiJie.giftId) {
                showPorcheView(userProfile);    //显示保时捷礼物
                isScreenAvaliable = false;
            }
        }else{
            //缓存待发送的保时捷礼物信息
            GiftFullInfo giftFullInfo = new GiftFullInfo(giftInfo, userProfile);
            porcheCachedList.add(giftFullInfo);
        }
    }

    private void showPorcheView(final TIMUserProfile userProfile) {
        if (porcheView == null) {
            porcheView = new GiftPorcheView(getContext());

            //这里没有给当前GiftFullView设置布局，故通过动态添加控件的方式将porcheView添加到当前布局的正中央，
            //并且大小是wrap_content
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    (LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(porcheView, params);    //将这个保时捷的view添加到当前视图中

            if(porcheView.isAvaliable()){
                porcheView.showPorchGift(userProfile);
            }

            porcheView.setOnAvaliableListener(new GiftPorcheView.OnAvaliableListener() {
                @Override
                public void onAvaliable() {
                    isScreenAvaliable = true;
                    int size = porcheCachedList.size();
                    //如果缓存队列中有待发送的保时捷信息，则取出第一个来发送
                    if(size > 0){
                        GiftFullInfo giftUserInfo= porcheCachedList.remove(0);
                        showGift(giftUserInfo.giftInfo, giftUserInfo.userProfile);
                    }
                }
            });
        }
        //开始显示保时捷动画
        porcheView.showPorchGift(userProfile);
    }
}
