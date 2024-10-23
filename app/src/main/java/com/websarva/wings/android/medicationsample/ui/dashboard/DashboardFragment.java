package com.websarva.wings.android.medicationsample.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.websarva.wings.android.medicationsample.AppDatabase;
import com.websarva.wings.android.medicationsample.HealthCare;
import com.websarva.wings.android.medicationsample.HealthCareDao;
import com.websarva.wings.android.medicationsample.R;
import com.websarva.wings.android.medicationsample.databinding.FragmentDashboardBinding;

import java.util.List;

public class DashboardFragment extends Fragment {

    private AppDatabase db;
    private HealthCareDao healthCareDao;
    private FragmentDashboardBinding binding;
    ;
    private EditText healthcaretemperatureInput;
    private EditText healthcarepressureInput;
    private EditText healthcareweightInput;
    private EditText healthcaresugerInput;
    private TextView healthcareList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(requireContext());
        healthCareDao = db.healthCareDao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //データの取得
//        List<HealthCare> healthCareList = healthCareDao.getAllHealthCare();
        //ここでデータを表示する処理を行う
        displayHealthCares();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // HealthCareのUI要素の取得
        healthcaretemperatureInput = view.findViewById(R.id.healthcare_temperature);
        healthcarepressureInput = view.findViewById(R.id.healthcare_pressure);
        healthcareweightInput = view.findViewById(R.id.healthcare_weight);
        healthcaresugerInput = view.findViewById(R.id.healthcare_sugar);
        Button hc_saveButton = view.findViewById(R.id.healthcare_save_button);
        healthcareList = view.findViewById(R.id.healthcare_list);

        // 体調データベースとDAOの取得
        AppDatabase db = AppDatabase.getDatabase(getActivity());
        healthCareDao = db.healthCareDao();

        // 健康管理の保存ボタンのクリックリスナー
        hc_saveButton.setOnClickListener(v -> saveHealthCare());

        // 保存された健康管理のリストを表示
        displayHealthCares();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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