package com.example.coinage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinage.models.Transaction;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    public static final String TAG = "TransactionsAdapter";

    private Context context;
    private List<Transaction> transactions;

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
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
        holder.bind(transaction);
    }

    // Clean all elements of the recycler
    public void clear() {
        transactions.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvAmount;
        private TextView tvCategory;
        private TextView tvDescription;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(this::onClick);
        }

        public void bind(Transaction transaction) {
            // Bind the post data to the view elements
            tvDate.setText(transaction.getDate().toString());
            tvAmount.setText("$"+transaction.getAmount().toString());
            tvCategory.setText(transaction.getCategory());
            tvDescription.setText(transaction.getDescription());
        }

        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position
                Transaction transaction = transactions.get(position);
                Log.i(TAG, "got transaction");
            }
        }
    }
}
