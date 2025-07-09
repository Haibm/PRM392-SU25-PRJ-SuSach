package com.example.susach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.R;
import com.example.susach.models.Quiz;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    public interface OnQuestionClickListener {
        void onQuestionClick(Quiz quiz);
        void onDeleteQuestion(Quiz quiz, int position);
    }

    private List<Quiz> questionList;
    private OnQuestionClickListener listener;

    public QuestionAdapter(List<Quiz> questionList, OnQuestionClickListener listener) {
        this.questionList = questionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        QuestionViewHolder holder = new QuestionViewHolder(view);
        bindingViewHolder(holder);
        bindingActionHolder(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Quiz quiz = questionList.get(position);
        holder.tvQuestionContent.setText(quiz.getQuestion());
        holder.itemView.setTag(quiz);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    private void bindingViewHolder(QuestionViewHolder holder) {
        holder.tvQuestionContent = holder.itemView.findViewById(R.id.tvQuestionContent);
        holder.btnDeleteQuestion = holder.itemView.findViewById(R.id.btnDeleteQuestion);
    }

    private void bindingActionHolder(QuestionViewHolder holder) {
        holder.itemView.setOnClickListener(this::onItemClick);
        holder.btnDeleteQuestion.setOnClickListener(v -> onBtnDeleteClick(holder, v));
    }

    private void onBtnDeleteClick(QuestionViewHolder holder, View v) {
        int position = holder.getAdapterPosition();
        if (listener != null && position != RecyclerView.NO_POSITION) {
            listener.onDeleteQuestion(questionList.get(position), position);
        }
    }

    private void onItemClick(View v) {
        if (listener != null) {
            Quiz quiz = (Quiz) v.getTag();
            listener.onQuestionClick(quiz);
        }
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionContent;
        android.widget.ImageButton btnDeleteQuestion;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
} 