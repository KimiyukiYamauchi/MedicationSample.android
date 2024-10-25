package com.websarva.wings.android.medicationsample.ui.notifications;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.websarva.wings.android.medicationsample.AppDatabase;
import com.websarva.wings.android.medicationsample.Medication;
import com.websarva.wings.android.medicationsample.MedicationDao;
import com.websarva.wings.android.medicationsample.R;
import com.websarva.wings.android.medicationsample.ui.notifications.placeholder.PlaceholderContent;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MedicationListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private AppDatabase db;
    private MedicationDao medicationDao; // データベースアクセス用
    private MedicationAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MedicationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        db = AppDatabase.getDatabase(requireContext());
        medicationDao = db.medicationDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medication_list_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // データベースからデータを取得し、アダプターに設定
        // データベースからすべての薬情報を取得（バックグラウンドスレッド）
        new Thread(() -> {
            List<Medication> medications = medicationDao.getAllMedications();


        }).start();
        List<Medication> medicationList = medicationDao.getAllMedicationsByCreationDate();
        adapter = new MedicationAdapter(medicationList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}