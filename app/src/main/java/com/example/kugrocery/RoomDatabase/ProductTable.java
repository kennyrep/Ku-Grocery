package com.example.kugrocery.RoomDatabase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

@Entity
@Parcel(Parcel.Serialization.BEAN)
public class ProductTable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "productName")
    private String productName;

    @ColumnInfo(name = "productId")
    private String productId;

    @ColumnInfo(name = "productCategory")
    private String productCategory;

    @ColumnInfo(name = "productSize")
    private int productSize;

    @ColumnInfo(name = "productDescription")
    private String productDescription;

    @ColumnInfo(name = "productPrice")
    private String productPrice;


    @ColumnInfo(name = "productQuantity")
    private String productQuantity;

    @ColumnInfo(name = "productImage")
    private String productImage;

    @Ignore
    private int quantityLeaving;

    @Ignore
    private int totalAmount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public int getProductSize() {
        return productSize;
    }

    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getQuantityLeaving() {
        return quantityLeaving;
    }

    public void setQuantityLeaving(int quantityLeaving) {
        this.quantityLeaving = quantityLeaving;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
