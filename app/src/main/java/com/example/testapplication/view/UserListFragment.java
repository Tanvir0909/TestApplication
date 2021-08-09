package com.example.testapplication.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.R;
import com.example.testapplication.adapter.UserListAdapter;
import com.example.testapplication.databinding.FragmentUserListBinding;
import com.example.testapplication.viewmodel.ListViewModel;

import java.util.ArrayList;


public class UserListFragment extends Fragment {
    private ListViewModel viewModel;
    FragmentUserListBinding userListBinding;
    private UserListAdapter userListAdapter;
    RecyclerView rvUserList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false);
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        userListAdapter = new UserListAdapter(new ArrayList<>(), getContext());
        rvUserList = userListBinding.userList;
        viewModel.getUserList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvUserList.setLayoutManager(layoutManager);
        rvUserList.setAdapter(userListAdapter);

        userListAdapter.setOnClickFromJobList(new UserListAdapter.OnClickFromUserList() {
            @Override
            public void onClickRemove(int id) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.alart_dialouge_delete, null);

                dialogBuilder.setView(dialogView);

                Button confirm = dialogView.findViewById(R.id.confirm);
                Button cancel = dialogView.findViewById(R.id.cancel);

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();


                confirm.setOnClickListener(v -> {
                    viewModel.getUserDelete(String.valueOf(id).toString());
                    //Toast.makeText(getContext(), String.valueOf(id).toString(), Toast.LENGTH_SHORT).show();

                    viewModel.userDelete.observe(
                            getViewLifecycleOwner(),
                            delete -> {
                                if (delete.getMeta().getStatus() == 200) {
                                    Toast.makeText(getContext(), delete.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    viewModel.userDelete = new MutableLiveData<>();
                                    viewModel.getUserList();
                                    observerViewModelForUserList();
                                    alertDialog.cancel();
                                } else {
                                    Toast.makeText(getContext(), delete.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    viewModel.userDetails = new MutableLiveData<>();
                                    alertDialog.cancel();
                                }
                            }
                    );

                    viewModel.userListLoadError.observe(
                            getViewLifecycleOwner(), isError -> {
                                if (isError != null) {
                                    if (isError)
                                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();

                                }

                            }
                    );


                });

                cancel.setOnClickListener(v -> {
                    alertDialog.cancel();
                });

            }
        });

        observerViewModelForUserList();
        return userListBinding.getRoot();
    }

    private void observerViewModelForUserList() {
        viewModel.userList.observe(
                getViewLifecycleOwner(),
                userList -> {
                    if (userList.getMeta().getStatus() == 200) {
                        userListAdapter.clearUserList();
                        userListBinding.totalUser.setText(String.valueOf(userList.getResponse().getUserList().size()));
                        userListAdapter.updateUserList(userList.getResponse().getUserList());
                        viewModel.usersLogin = new MutableLiveData<>();
                    } else {
                        Toast.makeText(getContext(), userList.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        viewModel.userDetails = new MutableLiveData<>();
                    }


                }
        );

        viewModel.userListLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError)
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();

                    }

                }
        );


    }
}