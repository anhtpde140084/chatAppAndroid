package com.team.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.mychat.R;
import com.team.mychat.databinding.ActivityMainBinding;
import com.team.mychat.fragment.HomeFragment;
import com.team.mychat.fragment.MyProfileFragment;
import com.team.mychat.utilities.Constants;
import com.team.mychat.utilities.PreferenceManager;

import java.util.HashMap;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    // anh xa
    private  TextView tvName;
    private TextView tvEmail;
    private RoundedImageView imageView;

    // tao constant
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_MY_PROFILE = 1;
    private static final int FRAGMENT_SIGN_OUT = 2;
    //current fragment
    private int mCurrentFragment = FRAGMENT_HOME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        // anh xa when start

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar,
                R.string.navgitation_drawer_open,R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navgationView.setNavigationItemSelectedListener(this);

        //start current home
        replaceFragment(new HomeFragment());
        binding.navgationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        //end

        // khởi tạo item và load user
        initItem();
        loadUserDetails();
        // end
        getToken();
        //setListener();

    }
    private void setListener(){
        TextView tvLogout = findViewById(R.id.imageSignOut);
        tvLogout.setOnClickListener(v-> signOut());
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
               .addOnSuccessListener(unused ->  showToast("Token updated successfully"))
                .addOnFailureListener(e ->showToast("Unable to update token"));
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    //set ánh xạ item
    private void initItem(){
        tvName = binding.navgationView.getHeaderView(0).findViewById(R.id.textName);
        tvEmail = binding.navgationView.getHeaderView(0).findViewById(R.id.textEmail);
        imageView = binding.navgationView.getHeaderView(0).findViewById(R.id.imageProfileMain);
    }

    // load user
    private void loadUserDetails(){
        tvName.setText(preferenceManager.getString(Constants.KEY_NAME));
        tvEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imageView.setImageBitmap(bitmap);
        System.out.println(preferenceManager.getString(Constants.KEY_EMAIL) + "aaa");
        System.out.println(preferenceManager.getString(Constants.KEY_NAME));
    }

    private void signOut(){
        showToast("SignOut...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    // chọn item trong từng thằng navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            if(mCurrentFragment != FRAGMENT_HOME){
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        } else if(id == R.id.my_profile){
            if(mCurrentFragment != FRAGMENT_MY_PROFILE){
                replaceFragment(new MyProfileFragment());
                mCurrentFragment = FRAGMENT_MY_PROFILE;
            }
        } else if(id == R.id.imageSignOut){
            signOut();
            finish();
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // set nút back trên đth để đóng drawer và đóng áp
    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    // replace fragment
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.commit();
    }

}