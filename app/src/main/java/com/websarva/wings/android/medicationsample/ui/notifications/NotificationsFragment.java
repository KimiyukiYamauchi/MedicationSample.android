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

import com.websarva.wings.android.medicationsample.AppDatabase;
import com.websarva.wings.android.medicationsample.HealthCare;
import com.websarva.wings.android.medicationsample.HealthCareDao;
import com.websarva.wings.android.medicationsample.Medication;
import com.websarva.wings.android.medicationsample.MedicationDao;
import com.websarva.wings.android.medicationsample.R;
import com.websarva.wings.android.medicationsample.databinding.FragmentNotificationsBinding;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private AppDatabase db;
    private MedicationDao medicationDao;
    private FragmentNotificationsBinding binding;

    private EditText medicationNameInput;
    private EditText medicationDosageInput;
    private EditText medicationFrequencyInput;
    private EditText medicationStartDateInput;
    private EditText medicationEndDateInput;
    private Switch medicationReminderInput;
    private TextView medicationList;

    private HealthCareDao healthCareDao;
    private EditText healthcaretemperatureInput;
    private EditText healthcarepressureInput;
    private EditText healthcareweightInput;
    private EditText healthcaresugerInput;
    private TextView healthcareList;

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

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // データの取得
//        List<Medication> medicationList = medicationDao.getAllMedications();
        // ここでデータを表示する処理を行う
        displayMedications();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // MedicationのUI要素の取得
        medicationNameInput = view.findViewById(R.id.medication_name);
        medicationDosageInput = view.findViewById(R.id.medication_dosage);
        medicationFrequencyInput = view.findViewById(R.id.medication_frequency);
        medicationStartDateInput = view.findViewById(R.id.medication_startdate);
        medicationEndDateInput = view.findViewById(R.id.medication_enddate);
        medicationReminderInput = view.findViewById(R.id.medication_reminder);
        Button saveButton = view.findViewById(R.id.medication_save_button);
        medicationList = view.findViewById(R.id.medication_list);

        // HealthCareのUI要素の取得
        healthcaretemperatureInput = view.findViewById(R.id.healthcare_temperature);
        healthcarepressureInput = view.findViewById(R.id.healthcare_pressure);
        healthcareweightInput = view.findViewById(R.id.healthcare_weight);
        healthcaresugerInput = view.findViewById(R.id.healthcare_sugar);
        Button hc_saveButton = view.findViewById(R.id.healthcare_save_button);
        healthcareList = view.findViewById(R.id.healthcare_list);

        // 薬・体調データベースとDAOの取得
        AppDatabase db = AppDatabase.getDatabase(getActivity());
        medicationDao = db.medicationDao();
        healthCareDao = db.healthCareDao();


        // 服用開始日入力欄をクリックしたときにDatePickerを表示
//        medicationStartDateInput.setOnClickListener(v -> showDatePickerDialog(medicationStartDateInput));

        // 服用終了日入力欄をクリックしたときにDatePickerを表示
//        medicationEndDateInput.setOnClickListener(v -> showDatePickerDialog(medicationEndDateInput));

        // 薬の保存ボタンのクリックリスナー
        saveButton.setOnClickListener(v -> saveMedication());


        // 健康管理の保存ボタンのクリックリスナー
//        hc_saveButton.setOnClickListener(v -> saveHealthCare());

        // 保存された薬のリストを表示
        displayMedications();

        // 保存された健康管理のリストを表示
//        displayHealthCares();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void saveMedication() {
        // 名前、服用量、服用回数,服用開始日と終了日の入力値を取得
        String name = medicationNameInput.getText().toString();
        int dosage = Integer.parseInt(medicationDosageInput.getText().toString());
        int frequency = Integer.parseInt(medicationFrequencyInput.getText().toString());
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
//            runOnUiThread(this::displayMedications);  // メインスレッドでリストを更新
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::displayMedications);
            }
        }).start();


    }

    private void saveHealthCare() {
        //　体温、血圧、体重、血糖値の入力値を取得
        double temperature = Double.parseDouble(healthcaretemperatureInput.getText().toString());
        int pressure = Integer.parseInt(healthcarepressureInput.getText().toString());
        double weight = Double.parseDouble(healthcareweightInput.getText().toString());
        int sugar = Integer.parseInt(healthcaresugerInput.getText().toString());

        // HealthCare オブジェクトを作成して保存
        HealthCare healthcare = new HealthCare();
        healthcare.temperature = temperature;
        healthcare.pressure = pressure;
        healthcare.weight = weight;
        healthcare.sugar = sugar;

        // データベースに健康管理情報を挿入（バックグラウンドスレッドで処理）
        new Thread(() -> {
            healthCareDao.insertHealthCare(healthcare);
//            runOnUiThread(this::displayMedications);  // メインスレッドでリストを更新
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
    }

    private void displayHealthCares() {         //ヘルスケアのデータを取得し画面に表示
        // データベースからすべての健康管理情報を取得（バックグラウンドスレッド）
        new Thread(() -> {
            List<HealthCare> healthCare = healthCareDao.getAllHealthCare();
            StringBuilder displayText = new StringBuilder();
            for (HealthCare healthcare : healthCare) {
                displayText.append("体調: ").append(healthcare.temperature)
                        .append(", 血圧: ").append(healthcare.pressure).append("\n")
                        .append(", 体重: ").append(healthcare.weight).append("\n")
                        .append(", 血糖値: ").append(healthcare.sugar).append("\n");

            }
//            runOnUiThread(() -> healthcareList.setText(displayText.toString()));
        }).start();
    }


}