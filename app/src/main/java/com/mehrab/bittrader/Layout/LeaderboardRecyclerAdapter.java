package com.mehrab.bittrader.Layout;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mehrab.bittrader.LeaderboardActivity;
import com.mehrab.bittrader.R;
import com.mehrab.bittrader.User.UserInformation;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.MyViewHolder> {
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    List<UserInformation> users_;

    public LeaderboardRecyclerAdapter(List<UserInformation> users) {
        users_ = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(textView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String text = users_.get(position).username_ + ", Account Value: " + DF.format(users_.get(position).accountValue_);
        holder.item_.setText(text);
    }

    @Override
    public int getItemCount() {
        return users_.size();
    }

    // Specify textview as item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_;
        MyViewHolder(TextView item) {
            super(item);
            item_ = item;
        }
    }
}
