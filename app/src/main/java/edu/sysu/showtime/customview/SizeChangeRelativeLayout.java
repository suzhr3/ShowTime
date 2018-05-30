package edu.sysu.showtime.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

//可以监听屏幕显示尺寸大小变化的RelativeLayout，和普通的RelativeLayout没什么区别，就是多了个能够监听屏幕显示尺寸改变的功能
public class SizeChangeRelativeLayout extends RelativeLayout {
    public SizeChangeRelativeLayout(Context context) {
        super(context);
    }
    public SizeChangeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SizeChangeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(h > oldh){
            //画面变长，说明隐藏了软键盘，回调接口
            sizeChangeListener.onHideKeyBoard();
        } else if(h < oldh) {
            //画面变短，说明弹出了软键盘，回调接口
            sizeChangeListener.onShowKeyBoard();
        }
    }

    public interface OnSizeChangeListener {
        public void onHideKeyBoard();
        public void onShowKeyBoard();
    }
    private OnSizeChangeListener sizeChangeListener;

    public void setOnSizeChangeListener(OnSizeChangeListener l) {
        sizeChangeListener = l;
    }
}
