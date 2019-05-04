package com.example.kugrocery;

import com.example.kugrocery.RoomDatabase.ProductTable;

import java.util.ArrayList;

public interface ProductListener {
    interface ProductClickListener {
        void onProductItemClicked(ProductTable productTable);
    }

    interface CheckoutListener {
        void onCheckOut(ArrayList<ProductTable> productTables, CustomerDetails customerDetails);
    }
}
