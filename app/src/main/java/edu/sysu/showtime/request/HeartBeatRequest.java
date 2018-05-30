package edu.sysu.showtime.request;

import edu.sysu.showtime.utils.HttpRequestUtils;

public class HeartBeatRequest extends HttpRequestUtils {
    private static final String urlHeader = "http://showtime.butterfly.mopaasapp.com/RoomServlet?action=heartBeat";

    private static final String Param_Room_id = "roomId";
    private static final String Param_User_id = "userId";

    public String getRequestUrl(int roomId, String userId) {
        return urlHeader + "&" + Param_Room_id + "=" + roomId
                + "&" + Param_User_id + "=" + userId;
    }

    @Override
    public Object onSuccess(String body) {
        return null;
    }
}
