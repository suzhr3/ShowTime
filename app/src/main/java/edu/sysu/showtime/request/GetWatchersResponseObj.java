package edu.sysu.showtime.request;

import java.util.Set;

import edu.sysu.showtime.bean.ResponseObj;

//从服务器返回的数据结构体，这里因为不确定返回的data是单个对象，还是多对象的列表，因此采用泛型
public class GetWatchersResponseObj extends ResponseObj{
    public Set<String> watcherIdSet;
}
