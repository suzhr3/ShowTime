package edu.sysu.showtime.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

//这是个人信息页中的不能编辑，只能显示的行控件，其中只需隐藏掉编辑箭头即可，在EditProfileFragment的布局中使用
public class ProfileUnEditableView extends ProdfileEditableView {
    public ProfileUnEditableView(Context context) {
        super(context);
        disableEdit();
    }
    public ProfileUnEditableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        disableEdit();
    }
    public ProfileUnEditableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        disableEdit();
    }
}
