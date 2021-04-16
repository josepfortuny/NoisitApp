package com.example.noisitapp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.example.noisitapp.Model.User;
import com.example.noisitapp.R;
import com.example.noisitapp.ViewModel.FirebaseViewModel;


public class LoginActivity  extends AppCompatActivity {
    private LinearLayout ly_login;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String uID;
    private final String SHARED_PREF_NAME = "sharedPrefs";
    private final String SESSION_KEY = "-1";
    private final String PASSWORD_KEY = "-3";
    private FirebaseViewModel userViewModel;
    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        setContentView(R.layout.activity_login);
        ly_login = findViewById(R.id.visibleLogin);
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkSessionConfiguration();
    }
    @SuppressLint("CommitPrefEdits")
    private void checkSessionConfiguration(){
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = getIntent();
        String fromMenuActivity = intent.getStringExtra("FromMenuActivity");
        if (fromMenuActivity == null) {
            loadSharedPreferences();
            if(!uID.equals(SESSION_KEY)) {
                userViewModel.checkUserExistFirebase(uID);
                userViewModel.getUser().observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            ly_login.setVisibility(View.GONE);
                            gotoSplash();
                            //saveSessionUserSharedPreferences(user.getUid());
                        } else {
                            removeSessionSharedPreferences();
                            ly_login.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }else{
                removeSessionSharedPreferences();
                ly_login.setVisibility(View.VISIBLE);
            }
        }else{
            removeSessionSharedPreferences();
            ly_login.setVisibility(View.VISIBLE);
        }
    }
    public void gotoSplash() {
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(intent);
        finish();
    }
    public void saveSessionUserSharedPreferences(String id, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SESSION_KEY, id);
        editor.putString(PASSWORD_KEY,password);
        editor.apply();
    }
    public void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        uID = sharedPreferences.getString(SESSION_KEY, SESSION_KEY);
    }
    public void removeSessionSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SESSION_KEY, SESSION_KEY);
        editor.putString(PASSWORD_KEY, PASSWORD_KEY);
        editor.apply();
    }
    public void checkUserFirebase(String email, String password) {
        userViewModel.checkUserFirebaseAuth(email,password);
        userViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null){
                    gotoSplash();
                    saveSessionUserSharedPreferences(user.getUid(),password);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.error_introduce_password), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
