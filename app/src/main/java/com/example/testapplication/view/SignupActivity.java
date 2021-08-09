package com.example.testapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.testapplication.R;
import com.example.testapplication.databinding.ActivitySignupBinding;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding signupBinding;
    private FusedLocationProviderClient client;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

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

        signupBinding.loginAccount.setOnClickListener(l -> {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
            finish();
        });
        signupBinding.btnSignup.setOnClickListener(l -> {
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
                if (signupBinding.password.getText().toString().length() >= 8 || signupBinding.password.getText().toString().length() <= 20){
                    password = signupBinding.password.getText().toString();
                }else {
                    Toast.makeText(SignupActivity.this, "Min length 8 and max 20 ", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                Toast.makeText(SignupActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (signupBinding.mblNo.getText().toString().length() != 0) {
                if (signupBinding.mblNo.getText().toString().length() == 11){
                    mobile = Integer.parseInt(signupBinding.mblNo.getText().toString());
                }else {
                    Toast.makeText(SignupActivity.this, "Mobile no must be 11 digit", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(SignupActivity.this, "Please enter your mobile no", Toast.LENGTH_SHORT).show();
                return;
            }

            




        });


        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        } else
            getCurrentLocation();


    }


    public void showErrorMessage(String errorText){
        Toast.makeText(SignupActivity.this, errorText, Toast.LENGTH_SHORT).show();
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
                    double lat = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                    double log = locationResult.getLocations().get(lastLocationIndex).getLongitude();
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

}