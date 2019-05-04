package com.example.kugrocery.RoomDatabase;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CashierDao {

    @Insert
    void addCashier(CashierTable cashierTable);

    @Query("SELECT * FROM CashierTable")
    List<CashierTable> getAllcashier();

    @Query("SELECT * FROM CashierTable WHERE user_id =:userId AND password=:password")
    CashierTable getCashierWithIdAndPassword(String userId, String password);

    @Query("SELECT * FROM CashierTable WHERE id=:id")
    CashierTable getCashierWithId(int id);
}
