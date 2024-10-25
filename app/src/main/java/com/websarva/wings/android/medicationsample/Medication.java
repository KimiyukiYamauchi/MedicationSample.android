package com.websarva.wings.android.medicationsample;

import android.icu.text.SimpleDateFormat;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Locale;

@Entity
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int id;             // 自動生成されるID
    public String name;        // 薬の名前
    public int dosage;         // 服用量
    public int frequency;      // 服用頻度
    public long startdate;     // 服用開始日（Unixタイムスタンプ）
    public long enddate;       // 服用終了日（Unixタイムスタンプ）
    public boolean reminder;   // リマインダー情報

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public long createdAt;     // 登録日時（Unixタイムスタンプ）

    // 日付をフォーマットして返すメソッド
    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(createdAt));
    }
}
