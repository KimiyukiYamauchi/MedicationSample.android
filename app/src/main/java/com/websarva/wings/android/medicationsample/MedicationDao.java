package com.websarva.wings.android.medicationsample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

    @Dao
    public interface MedicationDao {
        // 薬をデータベースに挿入
        @Insert
        void insertMedication(Medication medication);

        // 薬のすべてのレコードを取得
        @Query("SELECT * FROM Medication")
        List<Medication> getAllMedications();

        // 作成日の降順で薬を取得
        @Query("SELECT * FROM Medication ORDER BY createdAt DESC")
        LiveData<List<Medication>> getAllMedicationsByCreationDate();

        // 指定された薬を削除
        @Delete
        void deleteMedication(Medication medication);

    }
