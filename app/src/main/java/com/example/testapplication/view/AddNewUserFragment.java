package com.example.testapplication.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.testapplication.R;
import com.example.testapplication.databinding.FragmentAddNewUserBinding;
import com.example.testapplication.viewmodel.ListViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddNewUserFragment extends Fragment {
    private ListViewModel viewModel;
    FragmentAddNewUserBinding newUserBinding;

    File file;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    boolean selectPhoto = false;

    Double latitude;
    Double longitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        newUserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_user, container, false);
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);


        newUserBinding.btnEditImage.setOnClickListener(l -> {
            requestPermissionForTed();
        });

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
        } else
            getCurrentLocation();


        newUserBinding.btnSave.setOnClickListener(l -> {

            Map<String, Object> params = new HashMap<>();

            String name = "";
            String email = "";
            String password = "";
            int mobile = 0;
            int gender = 0;

            if (newUserBinding.name.getText().toString().length() != 0) {
                name = newUserBinding.name.getText().toString();
            } else {
                Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newUserBinding.email.getText().toString().length() != 0) {
                if (isEmailValid(newUserBinding.email.getText().toString())) {
                    email = newUserBinding.email.getText().toString();
                } else {
                    Toast.makeText(getContext(), "Please enter correct email", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newUserBinding.password.getText().toString().length() != 0) {
                if (newUserBinding.password.getText().toString().length() >= 8 || newUserBinding.password.getText().toString().length() <= 20) {
                    password = newUserBinding.password.getText().toString();
                } else {
                    Toast.makeText(getContext(), "Min length 8 and max 20 ", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newUserBinding.mblNo.getText().toString().length() != 0) {
                if (newUserBinding.mblNo.getText().toString().length() == 11) {
                    mobile = Integer.parseInt(newUserBinding.mblNo.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Mobile no must be 11 digit", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(getContext(), "Please enter your mobile no", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!selectPhoto)
                Toast.makeText(getContext(), "Upload a photo", Toast.LENGTH_SHORT).show();

            if (newUserBinding.radioMale.isChecked()) {
                gender = 1;
            } else {
                gender = 2;
            }

            if (name.length() != 0 && email.length() != 0 && password.length() != 0 && mobile != 0 && gender != 0 && selectPhoto) {

                viewModel.userSignUp(name, email, String.valueOf(latitude), String.valueOf(longitude), gender, mobile, password, file);

                viewModel.userSignUp.observe(
                        getViewLifecycleOwner(),
                        userSignup -> {
                            if (userSignup.getMeta().getStatus() == 200) {
                                Toast.makeText(getContext(), userSignup.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                navController.popBackStack();
                                navController.navigate(R.id.navigation_userList);
                                // viewModel.usersLogin = new MutableLiveData<>();
                            } else {
                                Toast.makeText(getContext(), userSignup.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                viewModel.userSignUp = new MutableLiveData<>();
                            }


                        }
                );

                viewModel.userSignUpLoadError.observe(
                        getViewLifecycleOwner(), isError -> {
                            if (isError != null) {
                                if (isError) {
                                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    viewModel.userSignUpLoadError = new MutableLiveData<>();
                                }


                            }

                        }
                );

            }


        });

        return newUserBinding.getRoot();
    }


    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getContext()).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int lastLocationIndex = locationResult.getLocations().size() - 1;
                    latitude = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                    longitude = locationResult.getLocations().get(lastLocationIndex).getLongitude();
                }
            }
        }, Looper.getMainLooper());
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void requestPermissionForTed() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(getContext())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {

        TedBottomPicker.with(getActivity())
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        newUserBinding.profileImage.setImageURI(uri);
                        selectPhoto = true;

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                            Bitmap converetdImage = getResizedBitmap(bitmap, 2000);
                            file = convertBitmapToFile(converetdImage);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public File convertBitmapToFile(Bitmap bitmap) throws IOException {
        //create a file to write bitmap data
        File f = new File(getContext().getCacheDir(), String.valueOf(new Timestamp(System.currentTimeMillis())));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        return f;
    }
}