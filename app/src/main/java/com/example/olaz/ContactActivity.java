package com.example.olaz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;

import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private CoreListenerStub mCoreListener;
    EditText  edSate;
    ActionBarDrawerToggle toggle;
    private Button mButtonChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        edSate= findViewById(R.id.text_state);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
//        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_logout:



                        Core core = LinphoneService.getCore();
                        if (core != null) {
                            if (core.getDefaultProxyConfig() != null) {
                                core.removeProxyConfig(core.getDefaultProxyConfig());
                            }
                        }

                        if (core != null && core.getDefaultProxyConfig() == null) {
                            ProxyConfig[] proxyConfigs = core.getProxyConfigList();
                            if (proxyConfigs.length > 0) {
                                core.setDefaultProxyConfig(proxyConfigs[0]);
                            }
                        }

                        Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    }
                    return true;
                }

        });
        mButtonChat = findViewById(R.id.button_chat);
        mButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });





// Monitors the registration state of our account(s) and update the LED accordingly
        mCoreListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
                updateState(state);
            }
        };




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestCallPermissions();

        // Ask runtime permissions, such as record audio and camera
        // We don't need them here but once the user has granted them we won't have to ask again
    }


    @Override
    protected void onResume() {
        super.onResume();

        // The best way to use Core listeners in Activities is to add them in onResume
        // and to remove them in onPause
        LinphoneService.getCore().addListener(mCoreListener);

        // Manually update the LED registration state, in case it has been registered before
        // we add a chance to register the above listener
        ProxyConfig proxyConfig = LinphoneService.getCore().getDefaultProxyConfig();
        if (proxyConfig != null) {
            updateState(proxyConfig.getState());
        } else {
            // No account configured, we display the configuration activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Like I said above, remove unused Core listeners in onPause
        LinphoneService.getCore().removeListener(mCoreListener);
    }
    private  void updateState(RegistrationState state){
        switch (state){
            case Ok:
                edSate.setText("Ket noi OK");
                break;
            case Failed:
                edSate.setText("Fail");
                break;
            case Progress:
                edSate.setText("Connecting");
        }

    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        // Some required permissions needs to be validated manually by the user
        // Here we ask for record audio and camera to be able to make video calls with sound
        // Once granted we don't have to ask them again, but if denied we can
        int recordAudio =
                getPackageManager()
                        .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i(
                "[Permission] Record audio permission is "
                        + (recordAudio == PackageManager.PERMISSION_GRANTED
                        ? "granted"
                        : "denied"));
        int camera =
                getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i(
                "[Permission] Camera permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for camera");
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

}
