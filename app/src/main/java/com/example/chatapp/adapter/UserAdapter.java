package com.example.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.databinding.CustomUserBinding;
import com.example.chatapp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userViewHolder> {
    private List<UserModel> userModelList;
    private OnUserClickListener onUserClickListener;
    private Context context;

    public UserAdapter(OnUserClickListener onUserClickListener, Context context) {
        userModelList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
        this.context = context;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomUserBinding binding = CustomUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new userViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {
        holder.binding.userNameFrag.setText(userModelList.get(position).getUsername());
        Glide.with(context).load(userModelList.get(position).getImage()).centerCrop().into(holder.binding.imageViewUser);

        if (userModelList.get(position).getStatus().equals("online")) {
            holder.binding.status.setImageResource(R.drawable.online);
        } else
            holder.binding.status.setImageResource(R.drawable.offline);

    }

    @Override
    public int getItemCount() {
        if (userModelList == null) {
            return 0;
        } else
            return userModelList.size();
    }

    public interface OnUserClickListener {
        void UserClicked(int position, List<UserModel> userModelList);
    }

    class userViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CustomUserBinding binding;

        public userViewHolder(@NonNull CustomUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.userNameFrag.setOnClickListener(this);
            binding.imageViewUser.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onUserClickListener.UserClicked(getAdapterPosition(), userModelList);
        }
    }
}
