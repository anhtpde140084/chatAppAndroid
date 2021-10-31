package com.team.mychat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.team.mychat.R;
import com.team.mychat.databinding.ActivityChatBinding;
import com.team.mychat.models.User;
import com.team.mychat.utilities.Constants;

public class ChatActivity extends AppCompatActivity {

     private ActivityChatBinding binding;
     private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListener();
    }

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }

    private void setListener(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

}