package com.team.mychat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.team.mychat.databinding.ActivityForgotBinding;
import com.team.mychat.databinding.ActivityMainBinding;
import com.team.mychat.utilities.PreferenceManager;

public class ForgotActivity extends AppCompatActivity {

    private ActivityForgotBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
    }

    private void setListeners() {

        binding.buttonBack.setOnClickListener(v -> onBackPressed());
        binding.buttonSend.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {

            }
        });
    }

    private void sendOtp(int otp, String email){
        String subject = "Reset Password E Message";
        String message = "Here is your OTP "+ otp;

    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSend.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSend.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignUpDetails() {

        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else {
            return true;
        }
    }
}