package com.team.mychat.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team.mychat.R;
import com.team.mychat.activities.MainActivity;
import com.team.mychat.utilities.Constants;
import com.team.mychat.utilities.PreferenceManager;


public class PasswordFragment extends Fragment {

    private View view;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;
    private EditText newPassword, newConfirm;
    private MaterialButton buttonSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_password, container, false);
        preferenceManager = new PreferenceManager(getActivity());
        initItem();
        setListener();
        return view;
    }

    private void initItem() {
        progressBar = view.findViewById(R.id.progressBarUpdate);
        newPassword = view.findViewById(R.id.inputNewPassword);
        newConfirm = view.findViewById(R.id.inputNewConfirmPassword);
        buttonSave = view.findViewById(R.id.buttonChagePass);
        db = FirebaseFirestore.getInstance();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListener(){
        buttonSave.setOnClickListener(v -> {
           if(isValid()){
               updatePassword();
           }
        });
    }

    private Boolean isValid() {
        if (newPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter new password");
            return false;
        } else if (newConfirm.getText().toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        } else if (!newPassword.getText().toString().equals(newConfirm.getText().toString())) {
            showToast("Password and confirm password must be same");
            return false;
        } if (newPassword.getText().toString().length() < 8) {
            showToast("Password must be lager than 8 characters");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            buttonSave.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else{
            buttonSave.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updatePassword(){
        loading(true);
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_PASSWORD, newPassword.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        showToast("Update Password Success!");
                        preferenceManager.putString(Constants.KEY_PASSWORD, newPassword.getText().toString().trim());
                    } else {
                        showToast("Something wrong!");
                        loading(false);
                    }
                });

    }


}