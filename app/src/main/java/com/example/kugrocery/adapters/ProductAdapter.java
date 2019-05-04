package com.example.kugrocery.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kugrocery.ProductListener;
import com.example.kugrocery.R;
import com.example.kugrocery.RoomDatabase.ProductTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductTable> productTableList;
    private ProductListener.ProductClickListener productListener;
    private Context context;
    private boolean isCart = false;

    public ProductAdapter(Context context) {
        this.productTableList = new ArrayList<>();
        this.productListener = (ProductListener.ProductClickListener) context;
        this.context = context;
    }

    public ProductAdapter(Context context, boolean isCart) {
        this.productTableList = new ArrayList<>();
        this.context = context;
        this.isCart = isCart;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (isCart) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item, viewGroup, false);
        }

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {
        ProductTable productTable = productTableList.get(i);
        productViewHolder.bind(productTable);
    }

    @Override
    public int getItemCount() {
        return productTableList.size();
    }

    public void swapList(List<ProductTable> productTables) {
        this.productTableList = productTables;
        notifyDataSetChanged();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productAmountTextView;
        TextView productSizeTextView;
        View outOfStockOverlay;
        TextView outOfStockTextView;
        TextView itemQuantity;
        ImageButton addQuantity;
        ImageButton minusQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productAmountTextView = itemView.findViewById(R.id.product_price);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productSizeTextView = itemView.findViewById(R.id.product_size);
            outOfStockOverlay = itemView.findViewById(R.id.product_fade);
            outOfStockTextView = itemView.findViewById(R.id.out);
            if (isCart) {
                itemQuantity = itemView.findViewById(R.id.product_quantity);
                addQuantity = itemView.findViewById(R.id.add);
                minusQuantity = itemView.findViewById(R.id.minus);
            }
        }

        public void bind(ProductTable productTable) {
            if (isCart) {
                outOfStockOverlay.setVisibility(View.GONE);
                outOfStockTextView.setVisibility(View.VISIBLE);
                productTable.setQuantityLeaving(1);
                productTable.setTotalAmount(productTable.getQuantityLeaving() * Integer.parseInt(productTable.getProductPrice()));
                outOfStockTextView.setText("\u0024 " + productTable.getTotalAmount());
                addQuantity.setOnClickListener(v -> {
                    int q = productTable.getQuantityLeaving();
                    if (q >= Integer.parseInt(productTable.getProductQuantity()))
                        return;
                    productTable.setQuantityLeaving(q + 1);
                    productTable.setTotalAmount(productTable.getQuantityLeaving() * Integer.parseInt(productTable.getProductPrice()));
                    itemQuantity.setText("" + productTable.getQuantityLeaving());
                    String totalprice = "\u0024 " + productTable.getTotalAmount();
                    Log.e("TAG", totalprice);
                    outOfStockTextView.setText(totalprice);
                });
                minusQuantity.setOnClickListener(v -> {
                    int q = Integer.parseInt(itemQuantity.getText().toString());
                    if (q <= 0) {
                        Toast.makeText(context, "Item " + productTable.getProductName() + " Will be removed at checkout", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ((q - 1) == 0) {
                        Toast.makeText(context, "Item " + productTable.getProductName() + " Will be removed at checkout", Toast.LENGTH_SHORT).show();
                    }
                    productTable.setQuantityLeaving(q - 1);
                    productTable.setTotalAmount(productTable.getQuantityLeaving() * Integer.parseInt(productTable.getProductPrice()));
                    itemQuantity.setText("" + productTable.getQuantityLeaving());
                    String totalprice = "\u0024 " + productTable.getTotalAmount();
                    outOfStockTextView.setText(totalprice);
                    Log.e("TAG", totalprice);
                });
            }
            productSizeTextView.setText(String.format(context.getString(R.string.product_size), productTable.getProductSize()));
            productNameTextView.setText(productTable.getProductName());
            String price = "\u0024 " + productTable.getProductPrice();
            productAmountTextView.setText(price);
            File file = new File(productTable.getProductImage());
            if (file.exists()) {
                productImageView.setImageURI(Uri.fromFile(file));
            } else {
                productImageView.setImageResource(R.drawable.product);
            }
            if (!isCart) {
                if (Integer.parseInt(productTable.getProductQuantity()) > 1) {
                    outOfStockTextView.setVisibility(View.GONE);
                    outOfStockOverlay.setVisibility(View.GONE);
                } else {
                    outOfStockTextView.setVisibility(View.VISIBLE);
                    outOfStockOverlay.setVisibility(View.VISIBLE);
                }
                itemView.setOnClickListener(v -> {
                    productListener.onProductItemClicked(productTable);
                });
            }
        }
    }
}