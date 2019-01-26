package com.android.sdk13.qchat.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.sdk13.qchat.Data.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoDAO {
    DBHelper helper;
    List<UserInfo> userInfoList;
    HashMap<String,String> map;
    public UserInfoDAO(Context context){
        helper = new DBHelper( context );
        map = new HashMap<String, String>();
        userInfoList = new ArrayList<UserInfo>();
    }

    public void add(UserInfo info){
        SQLiteDatabase database = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put( "username",info.getUsername() );
        values.put( "password",info.getPassword() );
        values.put( "isLogin",info.getIsLogin() );
        database.insert( "userInfo",null,values );
        database.close();
    }

    public void delete(int id){
        SQLiteDatabase database = helper.getReadableDatabase();
        database.delete( "userInfo","_id="+id,null );
        database.close();
    }

    public void update(UserInfo info){
        SQLiteDatabase database = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put( "username",info.getUsername() );
        values.put( "password",info.getPassword() );
        values.put( "isLogin",info.getIsLogin() );
        int id = info.getId();
        database.update( "userInfo",values,"_id="+id,null );
        database.close();
    }

    //输出包含UserInfo的List
    public List<UserInfo> getUserInfoList() {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query( "userInfo",null,null,null,null,null,null );
        while(cursor.moveToNext()){
            UserInfo innerInfo = new UserInfo();
            innerInfo.setId( cursor.getInt(0));
            innerInfo.setUsername( cursor.getString(1));
            innerInfo.setPassword( cursor.getString(2));
            innerInfo.setIsLogin( cursor.getInt( 3 ) );
            userInfoList.add( innerInfo );
        }
        return userInfoList;
    }

    //建立用户名和密码的HashMap
    public HashMap<String,String> getHashMap(){
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query( "userInfo",null,null,null,null,null,null );
        while(cursor.moveToNext()){
            map.put( cursor.getString( 1 ),cursor.getString( 2 ));
        }
        return map;
    }

}
