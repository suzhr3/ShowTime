package edu.sysu.showtime.loginRegister;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import edu.sysu.showtime.R;
import edu.sysu.showtime.editProfile.EditProfileActivity;

public class RegisterActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar titleBar;
    private EditText accountEdt;
    private EditText passwordEdt;
    private EditText confirmPasswordEdt;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setListeners();
        setTitleBar();
    }
    private void initViews() {
        titleBar = findViewById(R.id.titlebar);
        accountEdt = findViewById(R.id.reg_account);
        passwordEdt = findViewById(R.id.reg_password);
        confirmPasswordEdt = findViewById(R.id.reg_confirm_password);
        registerBtn = findViewById(R.id.reg_register);
    }
    private void setListeners() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void setTitleBar() {
        titleBar.setTitle("注册新用户");
        titleBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(titleBar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void register() {
        final String userName = accountEdt.getText().toString();
        final String password = passwordEdt.getText().toString();
        String confirmPsw = confirmPasswordEdt.getText().toString();
        boolean is_illegal = check(userName, password, confirmPsw);
        if(is_illegal) return;

        ILiveLoginManager.getInstance().tlsRegister(userName, password, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //注册成功并登录
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                });
                login(userName, password);
            }
            @Override
            public void onError(String module, int errCode, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "注册失败：" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void login(final String userName, final String passWord) {
        //调用腾讯IM登录
        ILiveLoginManager.getInstance().tlsLogin(userName, passWord, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                loginILive(userName, passWord);     //IMSDK登录成功，接着尝试登录iLvieSDK
            }
            @Override
            public void onError(String module, int errCode, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "TLS登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loginILive(String userName, String data) {
        ILiveLoginManager.getInstance().iLiveLogin(userName, data, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //最终登录成功，直接跳转到修改用户信息界面。
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,EditProfileActivity.class);
                startActivity(intent);
                //getSelfInfo();
                finish();
            }
            @Override
            public void onError(String module, int errCode, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean check(String userName, String password, String confirmPsw) {
        if (TextUtils.isEmpty(userName) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPsw)) {
            Toast.makeText(RegisterActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!password.equals(confirmPsw)) {
            Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(password.length() < 8){
            Toast.makeText(RegisterActivity.this, "密码长度不足8位", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
