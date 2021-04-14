package com.example.noisitapp.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.noisitapp.Model.User;
import com.example.noisitapp.R;
import com.example.noisitapp.ViewModel.FirebaseViewModel;
import com.example.noisitapp.JosepFortunyClasses.GpsUtils;
import com.example.noisitapp.ViewModel.UserViewModelComunication;

import java.io.File;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MenuNavigationActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private String uID,uPassword;
    private FirebaseViewModel firebaseUser;
    private UserViewModelComunication userViewModelCommunication;
    private final String SHARED_PREF_NAME = "sharedPrefs";
    private final String SESSION_KEY = "-1";
    private final String PASSWORD_KEY = "-3";
    private GpsUtils gpu;

    /***
     *
     * @param savedInstanceState
     ***/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpu = new GpsUtils(this);
        gpu.turnOnGPS();
        firebaseUser = new ViewModelProvider(this).get(FirebaseViewModel.class);
        setContentView(R.layout.activity_menu_navigation);
        loadSharedPreferences();
        setToolbar();
        reviewAppPermissions();
        createStorageFilesifExists();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode !=  101){
            gpu.turnOnGPS();
        }
    }

    /***
     *
     *
     ***/
    @Override

    protected void onStart() {
        super.onStart();
        checkSessionConfiguration();
    }

    /***
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation,menu);
        return true;
    }

    /***
     *
     * @param item
     * @return
     ***/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.aboutFragment) {
            if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_Dashboard))) { Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_dashboardFragment_to_aboutFragment2);
            } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_NewRecording))) { Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_newRecordingFragment_to_aboutFragment2);
            } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_EditRecording))) { Navigation.findNavController(this, R.id.nav_host_fragment_menu_nav).navigate(R.id.action_editRecordingFragment_to_aboutFragment2);
            } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_ViewChartRecording))) { Navigation.findNavController(this, R.id.nav_host_fragment_menu_nav).navigate(R.id.action_viewChartRecordingFragment_to_aboutFragment2);
            }else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_MyAccount))){ Navigation.findNavController(this, R.id.nav_host_fragment_menu_nav).navigate(R.id.action_aboutUserFragment_to_aboutFragment2); }
        }else if(item.getItemId() ==R.id.aboutUser){
            userViewModelCommunication.copyUser();
            if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_Dashboard))){ Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_dashboardFragment_to_aboutUserFragment);
            } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_NewRecording))) { Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_newRecordingFragment_to_aboutUserFragment);
            } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_EditRecording))){ Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_editRecordingFragment_to_aboutUserFragment);
            } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_ViewChartRecording))) { Navigation.findNavController(this, R.id.nav_host_fragment_menu_nav).navigate(R.id.action_viewChartRecordingFragment_to_aboutUserFragment);
            }else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_AboutUs))){ Navigation.findNavController(this, R.id.nav_host_fragment_menu_nav).navigate(R.id.action_aboutFragment2_to_aboutUserFragment); }
        }else if (item.getItemId() ==R.id.exit){
            closeSession();
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     *
     ***/
    @Override
    public void onBackPressed() {
        if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_Dashboard))) {
            finish();
        } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_NewRecording))){
            Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_newRecordingFragment_to_dashboardFragment);
        } else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_EditRecording))){
            Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_editRecordingFragment_to_dashboardFragment);
        }else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_AboutUs))){
            Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_aboutFragment2_to_dashboardFragment);
        }else if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_ViewChartRecording))){
            Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_viewChartRecordingFragment_to_editRecordingFragment);
        }else  if (myToolbar.getTitle().toString().equals(getResources().getString(R.string.Toolbar_MyAccount))){
            Navigation.findNavController(this,R.id.nav_host_fragment_menu_nav).navigate(R.id.action_aboutUserFragment_to_dashboardFragment);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void checkSessionConfiguration(){
        userViewModelCommunication = new ViewModelProvider(this).get(UserViewModelComunication.class);
        firebaseUser.checkUserExistFirebase(uID);
        firebaseUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    userViewModelCommunication.setUser(user);
                } else {
                    closeSession();
                }
            }
        });

    }
    private void createStorageFilesifExists(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Noisitapp Audio Files/";
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()){
                Toast.makeText(this,getResources().getString(R.string.toast_error_space), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void setToolbar() {
        // Setting up the Toolbar
        myToolbar = (Toolbar) findViewById(R.id.toolbar_menu_nav);
        myToolbar.setTitle(R.string.Toolbar_Dashboard);
        setSupportActionBar(myToolbar);
    }
    private void reviewAppPermissions() {
        if (!checkPermissionsFromDevice()) {
            requestPermissions();
        }
    }
    private boolean checkPermissionsFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, RECORD_AUDIO);
        int fine_location_result = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED && fine_location_result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        int REQUEST_PERMISSION_CODE = 1000;
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                RECORD_AUDIO,ACCESS_FINE_LOCATION
        }, REQUEST_PERMISSION_CODE);
    }
    public void closeSession() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("FromMenuActivity","true");
        startActivity(intent);
        finish();
    }
    public void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        uID = sharedPreferences.getString(SESSION_KEY, SESSION_KEY);
        uPassword = sharedPreferences.getString(PASSWORD_KEY, PASSWORD_KEY);
    }
    public void saveSessionUserSharedPreferences(String id, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SESSION_KEY, id);
        editor.putString(PASSWORD_KEY,password);
        editor.apply();
    }
    public String getUserPassword(){
        return uPassword;
    }
    public void updateRecodringUser( User user){
        firebaseUser.updateRecordingsFirebase(user);
    }

    @SuppressLint("ShowToast")
    public void changePasswordFirebase(String password) {
        if (firebaseUser.changePasswordFirebase(password)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_password_changed_correctly),Toast.LENGTH_SHORT ).show();
            saveSessionUserSharedPreferences(uID,password);
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_password_cannot_be_changed),Toast.LENGTH_SHORT ).show();
        }
    }
}
