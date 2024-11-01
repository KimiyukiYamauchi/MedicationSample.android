package com.websarva.wings.android.medicationsample.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.websarva.wings.android.medicationsample.AppDatabase;
import com.websarva.wings.android.medicationsample.Medication;
import com.websarva.wings.android.medicationsample.MedicationDao;
import com.websarva.wings.android.medicationsample.R;
import com.websarva.wings.android.medicationsample.databinding.FragmentNotificationsBinding;

import java.util.List;

public class AddMedicationFragment extends Fragment {

    private AppDatabase db;
    private MedicationDao medicationDao;
    private NavController navController;


    private FragmentNotificationsBinding binding;

    private EditText medicationNameInput;
    private EditText medicationDosageInput;
    private EditText medicationFrequencyInput;
    private EditText medicationStartDateInput;
    private EditText medicationEndDateInput;
    private Switch medicationReminderInput;
    private TextView medicationList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(requireContext());
        medicationDao = db.medicationDao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NavControllerの取得
        navController = Navigation.findNavController(view);

        // MedicationのUI要素の取得
        medicationNameInput = view.findViewById(R.id.medication_name);
        medicationDosageInput = view.findViewById(R.id.medication_dosage);
        medicationFrequencyInput = view.findViewById(R.id.medication_frequency);
        medicationStartDateInput = view.findViewById(R.id.medication_startdate);
        medicationEndDateInput = view.findViewById(R.id.medication_enddate);
        medicationReminderInput = view.findViewById(R.id.medication_reminder);
        Button saveButton = view.findViewById(R.id.medication_save_button);
        medicationList = view.findViewById(R.id.medication_list);

        // 薬データベースとDAOの取得
        AppDatabase db = AppDatabase.getDatabase(getActivity());
        medicationDao = db.medicationDao();

        // 服用開始日入力欄をクリックしたときにDatePickerを表示
//        medicationStartDateInput.setOnClickListener(v -> showDatePickerDialog(medicationStartDateInput));

        // 服用終了日入力欄をクリックしたときにDatePickerを表示
//        medicationEndDateInput.setOnClickListener(v -> showDatePickerDialog(medicationEndDateInput));

        // 薬の保存ボタンのクリックリスナー
        saveButton.setOnClickListener(v -> saveMedication());

        // 保存された薬のリストを表示
        displayMedications();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void saveMedication() {
        // 名前、服用量、服用回数,服用開始日と終了日の入力値を取得
        String name = medicationNameInput.getText().toString();
        if (name.isEmpty()) {
            name = "名前未設定";
        }
        int dosage;
        String strdosage = medicationDosageInput.getText().toString();
        if (strdosage.isEmpty()) {
            dosage = 1;
        } else {
            dosage = Integer.parseInt(strdosage);
        }
        int frequency;
        String strfrequency = medicationFrequencyInput.getText().toString();
        if (strfrequency.isEmpty()) {
            frequency = 1;
        } else {
            frequency = Integer.parseInt(strfrequency);
        }
        String startDateStr = medicationStartDateInput.getText().toString();
        String endDateStr = medicationEndDateInput.getText().toString();

        // 日付をタイムスタンプ(long)に変換
//        long startDate = convertDateToTimestamp(startDateStr);
//        long endDate = convertDateToTimestamp(endDateStr);

        // リマインダーのチェック状態を取得
        boolean reminder = medicationReminderInput.isChecked();

        // Medication オブジェクトを作成して保存
        Medication medication = new Medication();

        medication.name = name;
        medication.dosage = dosage;
        medication.frequency = frequency;
//        medication.startdate = startDate;
//        medication.enddate = endDate;
        medication.reminder = reminder;  // リマインダー設定

        // データベースに薬情報を挿入（バックグラウンドスレッドで処理）
        new Thread(() -> {
            medicationDao.insertMedication(medication);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
//                    displayMedications();
                    // 成功時にナビゲーションを実行
                    navController.navigate(R.id.navigation_notifications);
                });
            }
        }).start();


    }

    private void displayMedications() {         //薬のデータを取得し画面に表示
        // データベースからすべての薬情報を取得（バックグラウンドスレッド）
        new Thread(() -> {
            List<Medication> medications = medicationDao.getAllMedications();
            StringBuilder displayText = new StringBuilder();
            for (Medication medication : medications) {
                displayText.append("名前: ").append(medication.name)
                        .append(", 服用量: ").append(medication.dosage).append("\n")
                        .append(", 服用回数: ").append(medication.frequency).append("\n")
                        .append(", 服用開始: ").append(medication.startdate).append("\n")
                        .append(", 服用終了: ").append(medication.enddate).append("\n")
                        .append(", リマインダー: ").append(medication.reminder).append("\n");


            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> medicationList.setText(displayText.toString()));
            }

        }).start();

//        navController.navigate(R.id.navigation_notifications);
    }
}