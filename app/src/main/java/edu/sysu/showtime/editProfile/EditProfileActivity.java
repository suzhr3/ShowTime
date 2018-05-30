package edu.sysu.showtime.editProfile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.sysu.showtime.R;

//个人信息页
public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); //这里的布局中设置了fragment
    }
}
