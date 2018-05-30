package edu.sysu.showtime.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tencent.TIMUserProfile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.GiftDanmuInfo;
import edu.sysu.showtime.bean.GiftInfo;

//类似于自定义的弹幕消息，因为弹出礼物其实也是类似于弹幕的思想，这里也是用两个giftRepeatItemView来做复用
public class GiftDanmuRepeatView extends LinearLayout {
    private GiftDanmuRepeatItemView gift_item0, gift_item1;
    //因为要经常对弹幕消息队列进行插入删除，采取ArrayList会很低效，因此这里采用插入删除都高效的LinkedList
    private List<GiftDanmuInfo> giftDanmuList = new LinkedList<GiftDanmuInfo>();

    public GiftDanmuRepeatView(Context context) {
        super(context);
        init();
    }
    public GiftDanmuRepeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GiftDanmuRepeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat, this, true);
        initViews();
        gift_item0.setVisibility(INVISIBLE);
        gift_item1.setVisibility(INVISIBLE);
    }
    private void initViews() {
        gift_item0 = findViewById(R.id.gift_item0);
        gift_item1 = findViewById(R.id.gift_item1);

        gift_item0.setOnAvaliableListener(avaliableListener);
        gift_item1.setOnAvaliableListener(avaliableListener);
    }

    //礼物弹幕Item空出事件监听器
    private GiftDanmuRepeatItemView.OnAvaliableListener avaliableListener = new GiftDanmuRepeatItemView.OnAvaliableListener() {
        @Override
        public void onAvaliable() {
            //回调方法被触发了，说明有可以使用的礼物弹幕Item了，
            //故从礼物弹幕队列中取出之前缓存下来的弹幕消息，然后显示出来
            if(giftDanmuList.size() > 0) {
                GiftDanmuInfo firstGiftInfo = null;
                //加锁，避免对弹幕队列的并发修改
                synchronized (this){
                    firstGiftInfo = giftDanmuList.remove(0);
                }
                addGiftDanmuMsgInfo(firstGiftInfo.giftInfo, firstGiftInfo.repeatId, firstGiftInfo.userProfile);

                //找出缓存队列中和第一个礼物相同的连发信息
                List<GiftDanmuInfo> leftSameInfos = new ArrayList<GiftDanmuInfo>();
                for (GiftDanmuInfo info : giftDanmuList){
                    if(info.giftInfo.giftId == firstGiftInfo.giftInfo.giftId
                            && info.userProfile.getIdentifier().equals(firstGiftInfo.userProfile.getIdentifier())
                            && info.repeatId.equals(firstGiftInfo.repeatId)){
                        //三者同时满足，说明是同一个人发出的连发礼物
                        leftSameInfos.add(info);
                    }
                }
                giftDanmuList.removeAll(leftSameInfos);
                for(GiftDanmuInfo info : leftSameInfos){
                    addGiftDanmuMsgInfo(info.giftInfo, info.repeatId, info.userProfile);
                }
            }
        }
    };

    //显示礼物
    public void addGiftDanmuMsgInfo(GiftInfo giftInfo, String repeatId, TIMUserProfile userProfile) {
        //加锁，避免对礼物弹幕队列的并发修改
        synchronized (this) {
            GiftDanmuRepeatItemView avaliableItemView = getAvaliableItem(giftInfo, repeatId, userProfile);
            if (avaliableItemView == null) {
                //说明没有可用的giftDanmuItemView，故先将多余的礼物弹幕信息缓存到队列中，等待有可用的礼物弹幕时再去显示出来
                GiftDanmuInfo giftDanmuInfo = new GiftDanmuInfo(giftInfo, repeatId, userProfile);
                giftDanmuList.add(giftDanmuInfo);
            } else {    //说明有可用的itemView，将这个弹幕信息显示出来
                avaliableItemView.showGiftDanmuMsg(giftInfo, repeatId, userProfile);
            }
        }
    }
    private GiftDanmuRepeatItemView getAvaliableItem(GiftInfo giftInfo, String repeatId, TIMUserProfile userProfile) {
        //看当前是否有正在显示的匹配的礼物弹幕，有就直接重用
        if(gift_item0.isMatch(giftInfo, repeatId, userProfile)){
            return gift_item0;
        }
        if(gift_item1.isMatch(giftInfo, repeatId, userProfile)){
            return gift_item1;
        }

        //没有正在显示的可以重用的弹幕，故重新获取可用的giftDanmu item view
        if(gift_item0.isAvaliable()){
            return gift_item0;
        }
        if(gift_item1.isAvaliable()){
            return gift_item1;
        }
        return null;
    }
}
