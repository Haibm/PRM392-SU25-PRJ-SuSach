package com.example.susach.adapters;

import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.example.susach.models.Leaderboard;
import com.example.susach.R;
import java.util.List;
import androidx.core.content.ContextCompat;
import android.content.res.ColorStateList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<Leaderboard> userList;
    private int totalQuestions;

    public LeaderboardAdapter(List<Leaderboard> userList, int totalQuestions) {
        this.userList = userList;
        this.totalQuestions = totalQuestions;
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvName;
        TextView tvEmail;
        TextView tvGrade;
        TextView tvGrade10;
        TextView tvTime;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvGrade = itemView.findViewById(R.id.tvGrade);
            tvGrade10 = itemView.findViewById(R.id.tvGrade10);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
        Leaderboard item = userList.get(position);
        
        // Set rank (position + 1)
        holder.tvRank.setText(String.valueOf(position + 1));
        
        // Set user info
        String userName = item.getName();
        holder.tvName.setText(userName);
        holder.tvEmail.setText(userName); // Email same as name for now
        
        // Set score info
        holder.tvGrade.setText(item.getGrade() + "/" + totalQuestions);
        String grade10Text = String.format("%.1f", item.getGrade10());
        holder.tvGrade10.setText(grade10Text);
        holder.tvTime.setText(item.getFormattedTime());
        
        // Set rank badge color based on position
        android.view.View cardRank = holder.itemView.findViewById(R.id.cardRank);
        if (position == 0) {
            // Gold for 1st place
            cardRank.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.gold)));
        } else if (position == 1) {
            // Silver for 2nd place
            cardRank.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.silver)));
        } else if (position == 2) {
            // Bronze for 3rd place
            cardRank.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.bronze)));
        } else {
            // Purple for others
            cardRank.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_700)));
        }
        
        // Debug log để kiểm tra dữ liệu
        android.util.Log.d("LeaderboardAdapter", "Position " + position + ": " + 
            "Name=" + item.getName() + 
            ", Grade=" + item.getGrade() + 
            ", Grade10=" + item.getGrade10() + 
            ", Time=" + item.getTotalTime() + "s");
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

