package com.example.susach.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.susach.R;
import com.example.susach.models.Era;
import com.example.susach.models.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ArticleEditDialog extends DialogFragment {
    public interface OnSaveListener {
        void onSave(Event event);
    }
    private Event event;
    private OnSaveListener listener;
    private List<Era> eraList = new ArrayList<>();
    private ArrayAdapter<String> eraAdapter;

    public ArticleEditDialog(@Nullable Event event, OnSaveListener listener) {
        this.event = event;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_article_edit, null);

        EditText etId = view.findViewById(R.id.et_id);
        EditText etName = view.findViewById(R.id.et_name);
        EditText etDescription = view.findViewById(R.id.et_description);
        EditText etContents = view.findViewById(R.id.et_contents);
        EditText etStartDate = view.findViewById(R.id.et_start_date);
        EditText etEndDate = view.findViewById(R.id.et_end_date);
        EditText etImageUrl = view.findViewById(R.id.et_image_url);
        EditText etSummary = view.findViewById(R.id.et_summary);
        EditText etImageContent = view.findViewById(R.id.et_image_content);
        Spinner spinnerEra = view.findViewById(R.id.spinner_era_id);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // Load era list
        FirebaseFirestore.getInstance().collection("eras").get().addOnSuccessListener(query -> {
            eraList.clear();
            List<String> eraNames = new ArrayList<>();
            for (var doc : query) {
                Era era = doc.toObject(Era.class);
                if (era != null) {
                    era.setId(doc.getId());
                    eraList.add(era);
                    eraNames.add(era.getName());
                }
            }
            eraAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eraNames);
            eraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEra.setAdapter(eraAdapter);
            // Nếu sửa, set selection đúng era
            if (event != null) {
                for (int i = 0; i < eraList.size(); i++) {
                    if (eraList.get(i).getId().equals(event.getEraId())) {
                        spinnerEra.setSelection(i);
                        break;
                    }
                }
            }
        });

        // Nếu là sửa, điền dữ liệu vào form
        if (event != null) {
            etId.setText(event.getId());
            etName.setText(event.getName());
            etDescription.setText(event.getDescription());
            etContents.setText(event.getContents());
            etStartDate.setText(String.valueOf(event.getStartDate()));
            etEndDate.setText(String.valueOf(event.getEndDate()));
            etImageUrl.setText(event.getImageUrl());
            etSummary.setText(event.getSummary());
            etImageContent.setText(event.getImageContent());
            etId.setEnabled(false); // Không cho sửa ID khi update
        }

        btnSave.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String contents = etContents.getText().toString().trim();
            String startDateStr = etStartDate.getText().toString().trim();
            String endDateStr = etEndDate.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            String summary = etSummary.getText().toString().trim();
            String imageContent = etImageContent.getText().toString().trim();
            int eraPos = spinnerEra.getSelectedItemPosition();
            String eraId = eraPos >= 0 && eraPos < eraList.size() ? eraList.get(eraPos).getId() : "";
            int startDate, endDate;
            try {
                startDate = Integer.parseInt(startDateStr);
                endDate = Integer.parseInt(endDateStr);
            } catch (NumberFormatException e) {
                etStartDate.setError("Năm phải là số!");
                etEndDate.setError("Năm phải là số!");
                return;
            }
            Event newEvent = new Event(id, name, description, contents, startDate, endDate, imageUrl, eraId, summary, imageContent, "staff@example.com", "staff@example.com");
            listener.onSave(newEvent);
            dismiss();
        });
        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
} 