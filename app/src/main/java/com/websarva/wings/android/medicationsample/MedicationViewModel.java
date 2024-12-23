package com.websarva.wings.android.medicationsample;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;



public class MedicationViewModel extends ViewModel {
    private MedicationDao medicationDao;
    private LiveData<List<Medication>> medicationList;

    public MedicationViewModel(MedicationDao medicationDao) {
        this.medicationDao = medicationDao;
        medicationList = medicationDao.getAllMedicationsByCreationDate();
    }

    public LiveData<List<Medication>> getMedicationList() {
        return medicationList;
    }

    public void insertMedication(Medication medication, Runnable onSuccess, Runnable onFailure) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                medicationDao.insertMedication(medication);
//                onSuccess.run();  // 保存成功時のコールバック
//            } catch (Exception e) {
//                onFailure.run();  // 保存失敗時のコールバック
//            }
//        }
    }
}
