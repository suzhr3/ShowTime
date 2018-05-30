package edu.sysu.showtime.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.MsgInfo;

//自定义包含四个弹幕控件的弹幕组件，即一次最多显示4个弹幕
public class DanmuView extends LinearLayout {
    private DanmuItemView danmu0, danmu1, danmu2, danmu3;
    //因为要经常对弹幕消息队列进行插入删除，采取ArrayList会很低效，因此这里采用插入删除都高效的LinkedList
    private List<MsgInfo> danmuList = new LinkedList<MsgInfo>();

    public DanmuView(Context context) {
        super(context);
        init();
    }
    public DanmuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public DanmuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu, this, true);
        initViews();
    }
    private void initViews() {
        danmu0 = findViewById(R.id.danmu0);
        danmu1 = findViewById(R.id.danmu1);
        danmu2 = findViewById(R.id.danmu2);
        danmu3 = findViewById(R.id.danmu3);

        danmu0.setVisibility(INVISIBLE);
        danmu1.setVisibility(INVISIBLE);
        danmu2.setVisibility(INVISIBLE);
        danmu3.setVisibility(INVISIBLE);

        danmu0.setOnAvaliableListener(avaliableListener);
        danmu1.setOnAvaliableListener(avaliableListener);
        danmu2.setOnAvaliableListener(avaliableListener);
        danmu3.setOnAvaliableListener(avaliableListener);

    }
    //弹幕Item空出事件监听器
    private  DanmuItemView.OnAvaliableListener avaliableListener = new DanmuItemView.OnAvaliableListener(){
        @Override
        public void onAvaliable() {
            //回调方法被触发了，说明有可以使用的弹幕Item了，
            //故从弹幕队列中取出之前缓存下来的弹幕消息，然后显示出来
            if(danmuList.size() > 0) {
                MsgInfo chatMsgInfo = null;
                //加锁，避免对弹幕队列的并发修改
                synchronized (this){
                    chatMsgInfo = danmuList.remove(0);
                }
                addDanmuMsgInfo(chatMsgInfo);
            }
        }
    };

    public void addDanmuMsgInfo(MsgInfo danmuInfo) {
        //加锁，避免对弹幕队列的并发修改
        synchronized (this) {
            DanmuItemView avaliableItemView = getAvaliableItem();
            if (avaliableItemView == null) {
                //说明没有可用的danmuItemView，故先将多余的弹幕信息缓存到队列中，等待有可用的弹幕时再去显示出来
                danmuList.add(danmuInfo);
            } else {    //说明有可用的itemView，将这个弹幕信息显示出来
                avaliableItemView.showDanmuMsg(danmuInfo);
            }
        }
    }
    private DanmuItemView getAvaliableItem() {
        //获取可用的danmu item view
        if (danmu0.getVisibility() != VISIBLE) {
            return danmu0;
        }
        if (danmu1.getVisibility() != VISIBLE) {
            return danmu1;
        }
        if (danmu2.getVisibility() != VISIBLE) {
            return danmu2;
        }
        if (danmu3.getVisibility() != VISIBLE) {
            return danmu3;
        }
        return null;
    }
}
