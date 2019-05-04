package com.example.kugrocery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kugrocery.RoomDatabase.KuDatabase;
import com.example.kugrocery.RoomDatabase.ProductTable;
import com.example.kugrocery.adapters.ProductAdapter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;

public class CartDialogFragment extends DialogFragment {

    ArrayList<ProductTable> productTables;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    ImageButton checkout;
    Bundle bundle;
    ProductListener.CheckoutListener checkoutListener;
    KuDatabase kuDatabase;

    public static CartDialogFragment NewInstance(Bundle bundle) {
        CartDialogFragment cartDialogFragment = new CartDialogFragment();
        cartDialogFragment.setArguments(bundle);
        return cartDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cart_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kuDatabase = Room.databaseBuilder(getContext(), KuDatabase.class, "kudb")
                .allowMainThreadQueries()
                .build();
        productAdapter = new ProductAdapter(getContext(), true);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        checkout = view.findViewById(R.id.checkout_button);
        checkout.setOnClickListener(v -> {
            int sum = 0;
            for (ProductTable productTable : productTables) {
                if (productTable.getQuantityLeaving() < 1) {
                    productTables.remove(productTable);
                    continue;
                }
                sum += productTable.getQuantityLeaving();
            }
            if (sum < 1) {
                Toast.makeText(getContext(), "All Items have been removed from cart", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.checkout_form, null, false);

            DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String customerFirstName = ((EditText) dialogView.findViewById(R.id.customer_firstname)).getText().toString();
                    String customerLastName = ((EditText) dialogView.findViewById(R.id.customer_lastname)).getText().toString();
                    String customerEmail = ((EditText) dialogView.findViewById(R.id.customer_email)).getText().toString();
                    String customerPhone = ((EditText) dialogView.findViewById(R.id.customer_phone)).getText().toString();
                    if (!allFieldsFilled(customerFirstName, customerEmail, customerLastName, customerPhone)) {
                        Toast.makeText(getContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        ProductTable productTables1[] = new ProductTable[productTables.size()];
                        int i = 0;
                        for (ProductTable productTable : productTables) {
                            productTables1[i] = productTable;
                            i++;
                            int productQty = Integer.parseInt(productTable.getProductQuantity());
                            productTable.setProductQuantity(String.valueOf(productQty - productTable.getQuantityLeaving()));
                        }
                        CustomerDetails customerDetails = new CustomerDetails(customerFirstName, customerLastName, customerEmail, customerPhone);
                        kuDatabase.productDao().batchUpdateProducts(productTables1);
                        checkoutListener.onCheckOut(productTables, customerDetails);
                        getDialog().cancel();
                    }
                }
            };
            builder.setView(dialogView)
                    .setTitle("Enter Customer Details")
                    .setCancelable(false)
                    .setPositiveButton("Ok", onClickListener);
            builder.show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setAdapter(productAdapter);
        bundle = getArguments();
        productTables = Parcels.unwrap(bundle.getParcelable("cart"));
        if (productTables == null) {
            getDialog().dismiss();
            return;
        }
        productAdapter.swapList(productTables);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        checkoutListener = (ProductListener.CheckoutListener) context;

    }

    private boolean allFieldsFilled(String... fields) {
        for (String s : fields) {
            if (TextUtils.isEmpty(s))
                return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
