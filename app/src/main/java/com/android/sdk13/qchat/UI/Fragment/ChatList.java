package com.android.sdk13.qchat.UI.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sdk13.qchat.Data.Friend;
import com.android.sdk13.qchat.R;
import com.android.sdk13.qchat.UI.Adapter.ContactAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class ChatList extends Fragment {
    final public static String PATH = "http://192.168.43.10:8080/Server_Web/";
    SwipeMenuRecyclerView rv_contacts;
    String jsonString;
    List<Friend> list;
    ContactAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.fragment_chat_list,container,false);
        rv_contacts = v.findViewById( R.id.rv_chatlist );
        getJson(getActivity());
//        Log.i("TAG","2");
        return v;
    }

    @SuppressLint("StaticFieldLeak")
    public void getJson(final Context context){
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL( PATH+"FriendInfoListServlet" );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout( 2000 );
                    connection.setReadTimeout( 2000 );
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while((len=is.read( bytes ))!=-1){
                        baos.write( bytes,0,len );
                    }
                    jsonString = baos.toString();
                    baos.close();
                    is.close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                list = new Gson().fromJson( jsonString, new TypeToken<List<Friend>>() {}.getType() );
//                Log.i( "TAG", list.toString() );
                adapter = new ContactAdapter( context, list );
                rv_contacts.setLayoutManager( new LinearLayoutManager( context ) );
                rv_contacts.addItemDecoration( new DividerItemDecoration( context, DividerItemDecoration.VERTICAL ) );
                rv_contacts.setItemViewSwipeEnabled( true );
                rv_contacts.setLongPressDragEnabled( true );
//                Log.i( "TAG", "我的适配器呢？？" );

                rv_contacts.setOnItemMoveListener(new OnItemMoveListener(){

                    @Override
                    public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                        int fromPosition = srcHolder.getAdapterPosition();
                        int toPosition = targetHolder.getAdapterPosition();
                        Collections.swap(list, fromPosition, toPosition);
                        adapter.notifyItemMoved(fromPosition, toPosition);
                        // 返回true，表示数据交换成功，ItemView可以交换位置。
                        return true;
                        }

                        @Override
                        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                            int position = srcHolder.getAdapterPosition();
                            list.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                });
                rv_contacts.setAdapter( adapter );
            }
        }.execute();
    }
}

