package edu.sysu.showtime.livelist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.RoomInfo;
import edu.sysu.showtime.utils.HttpRequestUtils;

public class homePageFragment extends Fragment {
    private Toolbar titlebar;
    private SwipeRefreshLayout swipeRefreshLayout;      //下拉刷新布局
    private ListView liveListView;
    private LiveListAdapter adapter;
    private List<RoomInfo> roomList = new ArrayList<RoomInfo>();
    public static int index = 0;    //从0开始，也就是数据库中存的第一页开始获取20个房间信息

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        init(view);
        setTitleBar();
        requestRoomList();  //初始化时请求服务器获取直播间列表信息
        return view;
    }

    private void init(View view) {
        titlebar = view.findViewById(R.id.titlebar);
        liveListView = view.findViewById(R.id.live_list);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_list);
        //传入数据源roomList和适配器adapter
        adapter = new LiveListAdapter(getActivity().getApplicationContext(), roomList);
        liveListView.setAdapter(adapter);
        //设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {   //触发了下拉刷新，请求服务器，重新获取新的直播房间信息
                requestRoomList();
            }
        });
    }
    private void setTitleBar() {
        titlebar.setTitle("热播列表");
        titlebar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(titlebar);
    }

    private void requestRoomList() {
        //请求前20个数据
        GetRoomListRequest request = new GetRoomListRequest();
        request.setOnResultListener(new HttpRequestUtils.OnResultListener<List<RoomInfo>>() {
            @Override
            public void onSuccess(List<RoomInfo> roomList) {
                int newRoomSize = roomList.size();
                adapter.removeOldRoomList();        //删除过期的直播
                adapter.addNewRoomList(roomList);   //再添加新获取到的room信息
                //index += newRoomSize;        //下一次请求就从newRoomSize后的房间信息开始获取
                swipeRefreshLayout.setRefreshing(false);
            }
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(getActivity(), "刷新失败：" + msg, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        String requestUrl = request.getRequestUrl(index);
        request.request(requestUrl);
    }
}
