package com.example.kugrocery;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kugrocery.RoomDatabase.CashierTable;
import com.example.kugrocery.RoomDatabase.KuDatabase;
import com.example.kugrocery.RoomDatabase.ProductDao;

public class CashierSignIn extends AppCompatActivity {

    private EditText cashierId, cashierPassword;
    private Button cashierSignin;
    public static final String CASHER_ID = "cashier_id";

    Intent i;
    public  KuDatabase kuDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_sign_in);

        cashierId = findViewById(R.id.cashier_id);
        cashierPassword = findViewById(R.id.cashier_password);
        cashierSignin = findViewById(R.id.casher_signin);

        kuDatabase = Room.databaseBuilder(getApplicationContext(), KuDatabase.class, "kudb")
                .allowMainThreadQueries()
                .build();

        cashierSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(cashierId.getText().toString())){
                    cashierId.setError("User Id not Found");
                    cashierId.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(cashierPassword.getText().toString())){
                    cashierPassword.setError("Password not Found");
                    cashierPassword.requestFocus();
                    return;
                }
                CashierTable cashierTable = kuDatabase.cashierDao().getCashierWithIdAndPassword(cashierId.getText().toString(), cashierPassword.getText().toString());
                if(cashierTable != null){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(CashierSignIn.this).edit();
                    editor.putInt(CASHER_ID, cashierTable.getId());
                    editor.apply();
                    Intent intent = new Intent(CashierSignIn.this, ProductHome.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
