package com.android.sdk13.qchat.UI.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.sdk13.qchat.R;

public class CurrentUserInfo extends AppCompatActivity {

    ImageView iv_userinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_current_userinfo );
        iv_userinfo = findViewById( R.id.iv_userinfo );
        iv_userinfo.setImageBitmap( Login.bitmap );
    }
}
