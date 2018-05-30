package edu.sysu.showtime.bean;

import com.tencent.TIMUserProfile;

public class GiftFullInfo {
    public GiftInfo giftInfo;
    public TIMUserProfile userProfile;

    public GiftFullInfo(GiftInfo giftInfo, TIMUserProfile userProfile) {
        this.giftInfo = giftInfo;
        this.userProfile = userProfile;
    }
}
