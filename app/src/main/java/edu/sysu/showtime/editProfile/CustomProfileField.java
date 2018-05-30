package edu.sysu.showtime.editProfile;

import com.tencent.TIMFriendshipManager;

public class CustomProfileField {
    //自定义字段
    public static final String PREFIX = "TIM_PROFILE_FLAG_";
    public static final String CUSTOM_RENZHENG = PREFIX + "RENZHENG";
    public static final String CUSTOM_LEVEL = PREFIX + "LEVEL";
    public static final String CUSTOM_GET = PREFIX + "GETNUMS";
    public static final String CUSTOM_SEND = PREFIX + "SENDNUMS";

    //腾讯基础字段
    public static final long allBaseInfo =
            TIMFriendshipManager.TIM_PROFILE_FLAG_BIRTHDAY |
            TIMFriendshipManager.TIM_PROFILE_FLAG_FACE_URL |
            TIMFriendshipManager.TIM_PROFILE_FLAG_GENDER |
            TIMFriendshipManager.TIM_PROFILE_FLAG_LANGUAGE |
            TIMFriendshipManager.TIM_PROFILE_FLAG_LOCATION |
            TIMFriendshipManager.TIM_PROFILE_FLAG_NICK |
            TIMFriendshipManager.TIM_PROFILE_FLAG_SELF_SIGNATURE |
            TIMFriendshipManager.TIM_PROFILE_FLAG_REMARK |
            TIMFriendshipManager.TIM_PROFILE_FLAG_GROUP;
}
