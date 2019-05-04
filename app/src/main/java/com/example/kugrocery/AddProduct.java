package com.example.kugrocery;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.kugrocery.RoomDatabase.KuDatabase;
import com.example.kugrocery.RoomDatabase.ProductTable;

import org.parceler.Parcels;

import java.io.File;
import java.util.UUID;


public class AddProduct extends AppCompatActivity {

    Intent i;
    ProductTable productTable;

    public static KuDatabase kuDatabase;

    public static EditText productName, productId, productSize, productDescription, productPrice, productQuantity;
    Spinner productCategorySpinner;
    private Button add;
    private ImageView productImage;
    private String imagePath;
    boolean isEdit = false;
    private String productCategory;
    String categoryArray[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        kuDatabase = Room.databaseBuilder(getApplicationContext(), KuDatabase.class, "kudb")
                .allowMainThreadQueries()
                .build();
        onCreateView();
        i = getIntent();
        if (i != null) {
            if (i.hasExtra("cashierTable")) {
                productTable = i.getParcelableExtra("cashierTable");
                setUpFields();
            }
            if (i.hasExtra("product")) {
                isEdit = true;
                getSupportActionBar().setTitle("Edit Product");
                add.setVisibility(View.GONE);
                productTable = Parcels.unwrap(i.getParcelableExtra("product"));
                setUpFields();
            }
        }
        categoryArray = getResources().getStringArray(R.array.categories);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(aa);
        productCategorySpinner.setSelection(0, true);
        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productCategory = categoryArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpFields() {
        productName.setText(productTable.getProductName());
        productId.setText(productTable.getProductId());
        //productCategory.setText(productTable.getProductCategory());
        productSize.setText(String.valueOf(productTable.getProductSize()));
        productDescription.setText(productTable.getProductDescription());
        productQuantity.setText(productTable.getProductQuantity());
        productPrice.setText(productTable.getProductPrice());
        imagePath = productTable.getProductImage();
        File file = new File(productTable.getProductImage());
        if (file.exists()) {
            productImage.setImageURI(Uri.fromFile(file));
        } else {
            productImage.setImageResource(R.drawable.product);
        }
    }

    private void onCreateView() {

        productName = findViewById(R.id.product_name);
        productId = findViewById(R.id.product_id);
        productCategorySpinner = findViewById(R.id.product_category);
        productSize = findViewById(R.id.product_size);
        productDescription = findViewById(R.id.product_description);
        productQuantity = findViewById(R.id.product_quantity);
        productPrice = findViewById(R.id.product_price);
        productImage = findViewById(R.id.product_image);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });
        add = findViewById(R.id.product_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    productId.setText(UUID.randomUUID().toString());
                }
                String ProductName = productName.getText().toString();
                String ProductId = productId.getText().toString();
                String ProductCategory = productCategory;
                String ProductSize = productSize.getText().toString();
                String ProductDescription = productDescription.getText().toString();
                String ProductQuantity = productQuantity.getText().toString();
                String ProductPrice = productPrice.getText().toString();
                ProductTable productTable = new ProductTable();
                productTable.setProductName(ProductName);
                productTable.setProductId(ProductId);
                productTable.setProductCategory(ProductCategory);
                productTable.setProductSize(Integer.parseInt(ProductSize));
                productTable.setProductDescription(ProductDescription);
                productTable.setProductQuantity(ProductQuantity);
                productTable.setProductPrice(ProductPrice);
                productTable.setProductImage(imagePath);
                if (TextUtils.isEmpty(ProductName)) {
                    productName.setError("Product Name is Required");
                    productName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(ProductId)) {
                    productId.setError("Product Id is Required");
                    productId.requestFocus();
                    return;
                }


                if (TextUtils.isEmpty(ProductSize)) {
                    productSize.setError("Product Size is Required");
                    productSize.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(ProductQuantity)) {
                    productQuantity.setError("Product Quantity is Required");
                    productQuantity.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(ProductPrice)) {
                    productPrice.setError("Product Price is Required");
                    productPrice.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(ProductDescription)) {
                    productDescription.setError("Product Description is Required");
                    productDescription.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(imagePath)) {
                    Toast.makeText(AddProduct.this, "You must attach an image", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddProduct.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("current", productTable.getProductId());
                editor.apply();
                if (!isEdit) {
                    kuDatabase.productDao().addproduct(productTable);
                    Toast.makeText(AddProduct.this, "" + kuDatabase.productDao().getAllProduct().size(), Toast.LENGTH_LONG).show();
                    openDialog();
                } else {
                    kuDatabase.productDao().updateProduct(productTable);
                }

            }
        });
    }

    public void openDialog() {
        ShowDialog showDialog = new ShowDialog();
        showDialog.show(getSupportFragmentManager(), "Dialog");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            File imgFile = new File(image.getPath());
            if (imgFile.exists()) {
                Uri imageUri = Uri.fromFile(imgFile);
                productImage.setImageURI(imageUri);
                imagePath = image.getPath();
                Log.e("LTAG", image.getPath());
            } else
                finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImagePicker() {
        ImagePicker.create(this)
                .single()
                .folderMode(true)
                .toolbarImageTitle("Tap to select image")
                .returnMode(ReturnMode.CAMERA_ONLY)
                .start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if (!isEdit)
            inflater.inflate(R.menu.edit_menu, menu);
        else
            inflater.inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_update) {
            Intent intent = new Intent(this, ProductHome.class);
            intent.putExtra("isEdit", true);
            startActivity(intent);
        } else if (id == R.id.product_update) {
            add.performClick();
        } else if (id == R.id.product_delete) {
            kuDatabase.productDao().deleteProduct(productTable);
            Toast.makeText(this, "product deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
