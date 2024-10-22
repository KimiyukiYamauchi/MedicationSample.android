package com.websarva.wings.android.medicationsample;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Medication.class,HealthCare.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MedicationDao medicationDao();
    public abstract HealthCareDao healthCareDao();

    // シングルトンパターンでデータベースインスタンスを取得
    private static volatile AppDatabase INSTANCE;

public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")  // 1つのデータベース名に統一
                            .fallbackToDestructiveMigration()  // マイグレーションを行わず、データを破壊
                            .build();

                }
            }
        }
        return INSTANCE;
    }

}
