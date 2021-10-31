package com.team.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team.mychat.R;
import com.team.mychat.activities.MainActivity;
import com.team.mychat.activities.UserActivity;

public class HomeFragment extends Fragment {

    private View view;
    private FloatingActionButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        initItem();
        setListener();
        return view;
    }

    private void initItem(){
        btnAdd = view.findViewById(R.id.fabNewChat);
    }

    private void setListener(){
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            startActivity(intent);
        });
    }



}
