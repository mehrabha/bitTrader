package com.mehrab.bittrader.Layout;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mehrab.bittrader.R;
import com.mehrab.bittrader.User.Transaction;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private static final DecimalFormat BTC_DF = new DecimalFormat("0.0000");

    private List<Transaction> transactions_;

    public RecyclerAdapter(List<Transaction> transactions) {
        transactions_ = transactions;
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
        Transaction transaction = transactions_.get(position);
        String text = transaction.type_ + " " + BTC_DF.format(transaction.amount_);
        text += " at " + DF.format(transaction.btc_price_) + "/BTC";
        holder.item_.setText(text);
    }

    @Override
    public int getItemCount() {
        return transactions_.size();
    }

    // Specify textview as item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_;
        MyViewHolder(TextView item) {
            super(item);
            item_ = item;
        }
    }
}
