package com.android.sdk13.qchat.UI.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sdk13.qchat.Data.Msg;
import com.android.sdk13.qchat.R;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_item_msg_left;
        LinearLayout ll_item_msg_right;
        TextView tv_item_msg_left;
        TextView tv_item_msg_right;

        // view表示父类的布局，用其获取子项
        public ViewHolder(View view) {
            super(view);
            ll_item_msg_left = (LinearLayout) view.findViewById(R.id.ll_item_msg_left);
            ll_item_msg_right = (LinearLayout) view.findViewById(R.id.ll_item_msg_right);
            tv_item_msg_left = (TextView) view.findViewById(R.id.tv_item_msg_left);
            tv_item_msg_right = (TextView) view.findViewById(R.id.tv_item_msg_right);
        }
    }

    public MsgAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    /**
     * 创建 ViewHolder 加载 RecycleView 子项的布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 为 RecycleView 子项赋值
     * 赋值通过 position 判断子项位置
     * 当子项进入界面时执行
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.ll_item_msg_left.setVisibility(View.VISIBLE);
            holder.ll_item_msg_right.setVisibility(View.GONE);
            holder.tv_item_msg_left.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.ll_item_msg_right.setVisibility(View.VISIBLE);
            holder.ll_item_msg_left.setVisibility(View.GONE);
            holder.tv_item_msg_right.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

}

