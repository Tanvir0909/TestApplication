package com.example.testapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.testapplication.R;
import com.example.testapplication.databinding.ActivitySignupBinding;
import com.example.testapplication.utility.Utils;
import com.example.testapplication.viewmodel.ListViewModel;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding signupBinding;
    private ListViewModel viewModel;
    private FusedLocationProviderClient client;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    File file;

    boolean selectPhoto = false;

    Double latitude;
    Double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        signupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        file = new File("");
        signupBinding.loginAccount.setOnClickListener(l -> {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
            finish();
        });

        signupBinding.btnEditImage.setOnClickListener(l -> {
            requestPermissionForTed();
        });


        signupBinding.btnSignup.setOnClickListener(l -> {

            Map<String, Object> params = new HashMap<>();

            String name = "";
            String email = "";
            String password = "";
            int mobile = 0;
            int gender = 0;

            if (signupBinding.name.getText().toString().length() != 0) {
                name = signupBinding.name.getText().toString();
            } else {
                Toast.makeText(SignupActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (signupBinding.email.getText().toString().length() != 0) {
                if (isEmailValid(signupBinding.email.getText().toString())) {
                    email = signupBinding.email.getText().toString();
                } else {
                    Toast.makeText(SignupActivity.this, "Please enter correct email", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(SignupActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (signupBinding.password.getText().toString().length() != 0) {
                if (signupBinding.password.getText().toString().length() >= 8 || signupBinding.password.getText().toString().length() <= 20) {
                    password = signupBinding.password.getText().toString();
                } else {
                    Toast.makeText(SignupActivity.this, "Min length 8 and max 20 ", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                Toast.makeText(SignupActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (signupBinding.mblNo.getText().toString().length() != 0) {
                if (signupBinding.mblNo.getText().toString().length() == 11) {
                    mobile = Integer.parseInt(signupBinding.mblNo.getText().toString());
                } else {
                    Toast.makeText(SignupActivity.this, "Mobile no must be 11 digit", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(SignupActivity.this, "Please enter your mobile no", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!selectPhoto)
                Toast.makeText(SignupActivity.this, "Upload a photo", Toast.LENGTH_SHORT).show();

            if (signupBinding.radioMale.isChecked()) {
                gender = 1;
            } else {
                gender = 2;
            }

            if (name.length() != 0 && email.length() != 0 && password.length() != 0 && mobile != 0 && gender != 0 && selectPhoto) {

                viewModel.userSignUp(name, email, String.valueOf(latitude), String.valueOf(longitude), gender, mobile, password, file);

                viewModel.userSignUp.observe(
                        this,
                        userSignup -> {
                            if (userSignup.getMeta().getStatus() == 200) {
                                Toast.makeText(SignupActivity.this, userSignup.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, LoginActivity.class);
                                this.startActivity(intent);
                                finishAffinity();
                                // viewModel.usersLogin = new MutableLiveData<>();
                            } else {
                                Toast.makeText(SignupActivity.this, userSignup.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                viewModel.userSignUp = new MutableLiveData<>();
                            }

                        }
                );

                viewModel.userSignUpLoadError.observe(
                        this, isError -> {
                            if (isError != null) {
                                if (isError) {
                                    Toast.makeText(SignupActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    viewModel.userSignUpLoadError = new MutableLiveData<>();
                                }
                            }
                        }
                );

            }


        });


        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        } else
            getCurrentLocation();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(SignupActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(SignupActivity.this).removeLocationUpdates(this);
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
                Toast.makeText(SignupActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {

        TedBottomPicker.with(SignupActivity.this)
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        signupBinding.profileImage.setImageURI(uri);
                        selectPhoto = true;

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
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
        File f = new File(getCacheDir(), String.valueOf(new Timestamp(System.currentTimeMillis())));
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