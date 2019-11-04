package com.example.olaz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;

public class LoginActivity extends AppCompatActivity {
    private Button btn_login ;
    private EditText edUser, edPass, edDomain;
    private AccountCreator  accountCreator;
    private CoreListenerStub coreListenerStub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountCreator = LinphoneService.getCore().createAccountCreator(null);

        edUser = findViewById(R.id.text_username);
        edPass = findViewById(R.id.text_password);
        edDomain = findViewById(R.id.text_server);
        btn_login = findViewById(R.id.button_login);

        // event click sign in
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configureAccount();
            }
        });

        // listener
        coreListenerStub = new CoreListenerStub(){
            @Override
            public void onRegistrationStateChanged(Core lc, ProxyConfig cfg, RegistrationState state, String message) {
                if (state == RegistrationState.Ok) {
                    finish();
                } else if (state == RegistrationState.Failed) {
                    Toast.makeText(LoginActivity.this, "Failure: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinphoneService.getCore().addListener(coreListenerStub);
    }

    @Override
    protected void onPause() {
        LinphoneService.getCore().removeListener(coreListenerStub);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void configureAccount() {
        // add username, domain and password to accountCreator
        accountCreator.setUsername(edUser.getText().toString());
        accountCreator.setDomain(edDomain.getText().toString());
        accountCreator.setPassword(edPass.getText().toString());

        // Set transport type to UDP by default
        accountCreator.setTransport(TransportType.Udp);


        ProxyConfig cfg = accountCreator.createProxyConfig();
        cfg.edit();
        Address proxy = Factory.instance().createAddress("sip:192.168.122.40");
        cfg.setServerAddr(proxy.asString());
        cfg.done();

        // Make sure the newly created one is the default
        LinphoneService.getCore().setDefaultProxyConfig(cfg);
        }





        //lauchgr



}

