package edu.sysu.showtime.editProfile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

import edu.sysu.showtime.APP;
import edu.sysu.showtime.MainActivity;
import edu.sysu.showtime.R;
import edu.sysu.showtime.customview.ProdfileEditableView;
import edu.sysu.showtime.customview.ProfileUnEditableView;
import edu.sysu.showtime.utils.ImgUtils;
import edu.sysu.showtime.utils.PicChooseUtils;

public class EditProfileFragment extends Fragment {
    private FragmentActivity activity = null;
    private Toolbar titlebar;
    //头像栏
    private View headLinearLayoutView;
    private ImageView headpic_Img;
    //可编辑信息栏
    private ProdfileEditableView nickNameEdt;
    private ProdfileEditableView genderEdt;
    private ProdfileEditableView signEdt;
    private ProdfileEditableView renzhengEdt;
    private ProdfileEditableView locationEdt;
    //不可编辑信息栏
    private ProfileUnEditableView idView;
    private ProfileUnEditableView levelView;
    private ProfileUnEditableView getNumsView;
    private ProfileUnEditableView sendNumsView;

    private Button completeBtn;

    private EditProfileDialog nickNameDialog = null;
    private EditGenderDialog genderDialog = null;
    private EditProfileDialog signDialog = null;
    private EditProfileDialog renzhengDialog = null;
    private EditProfileDialog locationDialog = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();   //获取该fragment的FragmentActivity

