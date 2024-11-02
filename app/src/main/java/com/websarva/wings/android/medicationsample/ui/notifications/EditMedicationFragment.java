package com.websarva.wings.android.medicationsample.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.websarva.wings.android.medicationsample.AppDatabase;
import com.websarva.wings.android.medicationsample.Medication;
import com.websarva.wings.android.medicationsample.MedicationDao;
import com.websarva.wings.android.medicationsample.R;

public class EditMedicationFragment extends Fragment {

    private EditText medicationNameInput, medicationDosageInput, medicationFrequencyInput;
    private Switch medicationReminderInput;
    private MedicationDao medicationDao;
    private Medication currentMedication;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        medicationDao = AppDatabase.getDatabase(requireContext()).medicationDao();
        medicationNameInput = view.findViewById(R.id.medication_name);
        medicationDosageInput = view.findViewById(R.id.medication_dosage);
        medicationFrequencyInput = view.findViewById(R.id.medication_frequency);
        medicationReminderInput = view.findViewById(R.id.medication_reminder);
        Button saveButton = view.findViewById(R.id.medication_save_button);

        int medicationId = getArguments().getInt("medicationId", -1);

        // IDからMedicationを取得し、各フィールドに設定
        new Thread(() -> {
            currentMedication = medicationDao.getMedicationById(medicationId);
            if (currentMedication != null) {
                getActivity().runOnUiThread(() -> {
                    medicationNameInput.setText(currentMedication.name);
                    medicationDosageInput.setText(String.valueOf(currentMedication.dosage));
                    medicationFrequencyInput.setText(String.valueOf(currentMedication.frequency));
                    medicationReminderInput.setChecked(currentMedication.reminder);
                });
            }
        }).start();

        // 保存ボタンでデータを更新
        saveButton.setOnClickListener(v -> {
            Log.d("EditMedicationFragment", "EditMedicationFragment saveButton.setOnClickListener");
            String name = medicationNameInput.getText().toString();
            int dosage = Integer.parseInt(medicationDosageInput.getText().toString());
            int frequency = Integer.parseInt(medicationFrequencyInput.getText().toString());
            boolean reminder = medicationReminderInput.isChecked();

            new Thread(() -> {
                currentMedication.name = name;
                currentMedication.dosage = dosage;
                currentMedication.frequency = frequency;
                currentMedication.reminder = reminder;
                medicationDao.updateMedication(currentMedication);
            }).start();

            getActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
