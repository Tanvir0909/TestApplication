package com.example.testapplication.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.testapplication.R;
import com.example.testapplication.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
    ActivityWelcomeBinding welcomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        welcomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);


        welcomeBinding.btnLogin.setOnClickListener(l->{
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        });

        welcomeBinding.btnSignup.setOnClickListener(l->{
            Intent intent = new Intent(this, SignupActivity.class);
            this.startActivity(intent);
        });

    }
}