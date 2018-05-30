package edu.sysu.showtime.gift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.sysu.showtime.R;
import edu.sysu.showtime.bean.GiftInfo;
import edu.sysu.showtime.utils.ImgUtils;

public class GiftGridViewAdapter extends BaseAdapter {
    private Context context;
    public List<GiftInfo> giftList;
    private GiftGridView.OnGiftItemClickListener giftItemClickListener;
    public GiftGridViewAdapter(Context context, List<GiftInfo> giftList){
        this.context = context;
        this.giftList = giftList;
    }
    public void setListener(GiftGridView.OnGiftItemClickListener giftItemClickListener){
        this.giftItemClickListener = giftItemClickListener;     //接收从GridView中传来的监听器对象
    }
    @Override
    public int getCount() {
        return giftList.size();
    }
    @Override
    public Object getItem(int pos) {
        return giftList.get(pos);
    }
    @Override
    public long getItemId(int pos) {
        return pos;
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.view_gift_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        GiftInfo giftInfo = giftList.get(pos);
        holder.bindData(giftInfo);
        return convertView;
    }

    private GiftInfo selectGiftInfo;
    public void setSelectGiftInfo(GiftInfo selectGiftInfo) {
        this.selectGiftInfo = selectGiftInfo;
        notifyDataSetChanged();     //被选择的礼物发生了改变，通知adapter重新加载新的数据集
    }

    private class ViewHolder {
        private View view;
        private ImageView giftImg;
        private TextView giftExp;
        private TextView giftName;
        private ImageView giftSelectIcon;

        public ViewHolder(View view) {
            this.view = view;
            giftImg = view.findViewById(R.id.gift_img);
            giftExp = view.findViewById(R.id.gift_exp);
            giftName = view.findViewById(R.id.gift_name);
            giftSelectIcon = view.findViewById(R.id.gift_isSelected);
        }
        public void bindData(final GiftInfo giftInfo) {
            ImgUtils.load(giftInfo.giftResId, giftImg);
            if (giftInfo != GiftInfo.Gift_Empty) {  //不是空礼物
                giftExp.setText(giftInfo.expValue + "经验值");
                giftName.setText(giftInfo.name);
                if (giftInfo == selectGiftInfo) {   //当前礼物已经被选中
                    giftSelectIcon.setImageResource(R.drawable.gift_selected);
                } else {                //当前礼物未被选中，并显示礼物类别
                    if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                        giftSelectIcon.setImageResource(R.drawable.gift_repeat);
                    } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                        giftSelectIcon.setImageResource(R.drawable.gift_none);
                    }
                }
            } else {        //是空礼物
                giftExp.setText("");
                giftName.setText("");
                giftSelectIcon.setImageResource(R.drawable.gift_none);
            }
            //给当前整个礼物Item的view设置点击监听事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (giftInfo == GiftInfo.Gift_Empty){
                        return;
                    }
                    if (giftInfo == selectGiftInfo) {
                        //说明已经是第2次选择该项礼物了，因此这时候传递null回去告诉pager去隐藏掉发送按钮
                        if (giftItemClickListener != null) {
                            giftItemClickListener.onClick(null);
                        }
                    } else {
                        //说明这是第1次选择该项礼物，因此这时候传递这个被选中的礼物回去告诉pager去显示发送按钮
                        if (giftItemClickListener != null) {
                            giftItemClickListener.onClick(giftInfo);       //触发回调事件
                        }
                    }
                }
            });
        }
    }
}
