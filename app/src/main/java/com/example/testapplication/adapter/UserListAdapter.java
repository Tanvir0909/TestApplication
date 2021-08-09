package com.example.testapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.User;
import com.example.testapplication.R;
import com.example.testapplication.databinding.ItemUserBinding;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<User> users;
    Context context;

    public UserListAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }


    public OnClickFromUserList onClickFromUserList;

    public void setOnClickFromJobList(OnClickFromUserList onClickFromUserList) {
        this.onClickFromUserList = onClickFromUserList;
    }

    public interface OnClickFromUserList {
        void onClickRemove(int id);
        void onClickDetails(int id);

    }

    public void updateUserList(List<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }


    public void clearUserList() {
        this.users.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding userListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user, parent, false);
        return new ViewHolder(userListBinding.getRoot(), userListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User userList = users.get(position);
        holder.bind(userList);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding userListBinding;

        public ViewHolder(@NonNull View itemView, ItemUserBinding userListBinding) {
            super(itemView);
            this.userListBinding = userListBinding;
        }

        public void bind(User userList) {

            userListBinding.email.setText(userList.getEmail());
            userListBinding.name.setText(userList.getName());
            userListBinding.setUser(userList);

            userListBinding.deleteUser.setOnClickListener(l->{
                onClickFromUserList.onClickRemove(userList.getId());
            });


            userListBinding.details.setOnClickListener(l->{
                onClickFromUserList.onClickDetails(userList.getId());
            });



            userListBinding.executePendingBindings();
        }
    }
}
