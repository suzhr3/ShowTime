package edu.sysu.showtime.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.sysu.showtime.R;

//自定义个人信息页面可编辑的行控件，在EditProfileFragment的布局中使用
public class ProdfileEditableView extends LinearLayout{
    private TextView type;
    private TextView value;
    private ImageView arrow;

    //三种构造函数
    public ProdfileEditableView(Context context) {
        super(context);
        init();
    }
    public ProdfileEditableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ProdfileEditableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //将单个view和该自定义LinearLayout类绑定在一起
        LayoutInflater.from(getContext()).inflate(R.layout.view_single_edit_unit_layout, this, true);
        initViews();
    }

    private void initViews() {
        type = findViewById(R.id.type);
        value = findViewById(R.id.value);
        arrow = findViewById(R.id.arrow);
    }

    //设置个人信息中每一栏的图标、图标名、内容
    public void set(int iconId, String name, String content){
        type.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
        type.setText(name);
        value.setText(content);
    }

    public void updateValue(String content){
        value.setText(content);
    }

    public String getValue(){
        return value.getText().toString();
    }

    //对于不能编辑的内容，隐藏掉版编辑箭头
    public void disableEdit(){
        arrow.setVisibility(GONE);
    }
}
