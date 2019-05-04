package com.example.kugrocery;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kugrocery.RoomDatabase.CashierTable;
import com.example.kugrocery.RoomDatabase.KuDatabase;
import com.example.kugrocery.RoomDatabase.ProductTable;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CheckoutActivity extends AppCompatActivity {

    EditText custNameEditText, dateEditText, totalEditText;
    RecyclerView itemRecyclerView;
    ArrayList<ProductTable> productTableArrayList;
    CustomerDetails customerDetails;

    TextView cashierName;
    KuDatabase kuDatabase;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        custNameEditText = findViewById(R.id.cust_name);
        dateEditText = findViewById(R.id.date);
        itemRecyclerView = findViewById(R.id.items_recycler_view);
        totalEditText = findViewById(R.id.total);
        cashierName = findViewById(R.id.cashier_name);
        kuDatabase = Room.databaseBuilder(getApplicationContext(), KuDatabase.class, "kudb")
                .allowMainThreadQueries()
                .build();

        Intent intent = getIntent();

        if (intent == null) {
            finish();
            return;
        }

        productTableArrayList = Parcels.unwrap(intent.getParcelableExtra("cart"));
        customerDetails = Parcels.unwrap(intent.getParcelableExtra("cust"));
        custNameEditText.setText(customerDetails.getFirstName().concat(" ").concat(customerDetails.getLastName()));
        itemRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter();
        itemRecyclerView.setAdapter(checkoutAdapter);
        int sum = 0;
        for (ProductTable productTable : productTableArrayList) {
            sum += productTable.getTotalAmount();
        }

        totalEditText.setText("" + sum);
        dateEditText.setText(formatDate(System.currentTimeMillis()));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int cashierId = preferences.getInt(CashierSignIn.CASHER_ID, 1);
        CashierTable cashierTable = kuDatabase.cashierDao().getCashierWithId(cashierId);
        if (cashierTable != null) {
            cashierName.setText("Cashier: "+cashierTable.getUserName());
        }


    }

    public static String formatDate(long milliseconds) /* This is your topStory.getTime()*1000 */ {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }


    class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

        public CheckoutAdapter() {
        }

        @NonNull
        @Override
        public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checkout_list, viewGroup, false);
            return new CheckoutViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CheckoutViewHolder checkoutViewHolder, int i) {
            checkoutViewHolder.bind(productTableArrayList.get(i));
        }

        @Override
        public int getItemCount() {
            return productTableArrayList.size();
        }

        class CheckoutViewHolder extends RecyclerView.ViewHolder {
            TextView serial;
            TextView item;
            TextView qty;

            public CheckoutViewHolder(@NonNull View itemView) {
                super(itemView);
                serial = itemView.findViewById(R.id.serial);
                item = itemView.findViewById(R.id.item);
                qty = itemView.findViewById(R.id.qty);
            }

            public void bind(ProductTable productTable) {
                serial.setText("" + (getAdapterPosition() + 1));
                item.setText(productTable.getProductName());
                qty.setText("" + productTable.getQuantityLeaving() + "*" + productTable.getProductPrice() + "(" + productTable.getTotalAmount() + ")");
            }

        }
    }

}
