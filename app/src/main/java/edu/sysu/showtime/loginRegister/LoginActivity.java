package edu.sysu.showtime.loginRegister;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import edu.sysu.showtime.APP;
import edu.sysu.showtime.MainActivity;
import edu.sysu.showtime.R;
import edu.sysu.showtime.utils.ApplyForPermission;

public class LoginActivity extends AppCompatActivity {
    private EditText accountEdt;
    private EditText passwordEdt;
    private Button loginBtn;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setListeners();
        getPermissions();
    }
    private void initViews() {
        accountEdt = findViewById(R.id.account);
        passwordEdt = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);
    }
    private void setListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void getPermissions() {
        String[] permissionLists = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ApplyForPermission.applyPermission(LoginActivity.this, permissionLists, 1);
    }

    private void register() {
        //注册新用户，跳转到注册页面。
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        String userName = accountEdt.getText().toString();
        String passWord = passwordEdt.getText().toString();
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)){
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        doLogin(userName, passWord);
    }

    private void doLogin(final String userName, final String passWord) {
        //调用腾讯IM登录
        ILiveLoginManager.getInstance().tlsLogin(userName, passWord, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                loginILive(userName, passWord);      //IM登陆成功，接着尝试登录ILiveSDK
            }
            @Override
            public void onError(String module, int errCode, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "TLS登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void loginILive(String userName, String passWord) {
        // 使用托管方式或独立模式，在获取到用户的sig后，使用ILive
        // 登录接口，告知后台音视频模块上线了（包括avsdk）
        ILiveLoginManager.getInstance().iLiveLogin(userName, passWord, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //最终登录成功，跳转到主界面
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                getSelfInfo();  //每次登录成功都要重新加载用户个人信息到本地
                finish();
            }
            @Override
            public void onError(String module, int errCode, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功，保存到本地
                APP.getApp().setSelfProfile(timUserProfile);
            }
        });
    }
}
