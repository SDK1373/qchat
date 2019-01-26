package com.android.sdk13.qchat.UI.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sdk13.qchat.R;
import com.android.sdk13.qchat.Data.UserInfo;
import com.android.sdk13.qchat.Utils.UserInfoDAO;

import java.util.List;

public class Admin extends AppCompatActivity {

    private ListView lv_admin;
    private UserInfoDAO dao;
    private List<UserInfo> userInfoList;
    private int position;
    UserInfo info;
    EditText et_dl_username;
    EditText et_dl_password;
    AdminAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin );
        dao = new UserInfoDAO( this );
        userInfoList = dao.getUserInfoList();
        lv_admin = findViewById( R.id.lv_admin );
        adapter = new AdminAdapter();
        lv_admin.setAdapter( adapter);
        lv_admin.setOnCreateContextMenuListener( this );
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add( 0,1,0,"更新" );
        menu.add( 0,2,0,"删除" );
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            final View view = View.inflate( this,R.layout.other_dialog,null );
            et_dl_username = (EditText)view.findViewById( R.id.et_dl_username );
            et_dl_password = (EditText)view.findViewById( R.id.et_dl_password );
            et_dl_username.setHint( userInfoList.get( position ).getUsername() );
            et_dl_password.setHint( userInfoList.get( position ).getPassword() );
            new AlertDialog.Builder(this)
                    .setTitle( "请输入更新后的用户名和密码" )
                    .setView( view )
                    .setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String username = et_dl_username.getText().toString();
                            String password = et_dl_password.getText().toString();
                            if(username==""){
                                dialog.dismiss();
                                Toast.makeText( Admin.this,"用户名不允许为空",Toast.LENGTH_SHORT );
                            }
                            if(password==""){
                                dialog.dismiss();
                                Toast.makeText( Admin.this,"密码不允许为空",Toast.LENGTH_SHORT );
                            }
                            UserInfo userInfo = new UserInfo( userInfoList.get( position ).getId(),username,password,0);
                            userInfoList.set( position,userInfo );
                            dao.update(userInfo);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(Admin.this,"更新成功",Toast.LENGTH_SHORT).show();
                        }
                    } )
                    .setNegativeButton( "取消",null )
                    .show();
        }
        else if(item.getItemId()==2){
            new AlertDialog.Builder(this)
                    .setTitle( "你确定要删除该条记录吗?" )
                    .setMessage( "此操作无法恢复" )
                    .setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            dao.delete( userInfoList.get( position ).getId() );
                            userInfoList.remove( position );
                            dialog.dismiss();
                            final ProgressDialog dialog1 = new ProgressDialog( Admin.this );
                            new Thread( new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog1.setTitle( "正在删除" );
                                            dialog1.show();
                                        }
                                    } );
                                    SystemClock.sleep( 1000 );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Admin.this,"删除成功",Toast.LENGTH_SHORT).show();
                                            dialog1.dismiss();
                                            adapter.notifyDataSetChanged();
                                        }
                                    } );
                                }
                            } ).start();
                        }
                    } )
                    .setNegativeButton( "取消",null )
                    .show();

        }

        return super.onContextItemSelected( item );
    }

    class AdminAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return userInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfoList.get( position );
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = View.inflate( Admin.this,R.layout.item_admin_info,null);
            }
            TextView tv_item_username = convertView.findViewById( R.id.tv_item_username );
            TextView tv_item_password = convertView.findViewById( R.id.tv_item_password );
            tv_item_username.setText( userInfoList.get( position).getUsername());
            tv_item_password.setText( userInfoList.get( position).getPassword());
            return convertView;
        }
    }
}
