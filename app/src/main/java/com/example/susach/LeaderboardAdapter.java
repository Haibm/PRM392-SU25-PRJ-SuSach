package com.example.susach;

import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<Leaderboard> userList;

    public LeaderboardAdapter(List<Leaderboard> userList) {
        this.userList = userList;
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvGrade;
        TextView tvGrade10;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvGrade = itemView.findViewById(R.id.tvGrade);
            tvGrade10 = itemView.findViewById(R.id.tvGrade10);
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
        holder.tvName.setText(item.getName());
        holder.tvGrade.setText(String.valueOf(item.getGrade()));
        holder.tvGrade10.setText(String.valueOf(item.getGrade10()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

