package edu.sysu.showtime.request;

import edu.sysu.showtime.utils.HttpRequestUtils;

public class GetWatchersRequest extends HttpRequestUtils {
    private static final String urlHeader = "http://showtime.butterfly.mopaasapp.com/RoomServlet?action=getWatchers";

    private static final String Param_Room_id = "roomId";

    public String getRequestUrl(int roomId) {
        return urlHeader + "&" + Param_Room_id + "=" + roomId;
    }

    @Override
    public Object onSuccess(String body) {
        return gson.fromJson(body, GetWatchersResponseObj.class).watcherIdSet;
    }
}
