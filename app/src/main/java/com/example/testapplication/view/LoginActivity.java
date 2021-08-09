package com.example.testapplication.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.Login;
import com.example.testapplication.R;
import com.example.testapplication.databinding.ActivityLoginBinding;
import com.example.testapplication.utility.Utils;
import com.example.testapplication.viewmodel.ListViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    /**
     * ListViewModel object
     */
    private ListViewModel viewModel;

    ActivityLoginBinding loginBinding;

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

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        loginBinding.btnLogin.setOnClickListener(l -> {
            String email = "";
            String password = "";
            if (loginBinding.email.getText().toString().length() != 0) {
                if (isEmailValid(loginBinding.email.getText().toString())) {
                    email = loginBinding.email.getText().toString();
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter correct email", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (loginBinding.password.getText().toString().length() != 0) {
                password = loginBinding.password.getText().toString();
            } else {
                Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.length() != 0 && password.length() != 0)
                viewModel.login(email, password);

            observerViewModel();
        });

        loginBinding.createAccount.setOnClickListener(l -> {
            Intent intent = new Intent(this, SignupActivity.class);
            this.startActivity(intent);
        });

    }

    private void observerViewModel() {

        viewModel.usersLogin.observe(
                this,
                login -> {
                    if (login.getMeta().getStatus() == 200) {
                        Utils.userId = login.getResponse().getUser().getId();
                        Toast.makeText(LoginActivity.this, login.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        this.startActivity(intent);
                        finishAffinity();
                        // viewModel.usersLogin = new MutableLiveData<>();
                    } else {
                        Toast.makeText(LoginActivity.this, login.getResponse().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        viewModel.usersLogin = new MutableLiveData<>();
                    }


                }
        );

        viewModel.usersLoadError.observe(
                this, isError -> {
                    if (isError != null) {
                        if (isError)
                            Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                    }

                }
        );

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}