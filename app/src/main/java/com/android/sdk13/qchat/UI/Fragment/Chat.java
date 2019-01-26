package com.android.sdk13.qchat.UI.Fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.sdk13.qchat.Data.Msg;
import com.android.sdk13.qchat.R;
import com.android.sdk13.qchat.UI.Adapter.MsgAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Chat extends Fragment {
    private List<Msg> msgList;
    private EditText et_char_text;
    private Button bt_chat_send;
    private RecyclerView rv_chat;
    private MsgAdapter adapter;
    private final String MESSAGEPATH = "http://192.168.43.10:8080/Server_Web/MessageServlet";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.fragment_chat,container,false);
        et_char_text = v.findViewById( R.id.et_char_text);
        bt_chat_send = v.findViewById( R.id.bt_chat_send);
        rv_chat = v.findViewById( R.id.rv_chat);
        msgList = new ArrayList<>();
        initMsgs();
        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private void initMsgs() {
        new AsyncTask<Void,Void,Void>(){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String json;
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL( MESSAGEPATH );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout( 2000 );
                    connection.setReadTimeout( 2000 );
                    connection.connect();
                    if(connection.getResponseCode()==200){
                        InputStream is = connection.getInputStream();
                        int len = -1;
                        byte[] b = new byte[1024];
                        while((len=is.read( b ))!=-1){
                            baos.write( b,0,len );
                        }
                        is.close();
                        connection.disconnect();
                        json = baos.toString();
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                msgList = new Gson().fromJson( json, new TypeToken<List<Msg>>(){}.getType() );
                adapter = new MsgAdapter(msgList);
                //rv_chat.addItemDecoration( new DividerItemDecoration( getActivity(), DividerItemDecoration.VERTICAL ) );
                rv_chat.setAdapter(adapter);
                rv_chat.setLayoutManager(new LinearLayoutManager( getActivity()) );
                bt_chat_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String content = et_char_text.getText().toString();
                        if (!"".equals(content)) {
                            Msg msg = new Msg(content, Msg.TYPE_SENT);
                            msgList.add(msg);
                            // 当有新消息时，刷新ListView中的显示
                            adapter.notifyItemInserted(msgList.size() - 1);
                            // 将ListView定位到最后一行
                            et_char_text.setText("");
                            rv_chat.scrollToPosition(msgList.size() - 1);
                            new AsyncTask<Void,Void,Void>(){
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                String json;
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    try {
                                        URL url = new URL(MESSAGEPATH + "?text=" + content );
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setRequestMethod( "POST" );
                                        connection.setConnectTimeout( 2000 );
                                        connection.setReadTimeout( 2000 );
                                        connection.connect();
                                        InputStream is = connection.getInputStream();
                                        byte[] b = new byte[1024];
                                        int len = -1;
                                        while((len=is.read( b ))!=-1){
                                            baos.write( b,0,len );
                                        }
                                        is.close();
                                        json = baos.toString();
                                        baos.close();
                                        connection.disconnect();
                                        SystemClock.sleep( 500 );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    Msg msg2 = new Gson().fromJson( json,Msg.class );
                                    msgList.add( msg2 );
                                    adapter.notifyItemInserted(msgList.size() - 1);
                                    // 将ListView定位到最后一行
                                    rv_chat.scrollToPosition(msgList.size() - 1);
                                }
                            }.execute();
                        }
                    }
                });
            }
        }.execute();
    }

}
