package edu.sysu.showtime.livelist;

import java.util.List;

import edu.sysu.showtime.bean.ResponseObj;
import edu.sysu.showtime.bean.RoomInfo;

//从服务器返回的数据结构体，这里因为不确定返回的data是单个对象，还是多对象的列表，因此采用泛型
public class GetRoomListResponseObj extends ResponseObj{
    public List<RoomInfo> data;
}
