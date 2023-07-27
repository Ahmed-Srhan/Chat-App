package com.example.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageViewHolder> {
    public static final int MESSAGE_RIGHT = 0;//for user layout
    public static final int MESSAGE_LEFT = 1;//for friend layout
    private List<MessageModel> messageModelList;

    public void setMessageModelList(List<MessageModel> messageModelList) {
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MESSAGE_RIGHT) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new messageViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new messageViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {
        holder.message.setVisibility(View.VISIBLE);
        holder.time.setVisibility(View.VISIBLE);
        holder.message.setText(messageModelList.get(position).getMessage());
        holder.time.setText(messageModelList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        if (messageModelList == null) {
            return 0;

        } else
            return messageModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        MessageModel messageModel = messageModelList.get(position);
        if (messageModel.getSender().equals(firebaseAuth.getCurrentUser().getUid())) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }

    }

    class messageViewHolder extends RecyclerView.ViewHolder {
        TextView time, message;

        public messageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.displayTime);
        }
    }
}
