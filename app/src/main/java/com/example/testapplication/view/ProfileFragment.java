package com.example.testapplication.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.testapplication.R;
import com.example.testapplication.databinding.FragmentProfileBinding;
import com.example.testapplication.utility.Utils;
import com.example.testapplication.viewmodel.ListViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileFragment extends Fragment implements OnMapReadyCallback {
    private ListViewModel viewModel;
    FragmentProfileBinding profileBinding;
    private MapView mapView;
    private GoogleMap gmap;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyDjpJtCPg3jqYcRPTkb4lVSf82QqneLLwg";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

/*        ActionBar actionBar = ((AppCompatActivity)
                requireActivity()).getSupportActionBar();

        if(actionBar!=null)
            actionBar.setTitle("items");*/

        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        viewModel.getUserDetails(String.valueOf(Utils.userId));


        observerViewModel();

        //  Toast.makeText(getContext(), String.valueOf(Utils.userId).toString(), Toast.LENGTH_SHORT).show();


// Obtain the SupportMapFragment and get notified when the map is ready to be used.
/*        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);

        */

        Bundle mapViewBundle = new Bundle();
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        //   mapViewBundle = mapViewBundle.getBundle(MAP_VIEW_BUNDLE_KEY);
        mapView = profileBinding.map;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        return profileBinding.getRoot();
    }


    private void observerViewModel() {


        viewModel.userDetails.observe(
                getViewLifecycleOwner(),
                singleUser -> {
                    if (singleUser.getMeta().getStatus() == 200) {
                        profileBinding.name.setText(singleUser.getResponse().getUserData().getName());
                        profileBinding.email.setText(singleUser.getResponse().getUserData().getEmail());
                        profileBinding.mobile.setText(singleUser.getResponse().getUserData().getPhone());
                        profileBinding.dateTime.setText(convertDate(singleUser.getResponse().getUserData().getCreatedAt()));
                        Glide.with(this)
                                .load(singleUser.getResponse().getUserData().getProfileImage())
                                .into(profileBinding.profileImage);
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }
}