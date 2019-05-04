package com.example.kugrocery.RoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {CashierTable.class,  ProductTable.class}, version = 1)
public abstract class KuDatabase extends RoomDatabase {

    public abstract CashierDao cashierDao();

    public abstract ProductDao productDao();
}
