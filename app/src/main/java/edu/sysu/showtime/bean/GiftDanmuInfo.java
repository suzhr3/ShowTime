package edu.sysu.showtime.bean;

import com.tencent.TIMUserProfile;

public class GiftDanmuInfo {
    public GiftInfo giftInfo;
    public String repeatId;
    public TIMUserProfile userProfile;

    public GiftDanmuInfo(GiftInfo giftInfo, String repeatId, TIMUserProfile userProfile) {
        this.giftInfo = giftInfo;
        this.repeatId = repeatId;
        this.userProfile = userProfile;
    }
}
