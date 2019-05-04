package com.example.kugrocery;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kugrocery.RoomDatabase.KuDatabase;
import com.example.kugrocery.RoomDatabase.ProductTable;
import com.example.kugrocery.adapters.ProductAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProductHome extends AppCompatActivity implements ProductListener.ProductClickListener, ProductListener.CheckoutListener {

    public static KuDatabase kuDatabase;
    private EditText sizeEditText;
    private Spinner productCategorySpinner;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ProductAdapter productAdapter;
    ArrayList<ProductTable> cartItem = new ArrayList();
    ProductListener.ProductClickListener productListener;
    boolean isUpdate = false;
    String categoryArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_home);
        kuDatabase = Room.databaseBuilder(getApplicationContext(), KuDatabase.class, "kudb")
                .allowMainThreadQueries()
                .build();
        categoryArray = getResources().getStringArray(R.array.categories_all);
        recyclerView = findViewById(R.id.product_recycler_view);
        productCategorySpinner = findViewById(R.id.product_category);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(aa);
        productCategorySpinner.setSelection(0);
        sizeEditText = findViewById(R.id.product_size);
        emptyView = findViewById(R.id.empty);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        DividerItemDecoration horizontalDecor = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration verticalDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(horizontalDecor);
        recyclerView.addItemDecoration(verticalDecor);
        productAdapter = new ProductAdapter(this);
        recyclerView.setAdapter(productAdapter);
        productListener = this;
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        Intent i = getIntent();
        if (i != null) {
            isUpdate = i.getBooleanExtra("isEdit", false);
        }
        if (isUpdate) {
            productCategorySpinner.setSelection(0);
            sizeEditText.setText("");
            productCategorySpinner.setVisibility(View.GONE);
            sizeEditText.setVisibility(View.GONE);

        }
        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sizeEditText.addTextChangedListener(textWatcher);
    }

    private void getProducts() {
        String category = categoryArray[productCategorySpinner.getSelectedItemPosition()];
        String size = sizeEditText.getText().toString().trim();
        List<ProductTable> productTableList;

        if (category.equals(categoryArray[0])) {
            productTableList = kuDatabase.productDao().getAllProduct();
        } else if (TextUtils.isEmpty(size)) {
            productTableList = kuDatabase.productDao().getAllProductByCategory(category);
        } else {
            productTableList = kuDatabase.productDao().getAllProductBySizeAndCategory(category, Integer.parseInt(size));

        }
        emptyView.setVisibility(productTableList.size() > 0 ? View.GONE : View.VISIBLE);
        productAdapter.swapList(productTableList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isUpdate) {
            getMenuInflater().inflate(R.menu.cashier_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_cart:
                if (cartItem.size() < 1) {
                    Toast.makeText(this, "Cart is empty, add an item first", Toast.LENGTH_LONG).show();
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("cart");
                    if (prev != null)
                        fragmentTransaction.remove(prev);

                    Parcelable listParcelable = Parcels.wrap(cartItem);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cart", listParcelable);
                    CartDialogFragment.NewInstance(bundle).show(fragmentTransaction, "cart");
                }
                break;
            case R.id.menu_logout:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductItemClicked(ProductTable productTable) {
        Log.e("LOG", "click click");
        if (!isUpdate) {
            if (Integer.parseInt(productTable.getProductQuantity()) < 1) {
                Toast.makeText(this, "Out of stock", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cartItem.contains(productTable)) {
                Toast.makeText(this, "Item already in cart", Toast.LENGTH_LONG).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    cartItem.add(productTable);
                }
                dialog.cancel();
            };
            builder.setIcon(R.drawable.shopping_cart)
                    .setCancelable(false)
                    .setTitle("Add to cart")
                    .setMessage("Do you want to add " + productTable.getProductName() + " to cart?")
                    .setPositiveButton("Yes", onClickListener)
                    .setNegativeButton("No", onClickListener);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Intent editIntent = new Intent(this, AddProduct.class);
            editIntent.putExtra("product", Parcels.wrap(productTable));
            startActivity(editIntent);
        }
    }


    @Override
    public void onCheckOut(ArrayList<ProductTable> productTables, CustomerDetails customerDetails) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        Parcelable listParcelable = Parcels.wrap(cartItem);
        intent.putExtra("cust", Parcels.wrap(customerDetails));
        intent.putExtra("cart", listParcelable);
        startActivity(intent);
        this.cartItem.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProducts();
    }
}
