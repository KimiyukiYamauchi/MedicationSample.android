package com.websarva.wings.android.medicationsample.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.websarva.wings.android.medicationsample.Medication;
import com.websarva.wings.android.medicationsample.MedicationDao;
import com.websarva.wings.android.medicationsample.R;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {
    private List<Medication> medicationList;
    private final MedicationDao medicationDao;
    private final Context context;

    public MedicationAdapter(List<Medication> medicationList, MedicationDao medicationDao, Context context) {
        this.medicationList = medicationList;
        this.medicationDao = medicationDao;
        this.context = context;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);
        holder.medicationName.setText(medication.name);
        holder.medicationDate.setText(medication.getFormattedCreationDate());

        // 削除ボタンのクリックリスナー
        holder.deleteButton.setOnClickListener(
                v -> showDeleteConfirmationDialog(medication, position)
        );

        // 編集ボタンのクリックリスナー
        holder.editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("medicationId", medication.id);  // IDを渡す
            Navigation.findNavController(v)
                    .navigate(R.id.action_medicationListFragment_to_editMedicationFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    private void showDeleteConfirmationDialog(Medication medication, int position) {
        new AlertDialog.Builder(context)
                .setTitle("削除確認")
                .setMessage("このデータを削除してもよろしいですか？")
                .setPositiveButton("OK", (dialog, which) -> deleteMedication(medication, position))
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void deleteMedication(Medication medication, int position) {
        new Thread(() -> {
            medicationDao.deleteMedication(medication);
            ((FragmentActivity) context).runOnUiThread(() -> {
                medicationList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "データが削除されました", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView medicationName, medicationDate;
        Button deleteButton, editButton;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medication_name);
            medicationDate = itemView.findViewById(R.id.medication_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
