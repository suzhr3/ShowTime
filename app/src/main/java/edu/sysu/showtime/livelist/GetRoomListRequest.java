package edu.sysu.showtime.livelist;

import edu.sysu.showtime.utils.HttpRequestUtils;

public class GetRoomListRequest extends HttpRequestUtils {
    private static final String urlHeader = "http://showtime.butterfly.mopaasapp.com/RoomServlet?action=getRoomList";

    public String getRequestUrl(int pageIndex) {
        return urlHeader + "&pageIndex=" + pageIndex;
    }

    @Override
    public Object onSuccess(String body) {
        return gson.fromJson(body, GetRoomListResponseObj.class).data;
    }
}
