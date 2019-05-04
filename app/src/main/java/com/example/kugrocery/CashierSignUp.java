package com.example.kugrocery;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kugrocery.RoomDatabase.CashierTable;
import com.example.kugrocery.RoomDatabase.KuDatabase;

import java.util.regex.Pattern;

import static android.arch.persistence.room.Room.databaseBuilder;

public class CashierSignUp extends AppCompatActivity {

    Intent i;
    CashierTable cashierTable;


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    // "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    // "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{3,}" +               //at least 4 characters
                    "$");

    public static KuDatabase kuDatabase;

    public static EditText userId, name, password;
    private Button signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_sign_up);


        kuDatabase = Room.databaseBuilder(getApplicationContext(), KuDatabase.class, "kudb")
                .allowMainThreadQueries()
                .build();
        onCreateView();
        i = getIntent();
        if (i != null) {
            if (i.hasExtra("cashierTable")) {
                cashierTable = i.getParcelableExtra("cashierTable");
                setUpFields();
            }
        }

    }


    private void setUpFields() {
        userId.setText(cashierTable.getUserId());
        name.setText(cashierTable.getUserName());
        password.setText(cashierTable.getPassword());


    }

    private void onCreateView() {

        userId = findViewById(R.id.cashier_id);
        name = findViewById(R.id.cashier_name);
        password = findViewById(R.id.cashier_password);
        signup = findViewById(R.id.cashier_signUp);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String UserId = userId.getText().toString();
                String Name = name.getText().toString();
                String Password = password.getText().toString();

                CashierTable cashierTable = new CashierTable();

                cashierTable.setUserId(UserId);
                cashierTable.setUserName(Name);
                cashierTable.setPassword(Password);

                if (TextUtils.isEmpty(UserId)) {
                    userId.setError("User Id is required.");
                    userId.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Name)) {
                    name.setError("Name is required.");
                    name.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    password.setError("Password  is required.");
                    password.requestFocus();
                    return;
                } else if (!PASSWORD_PATTERN.matcher(Password).matches()) {
                    password.setError("Incorrect Password");
                    return;
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CashierSignUp.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("current", cashierTable.getUserId());
                editor.apply();
                kuDatabase.cashierDao().addCashier(cashierTable);
                Toast.makeText(CashierSignUp.this, ""+ kuDatabase.cashierDao().getAllcashier().size(), Toast.LENGTH_LONG).show();
                openDialog();


                userId.setText("");
                name.setText("");
                password.setText("");
            }
        });

    }

    public void openDialog() {
        ShowDialog showDialog = new ShowDialog();
        showDialog.show(getSupportFragmentManager(), "Dialog");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            startActivity(new Intent(CashierSignUp.this, AddProduct.class));
        }
        else if(id == R.id.menu_logout){
            SharedPreferences.Editor mPrefrenceEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            mPrefrenceEditor.clear();
            mPrefrenceEditor.apply();
            Intent intent = new Intent(CashierSignUp.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
