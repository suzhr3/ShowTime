package edu.sysu.showtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;

import edu.sysu.showtime.createlive.CreateRoomActivity;
import edu.sysu.showtime.editProfile.EditProfileFragment;
import edu.sysu.showtime.livelist.homePageFragment;

public class MainActivity extends AppCompatActivity {
    private FrameLayout fragment_container;
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupTab();
    }
    private void initViews() {
        fragment_container = findViewById(R.id.fragment_container);
        tabHost = findViewById(R.id.tabHost);
    }
    private void setupTab() {
        tabHost.setup(this, getSupportFragmentManager(), R.id.fragment_container);
        //给tabHost添加三个TabSpec，分别是首页、创建直播按钮、编辑个人信息页面
        {
            TabHost.TabSpec homePage = tabHost.newTabSpec("homePage").setIndicator(getIndicatorView(R.drawable.tab_home_unselected));
            tabHost.addTab(homePage, homePageFragment.class, null);
            tabHost.getTabWidget().setDividerDrawable(null);    //去掉分割线
        }
        {
            TabHost.TabSpec creatLive = tabHost.newTabSpec("creatLive").setIndicator(getIndicatorView(R.drawable.tab_create_live));
            tabHost.addTab(creatLive, null, null);
            tabHost.getTabWidget().setDividerDrawable(null);
        }
        {
            TabHost.TabSpec editProfile = tabHost.newTabSpec("editProfile").setIndicator(getIndicatorView(R.drawable.tab_profile_unselected));
            tabHost.addTab(editProfile, EditProfileFragment.class, null);
            tabHost.getTabWidget().setDividerDrawable(null);
        }

        //因为中间的创建直播房间按钮的tab在加入tabHost时没有设置class，因此需要单独对其做响应处理
        tabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //跳转到创建直播的页面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CreateRoomActivity.class);
                startActivity(intent);
            }
        });
    }

    private View getIndicatorView(int picId) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_indicator, null);
        ImageView icon = view.findViewById(R.id.tab_icon);
        icon.setImageResource(picId);
        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            Log.i("test", "MainActivity_onKeyDown");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
