package com.android.sdk13.qchat.UI.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.sdk13.qchat.Utils.DBHelper;
import com.android.sdk13.qchat.R;
import com.android.sdk13.qchat.Data.UserInfo;
import com.android.sdk13.qchat.Utils.UserInfoDAO;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * 主登录界面
 * 用数据库存储用户信息,登录状态
 */

public class Login extends AppCompatActivity {
    private ImageView iv_login_head;
    private EditText et_login_username;
    private EditText et_login_password;
    private DBHelper helper;
    private UserInfoDAO dao;
    private List<UserInfo> userInfoList;
    private SQLiteDatabase database;
    String username,password;
    HashMap<String,String> map;
    final public static String PATH = "http://192.168.43.10:8080/Server_Web/";
    public static Bitmap bitmap;
    SharedPreferences sp;
    String imagePath;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login );

        //获取View
        image = getFilesDir().getAbsolutePath() + "/default.jpg";
        iv_login_head = findViewById(R.id.iv_login_head);
        et_login_username = findViewById(R.id.et_login_username);
        et_login_password = findViewById(R.id.et_login_password);

        //初始化成员变量
        sp = getSharedPreferences("test",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        imagePath = sp.getString( "head",image );
        int isLogin = sp.getInt( "isLogin",0 );
        helper = new DBHelper(this);
        dao = new UserInfoDAO( this );
        userInfoList = dao.getUserInfoList();

        //判断路径与登录状态
        if(imagePath!=null){
            bitmap = BitmapFactory.decodeFile( imagePath );
            iv_login_head.setImageBitmap( bitmap );
        }
        else{
            bitmap = BitmapFactory.decodeFile( image );
            iv_login_head.setImageBitmap( bitmap );
        }

        String name = sp.getString( "UserName",null );
        et_login_username.setHint( name );

        if(isLogin==1){
           startActivity( new Intent( Login.this,Main.class ) );
           finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void getHead(String Username){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL( PATH+"image/"+ et_login_username.getText().toString() + ".jpg" );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout( 2000 );
                    connection.setConnectTimeout( 2000 );
                    connection.connect();
                    //开始连接
                    if(connection.getResponseCode()==200){
                        InputStream is = connection.getInputStream();
                        FileOutputStream fos = openFileOutput(  et_login_username.getText().toString()+".jpg",MODE_PRIVATE );
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while((len=is.read( bytes ))!=-1){
                            fos.write(bytes,0,len);
                        }
                        String image = getFilesDir().getAbsolutePath() + et_login_username.getText().toString()+".jpg";
                        bitmap = BitmapFactory.decodeFile( image );
                        is.close();
                        connection.disconnect();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                iv_login_head.setImageBitmap( bitmap );
            }
        }.execute();
    }

    public void Register(View v){
        final View view = View.inflate( this,R.layout.other_dialog,null );
        final EditText et_dl_username = (EditText)view.findViewById( R.id.et_dl_username );
        final EditText et_dl_password = (EditText)view.findViewById( R.id.et_dl_password );
        et_dl_username.setHint( "用户名" );
        et_dl_password.setHint( "密码" );
        new AlertDialog.Builder(this)
                .setView( view )
                .setTitle( "请输入用户名和密码" )
                .setPositiveButton( "确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username = et_dl_username.getText().toString().trim();
                        password = et_dl_password.getText().toString().trim();
                        if(username.equals("")||password.equals("")){
                            Toast.makeText( Login.this,"用户名密码不允许为空",Toast.LENGTH_SHORT ).show();
                        }
                        else{
                            UserInfo info = new UserInfo(username,password,0 );
                            dao.add( info );
                            Toast.makeText(Login.this,"注册成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                } )
                .setNegativeButton( "取消",null )
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    public void Login(View v) throws Exception {
        map = dao.getHashMap();
        username = et_login_username.getText().toString();
        password = et_login_password.getText().toString();
        if(username.equals("")||password.equals("")){
            Toast.makeText( Login.this,"用户名密码不允许为空",Toast.LENGTH_SHORT ).show();
        }
        else if(username.equals( userInfoList.get(0).getUsername() )&&password.equals(userInfoList.get(0).getPassword()))
            startActivity( new Intent( Login.this,Admin.class ));
        else if(!map.containsKey( username )){
            Toast.makeText( Login.this,"用户不存在",Toast.LENGTH_SHORT ).show();
        }
        else if(map.get(username).equals(password)){
            getHead( et_login_username.getText().toString() );
            new AsyncTask<Void,Void,String>(){
                ProgressDialog dialog = new ProgressDialog(Login.this);
                @Override
                protected void onPreExecute() {
                    dialog.setTitle( "正在登陆" );
                    dialog.show();
                    SystemClock.sleep( 500 );
                    super.onPreExecute();
                }

                protected String doInBackground(Void... voids) {
                    try {
                        URL url = new URL(PATH +"/index.jsp");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout( 2000 );
                        connection.setReadTimeout( 2000 );
                        SystemClock.sleep( 1000 );
                        Runtime runtime = Runtime.getRuntime();
                        connection.connect();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "SUCCESS";
                }

                @Override
                protected void onPostExecute(String s) {
                    if(s.equals("SUCCESS")){
                        dialog.dismiss();
                        Toast.makeText( Login.this,"登陆成功",Toast.LENGTH_SHORT ).show();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString( "head", getFilesDir().getAbsolutePath() + "/" + et_login_username.getText().toString()+".jpg");
                        editor.putInt( "isLogin",1 );
                        editor.putString( "UserName",et_login_username.getText().toString() );
                        editor.commit();
                        imagePath = sp.getString( "head",image );
                        bitmap = BitmapFactory.decodeFile( imagePath );
                        iv_login_head.setImageBitmap( bitmap );
                        startActivity( new Intent( Login.this,Main.class ) );
                        finish();
                    }
                    else{
                        dialog.dismiss();
                        Toast.makeText( Login.this,"网络连接失败",Toast.LENGTH_SHORT ).show();
                    }
                    super.onPostExecute( s );
                }
            }.execute();
        }
        else{
            Toast.makeText( Login.this,"账号或密码错误",Toast.LENGTH_SHORT ).show();
        }
    }

}
