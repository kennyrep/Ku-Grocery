package com.example.kugrocery.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.kugrocery.adapters.ProductAdapter;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    void addproduct(ProductTable productTable);

    /**
     * @return
     */
    @Query("SELECT * FROM ProductTable")
    List<ProductTable> getAllProduct();

    @Query("SELECT * FROM ProductTable WHERE productCategory=:productCategory AND productSize=:productSize")
    List<ProductTable> getAllProductBySizeAndCategory(String productCategory, int productSize);

    @Query("SELECT * FROM ProductTable WHERE productCategory=:productCategory")
    List<ProductTable> getAllProductByCategory(String productCategory);

    @Delete
    void deleteProduct(ProductTable productTable);

    @Update
    int updateProduct(ProductTable productTable);

    @Update
    void batchUpdateProducts(ProductTable... productTables);
}
