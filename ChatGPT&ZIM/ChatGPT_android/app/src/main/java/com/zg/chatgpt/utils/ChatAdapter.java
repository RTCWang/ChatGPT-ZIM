package com.zg.chatgpt.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zg.chatgpt.R;
import com.zg.chatgpt.msg.Msg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> implements View.OnClickListener {
    private static final String TAG = "ChatAdapter";

    public static final String ChatGPT_ID = "chatgpt";
    private OnClickItemListener onClickItemListener;
    public List<Msg> mMsgList;
    private Context context;
    private String myUID;

    public ChatAdapter(Context context, String myUID) {
        this.context = context;
        this.myUID = myUID;
        this.mMsgList = new ArrayList<>();
    }


    public void setData(List<Msg> msgList) {
        mMsgList = msgList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder;

        View inflate = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        viewHolder = new ItemViewHolder(inflate, context);

        inflate.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        Date date = new Date();
        boolean isMe = msg.fromUID == myUID;

        holder.setRole(isMe, msg.fromUID.equals(ChatGPT_ID), msg.fromUID);
        date.setTime(msg.time);
        holder.time.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", date));
        holder.msg.setText(msg.msg);
    }


    @Override
    public int getItemCount() {
        return mMsgList.size();
    }


    @Override
    public void onClick(View v) {
//        if (this.onClickItemListener == null) return;
//        int pos = (Integer) (v.findViewById(R.id.wxid).getTag());
//        this.onClickItemListener.onClickItem(pos);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        FrameLayout root;
        TextView nameTV;
        LinearLayout textGravityRoot;
        ImageView myAvatar;
        ImageView tarAvatar;
        TextView time;
        TextView msg;
        Drawable myBg;
        Drawable tarBg;
        Drawable chatGPTAvatar;
        Drawable grayAvatar;

        public ItemViewHolder(View itemView, Context context) {
            super(itemView);
            root = itemView.findViewById(R.id.chatItem);
            textGravityRoot = itemView.findViewById(R.id.textGravityRoot);
            nameTV = itemView.findViewById(R.id.uname);
            myAvatar = itemView.findViewById(R.id.myImg);
            tarAvatar = itemView.findViewById(R.id.tarImg);

            time = itemView.findViewById(R.id.time);
            msg = itemView.findViewById(R.id.msg);
            myBg = context.getResources().getDrawable(R.drawable.bg_chat_me);
            tarBg = context.getResources().getDrawable(R.drawable.bg_chat_tar);
            chatGPTAvatar = context.getResources().getDrawable(R.drawable.chatgpt);
            grayAvatar = context.getResources().getDrawable(R.mipmap.icon_gray);
        }

        public void setRole(boolean isMe, boolean isChatGPT, String name) {
            nameTV.setText(name);
            if (isMe) {
                myAvatar.setVisibility(View.VISIBLE);
                tarAvatar.setVisibility(View.GONE);
                root.setPadding(dp2px(44), 0, dp2px(12), 0);
                textGravityRoot.setPadding(0, 0, dp2px(12), 0);
                textGravityRoot.setGravity(Gravity.END);
                nameTV.setGravity(Gravity.END);
                msg.setBackground(myBg);
                msg.setTextColor(Color.WHITE);
            } else {
                nameTV.setGravity(Gravity.START);
                myAvatar.setVisibility(View.GONE);
                tarAvatar.setVisibility(View.VISIBLE);
                if (isChatGPT) tarAvatar.setImageDrawable(chatGPTAvatar);
                else tarAvatar.setImageDrawable(grayAvatar);
                root.setPadding(dp2px(12), 0, dp2px(44), 0);
                textGravityRoot.setPadding(dp2px(12), 0, 0, 0);
                textGravityRoot.setGravity(Gravity.START);
                msg.setBackground(tarBg);
                msg.setTextColor(Color.rgb(42, 42, 42));
//                 ViewGroup.LayoutParams lp = root.getLayoutParams();
//
//                pad.setLayoutParams(lp);
            }
        }

        public int dp2px(float dpValue) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

    }


    public void setOnClickItemListener(OnClickItemListener onClickItemBtnListener) {
        this.onClickItemListener = onClickItemBtnListener;
    }

    public interface OnClickItemListener {
        void onClickItem(int position);
    }
}
