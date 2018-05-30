package edu.sysu.showtime.createlive;

import edu.sysu.showtime.bean.CreateRoomParam;
import edu.sysu.showtime.utils.HttpRequestUtils;

public class CreateRoomRequest extends HttpRequestUtils {
    private static final String urlHeader = "http://showtime.butterfly.mopaasapp.com/RoomServlet?action=createRoom";

    private static final String Param_User_id = "user_id";
    private static final String Param_User_name = "user_name";
    private static final String Param_User_headPic = "user_headPic";
    private static final String Param_Live_cover = "live_coverPic";
    private static final String Param_Live_title = "live_title";

    public String getRequestUrl(CreateRoomParam param) {
        return urlHeader + "&" + Param_User_id + "=" + param.userId
                         + "&" + Param_User_name + "=" + param.userName
                         + "&" + Param_User_headPic + "=" + param.userHeadPic
                         + "&" + Param_Live_cover + "=" + param.liveCoverPic
                         + "&" + Param_Live_title + "=" + param.liveTitle;
    }

    @Override
    public Object onSuccess(String body) {
        return gson.fromJson(body, CreateRoomResponseObj.class).data;
    }
}
