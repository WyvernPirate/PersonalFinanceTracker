
package com.example.personalfinancetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private Context context;
    private String currencySymbol;

    public TransactionAdapter(Context context, List<Transaction> transactions, String currencySymbol) {
        this.context = context;
        this.transactions = transactions;
        this.currencySymbol = currencySymbol;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.categoryTextView.setText(transaction.getCategory());
        holder.amountTextView.setText(currencySymbol + String.format("%.2f", transaction.getAmount()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        holder.dateTextView.setText(dateFormat.format(transaction.getDate()));

        holder.notesTextView.setText(transaction.getNotes());
        if (transaction.getNotes().isEmpty()) {
            holder.notesTextView.setVisibility(View.GONE);
        } else {
            holder.notesTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView amountTextView;
        TextView dateTextView;
        TextView notesTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.text_transaction_category);
            amountTextView = itemView.findViewById(R.id.text_transaction_amount);
            dateTextView = itemView.findViewById(R.id.text_transaction_date);
            notesTextView = itemView.findViewById(R.id.text_transaction_notes);
        }
    }
}
