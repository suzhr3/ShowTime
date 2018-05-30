package edu.sysu.showtime.gift;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.sysu.showtime.bean.GiftInfo;

public class GiftPagerAdapter extends PagerAdapter {
    private static List<GiftInfo> giftTotalList = new ArrayList<GiftInfo>();
    static {
        giftTotalList.add(GiftInfo.Gift_BingGun);
        giftTotalList.add(GiftInfo.Gift_BingJiLing);
        giftTotalList.add(GiftInfo.Gift_MeiGui);
        giftTotalList.add(GiftInfo.Gift_PiJiu);
        giftTotalList.add(GiftInfo.Gift_HongJiu);
        giftTotalList.add(GiftInfo.Gift_Hongbao);
        giftTotalList.add(GiftInfo.Gift_ZuanShi);
        giftTotalList.add(GiftInfo.Gift_BaoXiang);
        giftTotalList.add(GiftInfo.Gift_BaoShiJie);
    }

    private Context context;
    public GiftInfo selectGiftInfo = null;     //gridview中被选中的那一个礼物格子
    private GiftPagerSelectDialog.OnListener listener;
    private List<GiftGridView> gridViewPages = new ArrayList<GiftGridView>();   //保存当前有多少页gridView

    public String repeatId = "";
    public int leftTimes = 5;


    public GiftPagerAdapter(Context context){
        this.context = context;
    }
    public void setListener(GiftPagerSelectDialog.OnListener listener){
        this.listener = listener;
    }
    @Override
    public int getCount() {
        return 2;       //因为这个pageAdapter有2页
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //adapter会自动调用的初始化每一页pager的实例化方法
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //构建viewPager适配器中每一页的pager，这里的每一页pager是一个GridView，这里共2页pager
        final GiftGridView giftGridView = new GiftGridView(context);
        List<GiftInfo> giftSubList = new ArrayList<GiftInfo>();

        int start = position * 8;
        int end = (position + 1) * 8;   //每页显示8个礼物
        int emptyGiftNum = 0;
        if(end > giftTotalList.size()){
            //空白没有礼物的格子用空白礼物GiftInfo.Gift_Empty填充
            emptyGiftNum = end - giftTotalList.size();
            end = giftTotalList.size();
        }
        giftSubList.addAll(giftTotalList.subList(start, end));
        for(int i = 0; i < emptyGiftNum; i++){
            giftSubList.add(GiftInfo.Gift_Empty);
        }
        //将这一页的礼物列表添加到该页的GridView中
        giftGridView.setGiftListData(giftSubList);

        //为该页的gridview礼物列表添加点击监听
        giftGridView.setOnGiftItemClickListener(new GiftGridView.OnGiftItemClickListener() {
            @Override
            public void onClick(GiftInfo giftInfo) {    //当点击了该gridview中的某个giftInfo后
                repeatId = "重新选择了礼物";    //重新选择了一个礼物，连续礼物发送记录器归零
                leftTimes = 5;
                if(listener != null){
                    listener.onSetSendBtnText();
                }

                selectGiftInfo = giftInfo;
                if (giftInfo != null) {
                    if(listener != null){
                        listener.onShowSendBtn();   //说明选中了一个礼物，告诉调用者显示send按钮
                    }
                } else {
                    listener.onHideSendBtn();   //说明没有选中任何礼物，此时选择了空白的格子，故告诉调用者隐藏send按钮
                }

                //通知每一个giftGridView现在是哪个selectGiftInfo被选中了，
                //并让这些gridView去刷新其中的数据
                for (GiftGridView giftGridView : gridViewPages) {
                    giftGridView.adapter.setSelectGiftInfo(selectGiftInfo);
                }
            }
        });
        container.addView(giftGridView);    //将这个gridView加入到这个viewPager容器中保存
        gridViewPages.add(giftGridView); //保存该页gridView

        ViewGroup.LayoutParams params = container.getLayoutParams();    //获取到当前viewPager这个容器的布局参数
        params.height = giftGridView.getGridViewHeight();
        container.setLayoutParams(params);  //修改完container布局参数中的高度后重新设置回去
        return giftGridView;    //返回该页gridView
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //销毁viewPager适配器中的每一个pager，这里也就是每一个gridView
        container.removeView(gridViewPages.remove(position));
    }
}
