package com.example.susach.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.susach.R;
import com.example.susach.models.Quiz;

public class EditQuestionDialogFragment extends DialogFragment {
    public interface EditQuestionListener {
        void onSaveQuestion(Quiz quiz, boolean isEdit);
    }

    private EditText edtQuestionContent, edtAnswer1, edtAnswer2, edtAnswer3, edtAnswer4, edtCorrectIndex;
    private Button btnSave, btnCancel;
    private Quiz quiz;
    private boolean isEdit = false;
    private EditQuestionListener listener;

    public static EditQuestionDialogFragment newInstance(Quiz quiz) {
        EditQuestionDialogFragment fragment = new EditQuestionDialogFragment();
        Bundle args = new Bundle();
        if (quiz != null) {
            args.putString("question", quiz.getQuestion());
            args.putString("answer1", quiz.getAnswer1());
            args.putString("answer2", quiz.getAnswer2());
            args.putString("answer3", quiz.getAnswer3());
            args.putString("answer4", quiz.getAnswer4());
            args.putInt("correct", quiz.getCorrect());
            fragment.setArguments(args);
            fragment.isEdit = true;
        }
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditQuestionListener) {
            listener = (EditQuestionListener) context;
        } else if (getParentFragment() instanceof EditQuestionListener) {
            listener = (EditQuestionListener) getParentFragment();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_question, container, false);
        bindingView(view);
        bindingAction();
        loadData();
        return view;
    }

    private void bindingView(View view) {
        edtQuestionContent = view.findViewById(R.id.edtQuestionContent);
        edtAnswer1 = view.findViewById(R.id.edtAnswer1);
        edtAnswer2 = view.findViewById(R.id.edtAnswer2);
        edtAnswer3 = view.findViewById(R.id.edtAnswer3);
        edtAnswer4 = view.findViewById(R.id.edtAnswer4);
        edtCorrectIndex = view.findViewById(R.id.edtCorrectIndex);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void bindingAction() {
        btnSave.setOnClickListener(this::onBtnSaveClick);
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void loadData() {
        if (getArguments() != null) {
            edtQuestionContent.setText(getArguments().getString("question", ""));
            edtAnswer1.setText(getArguments().getString("answer1", ""));
            edtAnswer2.setText(getArguments().getString("answer2", ""));
            edtAnswer3.setText(getArguments().getString("answer3", ""));
            edtAnswer4.setText(getArguments().getString("answer4", ""));
            int correct = getArguments().getInt("correct", 1);
            edtCorrectIndex.setText(String.valueOf(correct));
        }
    }

    private void onBtnSaveClick(View v) {
        String question = edtQuestionContent.getText().toString().trim();
        String answer1 = edtAnswer1.getText().toString().trim();
        String answer2 = edtAnswer2.getText().toString().trim();
        String answer3 = edtAnswer3.getText().toString().trim();
        String answer4 = edtAnswer4.getText().toString().trim();
        String correctStr = edtCorrectIndex.getText().toString().trim();
        if (question.isEmpty() || answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty() || answer4.isEmpty() || correctStr.isEmpty()) {
            return;
        }
        int correct = 1;
        try {
            correct = Integer.parseInt(correctStr);
            if (correct < 1 || correct > 4) correct = 1;
        } catch (NumberFormatException e) {
            correct = 1;
        }
        Quiz quiz = new Quiz(question, answer1, answer2, answer3, answer4, correct);
        if (listener != null) listener.onSaveQuestion(quiz, isEdit);
        dismiss();
    }
} 