        View profileView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initViews(profileView);
        setTitleBar();
        setListeners();
        setIconKey();//初始化个人信息每一栏的图标、图标名和内容
        drawUserProfile();   //初始化个人信息页的各项数据
        return profileView;
    }
    private void initViews(View view) {
        titlebar = view.findViewById(R.id.titlebar);

        headLinearLayoutView = view.findViewById(R.id.headview);
        headpic_Img = view.findViewById(R.id.headpic_Img);

        nickNameEdt = view.findViewById(R.id.nick_name);
        genderEdt = view.findViewById(R.id.gender);
        signEdt = view.findViewById(R.id.sign);
        renzhengEdt = view.findViewById(R.id.renzheng);
        locationEdt = view.findViewById(R.id.location);
        idView = view.findViewById(R.id.id);
        levelView = view.findViewById(R.id.level);
        getNumsView = view.findViewById(R.id.get_nums);
        sendNumsView = view.findViewById(R.id.send_nums);
        completeBtn = view.findViewById(R.id.complete);
    }
    private void setTitleBar() {
        titlebar.setTitle("编辑个人信息");
        titlebar.setTitleTextColor(Color.WHITE);
        if(activity instanceof AppCompatActivity){
            ((AppCompatActivity)activity).setSupportActionBar(titlebar);
        }
    }
    private void setListeners() {
        headLinearLayoutView.setOnClickListener(clickListener);

        nickNameEdt.setOnClickListener(clickListener);
        genderEdt.setOnClickListener(clickListener);
        signEdt.setOnClickListener(clickListener);
        renzhengEdt.setOnClickListener(clickListener);
        locationEdt.setOnClickListener(clickListener);
        completeBtn.setOnClickListener(clickListener);
    }
    private void setIconKey() {
        nickNameEdt.set(R.drawable.ic_info_nickname, "昵称", "");
        genderEdt.set(R.drawable.ic_info_gender, "性别", "");
        signEdt.set(R.drawable.ic_info_sign, "签名", "无");
        renzhengEdt.set(R.drawable.ic_info_renzhen, "认证", "未知");
        locationEdt.set(R.drawable.ic_info_location, "地区", "中国大陆");
        idView.set(R.drawable.ic_info_id, "ID", "");
        levelView.set(R.drawable.ic_info_level, "等级", "0");
        getNumsView.set(R.drawable.ic_info_get, "获得票数", "0");
        sendNumsView.set(R.drawable.ic_info_send, "送出票数", "0");
    }

    private void drawUserProfile() {

        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(), "获取信息失败：" + s, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //从腾讯TIM获取个人信息成功，并将获取到的信息更新到本地，并展示到个人信息页上
                APP.getApp().setSelfProfile(timUserProfile);
                displayProfile(timUserProfile);
            }
        });
    }
    //将用户个人信息显示到个人信息页上
    private void displayProfile(TIMUserProfile userProfile){
        String faceUrl = userProfile.getFaceUrl();  //获取到头像的Url
        if (TextUtils.isEmpty(faceUrl)) {       //初始设置一张默认头像
            ImgUtils.loadRound(R.drawable.default_head_pic, headpic_Img);
        } else {
            ImgUtils.loadRound(faceUrl, headpic_Img);
            Log.i("test", "faceUrl = " + faceUrl);
        }
        //获取腾讯TIM的基础字段
        nickNameEdt.updateValue(userProfile.getNickName());
        long genderValue = userProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        genderEdt.updateValue(genderStr);
        signEdt.updateValue(userProfile.getSelfSignature());
        //locationEdt.updateValue(userProfile.getLocation());
        idView.updateValue(userProfile.getIdentifier());
        //获取自定义的字段
        Map<String, byte[]> customInfo = userProfile.getCustomInfo();
        renzhengEdt.updateValue(getValue(customInfo, CustomProfileField.CUSTOM_RENZHENG, "未知"));
        levelView.updateValue(getValue(customInfo, CustomProfileField.CUSTOM_LEVEL, "0"));
        getNumsView.updateValue(getValue(customInfo, CustomProfileField.CUSTOM_GET, "0"));
        sendNumsView.updateValue(getValue(customInfo, CustomProfileField.CUSTOM_SEND, "0"));
    }
    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueBytes = customInfo.get(key);      //获取到返回值中对应的字节数组
            if (valueBytes != null) {
                return new String(valueBytes);
            }
            else{
                Log.i("test", "bytes = null");
            }
        } else{
            Log.i("test", "customInfo = null");
        }
        return defaultValue;
    }

    //点击响应事件处理
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.headview) {
                choosePic();                    //打开相册，修改头像图片
            } else if (id == R.id.nick_name) {
                showEditNickNameDialog();       //修改昵称
            } else if (id == R.id.gender) {
                showEditGenderDialog();          //修改性别
            } else if (id == R.id.sign) {
                showEditSignDialog();           //修改个性签名
            } else if (id == R.id.renzheng) {
                showEditRenzhengDialog();       //修改认证信息
            } else if (id == R.id.location) {
                showEditLocationDialog();       //修改地区信息
            } else if (id == R.id.complete) {
                //完成，点击跳转到主界面
                Intent intent = new Intent();
                intent.setClass(getContext(), MainActivity.class);
                startActivity(intent);
                //如果是activity的话，则需要finish掉，如果是fragment则不用finish掉
                if(activity != null){
                    activity.finish();
                }
            }
        }
    };

    private PicChooseUtils picChooseUtils = null;
    //选择头像图片
    private void choosePic() {
        if (picChooseUtils == null) {
            picChooseUtils = new PicChooseUtils(EditProfileFragment.this, "headPic");
            //设置上传照片成功监听器，如果成功则回调onSuccess方法
            picChooseUtils.setOnChooseResultListener(new PicChooseUtils.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) {
                    //图片选择成功并且上传到七牛云成功，更新头像信息到TIM
                    TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(activity, "更新头像失败：" + s, Toast.LENGTH_SHORT).show();
                            Log.i("test", "更新头像失败");
                        }
                        @Override
                        public void onSuccess() {   //更新头像到界面上
                            drawUserProfile();
                            Log.i("test", "更新头像成功");
                        }
                    });
                }
                @Override
                public void onFail(String msg) {
                    //图片选择失败或者上传失败，不更新头像
                    Toast.makeText(activity, "更新头像失败：" + msg, Toast.LENGTH_SHORT).show();
                    Log.i("test", "更新头像失败");
                }
            });
        }
        picChooseUtils.showPicChooseDialog();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //在打开相册（调用Intent）获取到数据后，返回的数据会跳到这里，因为picChooseUtils不是Activity，
        //所以picChooseUtils中收不到返回的图片数据，但是这里EditProfileActivity因为是Activity，
        //因此可以根据requestCode收到返回的图片数据，但是这里获取到的数据是要传给picChooseUtils
        //使用的，因此这里判断是否要给picChooseUtils使用，只需看它是否为null即可。
        //如果是要给picChooseUtils用的话，则调用它的getResult来获取返回的数据
        if(picChooseUtils != null){
            Log.i("test", "进入了子类的onActivityResult方法");
            picChooseUtils.getReturnResult(requestCode, resultCode, data);
        }else{
            Log.i("test", "进入了父类的onActivityResult方法");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //修改昵称
    private void showEditNickNameDialog() {
        if(activity == null){
            Log.i("test", "activity等于null");
        } else {
            Log.i("test", "activity不等于null");
        }
        if(nickNameDialog == null)
            nickNameDialog = new EditProfileDialog(activity);
        nickNameDialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setNickName(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(activity, "更新昵称失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess() {
                        drawUserProfile();
                    }
                });
            }
        });
        nickNameDialog.showEditDialog("昵称", R.drawable.ic_info_nickname, nickNameEdt.getValue());
    }

    //修改性别
    private void showEditGenderDialog() {
        if(genderDialog == null)
            genderDialog = new EditGenderDialog(activity);
        genderDialog.setOnOKListener(new EditGenderDialog.OnOKListener() {
            @Override
            public void onOk(boolean isMale) {
                TIMFriendGenderType gender = isMale ? TIMFriendGenderType.Male : TIMFriendGenderType.Female;
                TIMFriendshipManager.getInstance().setGender(gender, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(activity, "更新性别失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess() {
                        drawUserProfile();
                    }
                });
            }
        });
        genderDialog.showChangeGenderDialog(genderEdt.getValue().equals("男"));
    }

    //修改个性签名，会弹出一个自定义的对话框
    private void showEditSignDialog() {
        if(signDialog == null)
            signDialog = new EditProfileDialog(activity);
        //监听自定义对话框中的确定按钮
        signDialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                //当点击了自定义对话框中的确定按钮后，将修改后的数据发送回给腾讯TIM服务器做保存，并刷新当前界面
                TIMFriendshipManager.getInstance().setSelfSignature(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(activity, "更新签名失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess() {
                        drawUserProfile();  //将修改后的用户信息重新显示在个人信息页上
                    }
                });
            }
        });
        signDialog.showEditDialog("签名", R.drawable.ic_info_sign, signEdt.getValue());
    }

    //修改认证信息
    private void showEditRenzhengDialog() {
        if(renzhengDialog == null)
            renzhengDialog = new EditProfileDialog(activity);
        renzhengDialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfileField.CUSTOM_RENZHENG, content.getBytes(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(activity, "更新认证失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess() {
                        drawUserProfile();
                    }
                });
            }
        });
        renzhengDialog.showEditDialog("认证", R.drawable.ic_info_renzhen, renzhengEdt.getValue());
    }

    //修改地区信息
    private void showEditLocationDialog() {
        if(locationDialog == null)
            locationDialog = new EditProfileDialog(activity);
        locationDialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setLocation(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(activity, "更新地区失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess() {
                        locationEdt.updateValue(content);
                        drawUserProfile();
                    }
                });
            }
        });
        locationDialog.showEditDialog("地区", R.drawable.ic_info_location, locationEdt.getValue());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("test", "onDestroy()");

        if(nickNameDialog != null){
            nickNameDialog.dismiss();
        }
        if(genderDialog != null){
            genderDialog.dismiss();
        }
        if(signDialog != null){
            signDialog.dismiss();
        }
        if(renzhengDialog != null){
            renzhengDialog.dismiss();
        }
        if(locationDialog != null){
            locationDialog.dismiss();
        }
        if(picChooseUtils != null){
            picChooseUtils.dismiss();
        }
    }
}

