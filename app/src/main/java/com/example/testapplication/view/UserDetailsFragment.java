package com.example.testapplication.view;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.testapplication.R;
import com.example.testapplication.databinding.FragmentUserDetailsBinding;
import com.example.testapplication.utility.Utils;
import com.example.testapplication.viewmodel.ListViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDetailsFragment extends Fragment {

    private ListViewModel viewModel;
    FragmentUserDetailsBinding userDetailsBinding;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyDjpJtCPg3jqYcRPTkb4lVSf82QqneLLwg";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false);
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        viewModel.getUserDetails(String.valueOf(Utils.userDetailsId));


        observerViewModel();

        return userDetailsBinding.getRoot();
    }

    private void observerViewModel() {


        viewModel.userDetails.observe(
                getViewLifecycleOwner(),
                singleUser -> {
                    if (singleUser.getMeta().getStatus() == 200) {
                        userDetailsBinding.name.setText(singleUser.getResponse().getUserData().getName());
                        userDetailsBinding.email.setText(singleUser.getResponse().getUserData().getEmail());
                        userDetailsBinding.mobile.setText(singleUser.getResponse().getUserData().getPhone());
                        userDetailsBinding.dateTime.setText(convertDate(singleUser.getResponse().getUserData().getCreatedAt()));
                        Glide.with(this)
                                .load(singleUser.getResponse().getUserData().getProfileImage())
                                .into(userDetailsBinding.profileImage);
                        // viewModel.usersLogin = new MutableLiveData<>();
                    } else {
                        Toast.makeText(getContext(), singleUser.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        viewModel.userDetails = new MutableLiveData<>();
                    }


                }
        );

        viewModel.usersDetailsLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError)
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                }
        );


    }

    public static String convertDate(String dateTime) {

        try {
            Date date = simpleDateFormat.parse(dateTime);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            return simpleDateFormat1.format(date);
        } catch (ParseException e) {
            return "";
        } catch (NullPointerException e) {
            return "";
        }

    }
}