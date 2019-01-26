package com.android.sdk13.qchat.UI.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sdk13.qchat.Data.Friend;
import com.android.sdk13.qchat.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ContactAdapter extends SwipeMenuRecyclerView.Adapter<ContactAdapter.VH>{

    Context context;
    public class VH extends SwipeMenuRecyclerView.ViewHolder{

        public final ImageView iv_item_head;
        public final TextView tv_item_name;
        public VH(View v) {
            super(v);
            tv_item_name = (TextView) v.findViewById(R.id.tv_item_name);
            iv_item_head = (ImageView)v.findViewById( R.id.iv_item_head );
        }
    }

    List<Friend> mdata;
    public ContactAdapter(Context context, List<Friend> data){
        this.context = context;
        mdata = data;
    }

    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, viewGroup, false);
        return new VH(v);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull final VH vh, final int i) {
        final String path = mdata.get(i).getImagePath();
        final String name = mdata.get(i).getName();
        new AsyncTask<Void,Void,Void>(){
            Bitmap bitmap;
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL( path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout( 2000 );
                    connection.setConnectTimeout( 2000 );
                    connection.connect();
                    //开始连接
                    if(connection.getResponseCode()==200){
                        InputStream is = connection.getInputStream();
                        FileInputStream fis = context.openFileInput( name+".jpg" );
                        int len = -1;
                        if((len=fis.read())==-1){
                            FileOutputStream fos = context.openFileOutput(name+".jpg",MODE_PRIVATE);
                            byte[] bytes = new byte[1024];
                            while((len=is.read( bytes ))!=-1){
                                fos.write(bytes,0,len);
                            }
                            is.close();
                            connection.disconnect();
                            fos.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                String image = context.getFilesDir().getAbsolutePath() + "/" + name+ ".jpg";
                Bitmap bitmap = BitmapFactory.decodeFile( image );
                vh.iv_item_head.setImageBitmap( bitmap );
                vh.tv_item_name.setText( name );
                Log.i("TAG",i+"已创建");
            }
        }.execute();
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }
}
