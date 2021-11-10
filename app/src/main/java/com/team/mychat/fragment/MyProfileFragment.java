package com.team.mychat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.mychat.R;
import com.team.mychat.activities.MainActivity;
import com.team.mychat.adapter.RecentConversionsAdapter;
import com.team.mychat.models.ChatMessage;
import com.team.mychat.utilities.Constants;
import com.team.mychat.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyProfileFragment extends Fragment {

    private View view;
    private FrameLayout lnButton, layoutUpdateImage;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;
    private EditText edName, edEmail, edPhone, edAddress;
    private MaterialButton buttonSave;
    private TextView textName, textEmail, textUpdate, textCancel, textAddImage, nameMain, emailMain, textPhone, textAddress;
    private RoundedImageView imageView, avatar;
    private String encodedImage;
    private LinearLayout  lnName, lnEmail, lnPhone, lnAddress;

    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myprofile, container, false);
        preferenceManager = new PreferenceManager(getActivity());
        initItem();
        loadUserDetails();
        setListener();
        mainActivity = (MainActivity) getActivity();
        return view;
    }

    private void initItem() {
        nameMain = view.findViewById(R.id.textName);
        //-------------------------
        lnPhone =view.findViewById(R.id.layoutPhone);
        lnAddress =view.findViewById(R.id.layoutAddress);
        lnEmail = view.findViewById(R.id.layoutEmail);
        lnName = view.findViewById(R.id.layoutName);
        //
        textEmail = view.findViewById(R.id.textEmail);
        textName = view.findViewById(R.id.textName);
        textPhone = view.findViewById(R.id.textPhone);
        textAddress = view.findViewById(R.id.textAddress);
        //
        progressBar = view.findViewById(R.id.progressBar);
        imageView = view.findViewById(R.id.imageProfile);
        edEmail = view.findViewById(R.id.updateEmail);
        edName = view.findViewById(R.id.updateName);
        edPhone = view.findViewById(R.id.inputPhone);
        edAddress = view.findViewById(R.id.inputAddress);
        buttonSave = view.findViewById(R.id.buttonUpdate);
        textUpdate = view.findViewById(R.id.textUpdate);
        textCancel = view.findViewById(R.id.textCancel);
        textAddImage = view.findViewById(R.id.textUpdateImage);
        textAddImage.setVisibility(View.INVISIBLE);
        lnButton = view.findViewById(R.id.lnButton);
        layoutUpdateImage = view.findViewById(R.id.layoutImageUpdate);
        db = FirebaseFirestore.getInstance();
    }

    private void loadUserDetails() {
        textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        textPhone.setText(preferenceManager.getString(Constants.KEY_PHONE));
        textAddress.setText(preferenceManager.getString(Constants.KEY_ADDRESS));
        edName.setText(preferenceManager.getString(Constants.KEY_NAME));
        edEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        edAddress.setText(preferenceManager.getString(Constants.KEY_ADDRESS));
        edPhone.setText(preferenceManager.getString(Constants.KEY_PHONE));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
    }

    private void setListener() {

        textUpdate.setOnClickListener(v -> {
            loading(false);
            lnAddress.setVisibility(View.GONE);
            lnPhone.setVisibility(View.GONE);
            edEmail.setVisibility(View.VISIBLE);
            edName.setVisibility(View.VISIBLE);
            textUpdate.setVisibility(View.GONE);
            textCancel.setVisibility(View.VISIBLE);
            lnEmail.setVisibility(View.GONE);
            lnName.setVisibility(View.GONE);
            lnButton.setVisibility(View.VISIBLE);
            edPhone.setVisibility(View.VISIBLE);
            edAddress.setVisibility(View.VISIBLE);
        });
        textCancel.setOnClickListener(v -> {
            cancelButton();
        });

        buttonSave.setOnClickListener(v -> {
            if (isValid()) {
                updateInfor();
            }
        });

        layoutUpdateImage.setOnClickListener(v -> {
            loading(false);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
    }

    private void updateImage() {
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_IMAGE, encodedImage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Update image success!");
                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                        mainActivity.loadUserDetails();
                        cancelButton();
                        loading(true);
                    } else {
                        showToast("Something wrong!");
                        loading(false);
                    }
                });
    }

    private void updateInfor() {
        loading(true);
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_NAME, edName.getText().toString().trim(),
                Constants.KEY_EMAIL, edEmail.getText().toString().trim(),
                Constants.KEY_PHONE, edPhone.getText().toString().trim(),
                Constants.KEY_ADDRESS, edAddress.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Update success!");
                        preferenceManager.putString(Constants.KEY_EMAIL, edEmail.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_NAME, edName.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_PHONE, edPhone.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_ADDRESS, edAddress.getText().toString().trim());
                        mainActivity.loadUserDetails();
                        textEmail.setText(edEmail.getText().toString());
                        textName.setText(edName.getText().toString());
                        textPhone.setText(edPhone.getText().toString());
                        textAddress.setText(edAddress.getText().toString());
                        cancelButton();

                    } else {
                        showToast("Something wrong!");
                        loading(false);
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValid() {
        if (edName.getText().toString().trim().isEmpty()) {
            showToast("Enter name");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            buttonSave.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            buttonSave.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageView.setImageBitmap(bitmap);
                            textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                            if (encodedImage != null) {
                                updateImage();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void cancelButton() {
        lnAddress.setVisibility(View.VISIBLE);
        lnPhone.setVisibility(View.VISIBLE);
        edEmail.setVisibility(View.GONE);
        edName.setVisibility(View.GONE);
        textUpdate.setVisibility(View.VISIBLE);
        textCancel.setVisibility(View.GONE);
        lnEmail.setVisibility(View.VISIBLE);
        lnName.setVisibility(View.VISIBLE);
        lnButton.setVisibility(View.GONE);
        edPhone.setVisibility(View.GONE);
        edAddress.setVisibility(View.GONE);
        loading(false);
    }
}
