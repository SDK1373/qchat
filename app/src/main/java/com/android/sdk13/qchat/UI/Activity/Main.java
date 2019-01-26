package com.android.sdk13.qchat.UI.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sdk13.qchat.R;
import com.android.sdk13.qchat.UI.Fragment.Chat;
import com.android.sdk13.qchat.UI.Fragment.ChatList;
import com.android.sdk13.qchat.UI.Fragment.Contact;

public class Main extends FragmentActivity implements View.OnClickListener {

    ImageView iv_main_head;
    TextView tv_username;
    String image;
//    final public static String PATH = "http://192.168.43.10:8080/Server_Web/";
    SharedPreferences sp;
    FragmentManager manager;
    FragmentTransaction transaction;
    ChatList list;
    Chat chat;
    Contact contact;
    RadioButton rbt_main_list;
    RadioButton rbt_main_contact;
    RadioButton rbt_main_chat;
    RadioGroup rg_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = getSharedPreferences("test",MODE_PRIVATE);
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //初始化控件
        iv_main_head = findViewById( R.id.iv_main_head );
        iv_main_head.setImageBitmap( Login.bitmap );
        tv_username = findViewById( R.id.tv_username );
        tv_username.setText( sp.getString( "UserName",null ) );
        rbt_main_chat = findViewById( R.id.rbt_main_chat );
        rbt_main_contact = findViewById( R.id.rbt_main_contact );
        rbt_main_list = findViewById( R.id.rbt_main_list );
        rg_main = findViewById( R.id.rg_main );

        //添加按钮监听
        iv_main_head.setOnClickListener( this );
        rbt_main_list.setOnClickListener( this );
        rbt_main_contact.setOnClickListener( this );
        rbt_main_chat.setOnClickListener( this );
        rg_main.check( R.id.rbt_main_list );

        //fragment初始化
        list = new ChatList();
        chat = new Chat();
        contact = new Contact();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add( R.id.ll_main, chat );
        transaction.add( R.id.ll_main, contact);
        transaction.add( R.id.ll_main, list );

        transaction.hide( chat );
        transaction.hide( contact );
        transaction.commit();

    }

    @Override
    public void onClick(View v) {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        if(v==rbt_main_list){
            hideFragment(transaction);
            transaction.show( list );
            transaction.commit();
        }
        if(v==rbt_main_chat){
            hideFragment(transaction);
            transaction.show(chat);
            transaction.commit();
        }
        if(v==rbt_main_contact){
            hideFragment(transaction);
            transaction.show(contact);
            transaction.commit();
        }
        if(v==iv_main_head){
            startActivity( new Intent( this,CurrentUserInfo.class ) );
        }
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (list != null) {
            transaction.hide( list );
        }
        if (chat != null) {
            transaction.hide(chat);
        }
        if (contact != null) {
            transaction.hide( contact );
        }
}

    private boolean exit = false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            if(!exit){
                exit = true;
                Toast.makeText( this,"你确定要退出吗？",Toast.LENGTH_SHORT).show();
                return true;
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("isLogin",0);
            editor.commit();
        }
        return super.onKeyUp( keyCode, event );
    }


}
