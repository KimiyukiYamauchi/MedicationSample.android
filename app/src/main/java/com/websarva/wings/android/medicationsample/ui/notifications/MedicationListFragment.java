package com.websarva.wings.android.medicationsample.ui.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.websarva.wings.android.medicationsample.AppDatabase;
import com.websarva.wings.android.medicationsample.MedicationDao;
import com.websarva.wings.android.medicationsample.MedicationViewModel;
import com.websarva.wings.android.medicationsample.R;

/**
 * A fragment representing a list of Items.
 */
public class MedicationListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private AppDatabase db;
    private MedicationDao medicationDao; // データベースアクセス用
    MedicationViewModel viewModel;
    RecyclerView recyclerView;
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

        // Database インスタンスを取得し、ViewModel を設定
        db = AppDatabase.getDatabase(requireContext());
        medicationDao = db.medicationDao();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MedicationViewModel(medicationDao);
            }
        }).get(MedicationViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medication_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // LiveData を観察してデータが変わったらアダプターにセット
        viewModel.getMedicationList().observe(getViewLifecycleOwner(), medicationList -> {
            adapter = new MedicationAdapter(medicationList);
            recyclerView.setAdapter(adapter);
        });

        // ボタンクリックで画面遷移
        view.findViewById(R.id.button_to_notifications).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_medicationListFragment_to_notificationsFragment);
        });
    }
}