package com.websarva.wings.android.medicationsample;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.websarva.wings.android.medicationsample.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MedicationDao medicationDao;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // MedicationのUI要素の取得
        medicationNameInput = findViewById(R.id.medication_name);
        medicationDosageInput = findViewById(R.id.medication_dosage);
        medicationFrequencyInput = findViewById(R.id.medication_frequency);
        medicationStartDateInput = findViewById(R.id.medication_startdate);
        medicationEndDateInput = findViewById(R.id.medication_enddate);
        medicationReminderInput = findViewById(R.id.medication_reminder);
        Button saveButton = findViewById(R.id.medication_save_button);
        medicationList = findViewById(R.id.medication_list);

        // HealthCareのUI要素の取得
        healthcaretemperatureInput = findViewById(R.id.healthcare_temperature);
        healthcarepressureInput = findViewById(R.id.healthcare_pressure);
        healthcareweightInput = findViewById(R.id.healthcare_weight);
        healthcaresugerInput = findViewById(R.id.healthcare_sugar);
        Button hc_saveButton = findViewById(R.id.healthcare_save_button);
        healthcareList = findViewById(R.id.healthcare_list);

        // 薬・体調データベースとDAOの取得
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        medicationDao = db.medicationDao();
        healthCareDao = db.healthCareDao();


        // 服用開始日入力欄をクリックしたときにDatePickerを表示
        medicationStartDateInput.setOnClickListener(v -> showDatePickerDialog(medicationStartDateInput));

        // 服用終了日入力欄をクリックしたときにDatePickerを表示
        medicationEndDateInput.setOnClickListener(v -> showDatePickerDialog(medicationEndDateInput));

        // 薬の保存ボタンのクリックリスナー
        saveButton.setOnClickListener(v -> saveMedication());


        // 健康管理の保存ボタンのクリックリスナー
        hc_saveButton.setOnClickListener(v -> saveHealthCare());

        // 保存された薬のリストを表示
        displayMedications();

        // 保存された健康管理のリストを表示
        displayHealthCares();

    }


    private void saveMedication() {
        // 名前、服用量、服用回数,服用開始日と終了日の入力値を取得
        String name = medicationNameInput.getText().toString();
        int dosage = Integer.parseInt(medicationDosageInput.getText().toString());
        int frequency = Integer.parseInt(medicationFrequencyInput.getText().toString());
        String startDateStr = medicationStartDateInput.getText().toString();
        String endDateStr = medicationEndDateInput.getText().toString();

        // 日付をタイムスタンプ(long)に変換
        long startDate = convertDateToTimestamp(startDateStr);
        long endDate = convertDateToTimestamp(endDateStr);

        // リマインダーのチェック状態を取得
        boolean reminder = medicationReminderInput.isChecked();

        // Medication オブジェクトを作成して保存
        Medication medication = new Medication();
        medication.name = name;
        medication.dosage = dosage;
        medication.frequency = frequency;
        medication.startdate = startDate;
        medication.enddate = endDate;
        medication.reminder = reminder;  // リマインダー設定

        // データベースに薬情報を挿入（バックグラウンドスレッドで処理）
        new Thread(() -> {
            medicationDao.insertMedication(medication);
            runOnUiThread(this::displayMedications);  // メインスレッドでリストを更新
        }).start();


    }

    // 日付文字列をタイムスタンプ(long)に変換するヘルパーメソッド
    private long convertDateToTimestamp(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : 0;  // タイムスタンプ (ミリ秒)
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;  // エラーハンドリングが必要
        }
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
            runOnUiThread(this::displayMedications);  // メインスレッドでリストを更新
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
            runOnUiThread(() -> medicationList.setText(displayText.toString()));
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
            runOnUiThread(() -> healthcareList.setText(displayText.toString()));
        }).start();
    }

    // DatePickerDialogを表示し、選択した日付をEditTextにセットする
    private void showDatePickerDialog(EditText dateInput) {
        // 現在の日付を取得
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialogを表示
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // 選択された日付を "yyyy/MM/dd" の形式でEditTextにセット
            String selectedDate = year1 + "/" + (month1 + 1) + "/" + dayOfMonth;
            dateInput.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}