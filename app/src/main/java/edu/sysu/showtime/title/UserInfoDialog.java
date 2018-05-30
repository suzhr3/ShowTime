package edu.sysu.showtime.title;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.TIMUserProfile;

import edu.sysu.showtime.R;
import edu.sysu.showtime.utils.ImgUtils;
import edu.sysu.showtime.widget.TransParentDialog;

public class UserInfoDialog extends TransParentDialog {
    private TIMUserProfile userInfo;

    private ImageView user_close;
    private ImageView user_headPic;
    private TextView user_name;
    private ImageView user_gender;
    private TextView user_level;
    private TextView user_id;
    private TextView user_renzhen;
    private TextView user_sign;
    private TextView user_songchu;
    private TextView user_bopiao;

    public UserInfoDialog(Activity activity, TIMUserProfile userInfo) {
        super(activity);
        this.userInfo = userInfo;

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_userinfo, null);
        setContentViewToCustomDialog(view);
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100, WindowManager.LayoutParams.WRAP_CONTENT);

        initViews(view);
        bindDataToViews();
    }
    private void initViews(View view) {
        user_close = view.findViewById(R.id.user_close);
        user_headPic = view.findViewById(R.id.user_headPic);
        user_name = view.findViewById(R.id.user_name);
        user_gender = view.findViewById(R.id.user_gender);
        user_level = view.findViewById(R.id.user_level);
        user_id = view.findViewById(R.id.user_id);
        user_renzhen = view.findViewById(R.id.user_renzhen);
        user_sign = view.findViewById(R.id.user_sign);
        user_songchu = view.findViewById(R.id.user_songchu);
        user_bopiao = view.findViewById(R.id.user_bopiao);

        user_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void bindDataToViews() {
        String faceUrl = userInfo.getFaceUrl();
        if (TextUtils.isEmpty(faceUrl)) {
            ImgUtils.loadRound(R.drawable.default_head_pic, user_headPic);
        } else {
            ImgUtils.loadRound(faceUrl, user_headPic);
        }

        String nickName = userInfo.getNickName();
        if(TextUtils.isEmpty(nickName)){
            nickName = userInfo.getIdentifier();
        }
        user_name.setText(nickName);

        long genderValue = userInfo.getGender().getValue();
        user_gender.setImageResource(genderValue == 1 ? R.drawable.ic_male : R.drawable.ic_female);

        user_id.setText("ID：" + userInfo.getIdentifier());
        String sign = userInfo.getSelfSignature();
        user_sign.setText(TextUtils.isEmpty(sign) ? "Ta好像忘记写签名了..." : sign);
    }
}
