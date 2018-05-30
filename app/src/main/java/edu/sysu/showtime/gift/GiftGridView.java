package edu.sysu.showtime.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import edu.sysu.showtime.bean.GiftInfo;

//自定义礼物九宫格控件
public class GiftGridView extends GridView {
    public GiftGridViewAdapter adapter;
    private List<GiftInfo> giftList = new ArrayList<GiftInfo>();


    public GiftGridView(Context context) {
        super(context);
        init();
    }
    public GiftGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GiftGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        setNumColumns(4);
        adapter = new GiftGridViewAdapter(getContext(), giftList);
        setAdapter(adapter);    //将adapter绑定到该自定义的GridView上
    }

    public void setGiftListData(List<GiftInfo> giftList){
        adapter.giftList = giftList;       //数据集发生了修改
        adapter.notifyDataSetChanged(); //通知adapter重新加载新的数据集
    }

    //获取一个gridView的高度
    public int getGridViewHeight() {
        // 要获取gridview的高度，只需获取adapter中的一个数据（这里取第1个位置的数据）的高度，
        // 并乘以gridview对应的行数（这里为2）即可
        View item = adapter.getView(0, null, this);
        item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int height = item.getMeasuredHeight();
        return height * 2;
    }


    //在GiftPagerAdapter中设置了该监听器，用于监听当某个gift被点击时（触发onClick）的事件
    public interface OnGiftItemClickListener {
        void onClick(GiftInfo giftInfo);        //在adapter中去触发该回调事件
    }
    private OnGiftItemClickListener giftItemClickListener;

    public void setOnGiftItemClickListener(OnGiftItemClickListener l) {
        giftItemClickListener = l;
        adapter.setListener(giftItemClickListener);
    }
}
