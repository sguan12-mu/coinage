package com.example.coinage;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinage.fragments.TransactionDetailFragment;
import com.example.coinage.models.Transaction;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    public static final String TAG = "TransactionsAdapter";

    private Context context;
    private List<Transaction> transactions;
    public static final String myFormat="MM/dd/yy";
    public final SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);

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
        try {
            holder.bind(transaction);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

        public void bind(Transaction transaction) throws ParseException {
            Date date = dateFormat.parse(transaction.getDate().toString());
            tvDate.setText(transaction.getDate());
            tvAmount.setText("$"+transaction.getAmount().toString());
            tvCategory.setText(transaction.getCategory());
            tvDescription.setText(transaction.getDescription());
        }

        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Transaction transaction = transactions.get(position);
                // create bundle of transaction (intents don't work from activity to fragment)
                Fragment detailFragment = new TransactionDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Transaction.class.getSimpleName(), transaction);
                detailFragment.setArguments(bundle);
                // switch to desired fragment
                FragmentTransaction fragmentTransaction = ((AppCompatActivity)context)
                        .getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out);
                fragmentTransaction.replace(R.id.frameLayout, detailFragment);
                fragmentTransaction.addToBackStack(TransactionsAdapter.class.getSimpleName()).commit();
            }
        }
    }
}
