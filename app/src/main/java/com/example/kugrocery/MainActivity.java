package com.example.kugrocery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button cashierLogin;
    private Button adminSignIn;

    private final String adminUserName = "salim";

    private final String adminPassword = "salim1234";

    private EditText adminUserNAmeEditText;
    private EditText adminPasswordEditText;

    private SharedPreferences mPrefrence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefrence = PreferenceManager.getDefaultSharedPreferences(this);


        adminUserNAmeEditText = findViewById(R.id.editText1);
        adminPasswordEditText = findViewById(R.id.editText2);


        cashierLogin = findViewById(R.id.cashier_login);
        cashierLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CashierSignIn.class);
                startActivity(intent);
            }
        });

        adminSignIn = findViewById(R.id.admin_signin);
        adminSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(adminUserNAmeEditText.getText().toString())) {
                    adminUserNAmeEditText.setError("Username Required");
                    adminUserNAmeEditText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(adminPasswordEditText.getText().toString())) {
                    adminPasswordEditText.setError("Password Required");
                    adminPasswordEditText.requestFocus();
                    return;
                }

                if (adminUserNAmeEditText.getText().toString().equals(adminUserName) && adminPasswordEditText.getText().toString().equals(adminPassword)) {

                    //SharePreference
                    SharedPreferences.Editor mEditor = mPrefrence.edit();
                    mEditor.putBoolean("IsAdmin", true);
                    mEditor.apply();
                    redirect(CashierSignUp.class);


                } else {
                    Toast.makeText(MainActivity.this, "Password or Username Incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void redirect(Class destination) {
        Intent intent = new Intent(MainActivity.this, destination);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPrefrence.contains("IsAdmin")) {
            redirect(CashierSignUp.class);
        } else if (mPrefrence.contains(CashierSignIn.CASHER_ID)) {
            redirect(ProductHome.class);
        }
    }
}
