package com.websarva.wings.android.medicationsample;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HealthCare {
    @PrimaryKey(autoGenerate = true)
    public int id;                  //自動生成されるID
    public int entrydate;           //登録日
    public double temperature;        //体温
    public int pressure;            //血圧
    public double weight;              //体重
    public int sugar;
}
