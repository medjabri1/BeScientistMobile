package com.example.bescientist.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bescientist.Classes.Reviewer;
import com.example.bescientist.R;

import java.util.List;

public class ChooseReviewerAdapter extends RecyclerView.Adapter<ChooseReviewerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Reviewer> mData;

    public ChooseReviewerAdapter(Context mContext, List<Reviewer> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_reviewer, parent, false);

        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_id.setText(String.valueOf(mData.get(position).getId()));
        holder.tv_name.setText(mData.get(position).getName());
        holder.tv_email.setText(mData.get(position).getEmail());
        holder.tv_toreview_nbr.setText("En attente : "+ String.valueOf(mData.get(position).getToReviewNbr()));

        int animationDelay = position * 400;

        holder.cardView.setAlpha(0);
        holder.cardView.setTranslationX(-300);
        holder.cardView.animate().alpha(1f).setDuration(500).setStartDelay(animationDelay).start();
        holder.cardView.animate().translationX(0).setDuration(600).setStartDelay(animationDelay).start();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id;
        private TextView tv_name;
        private TextView tv_email;
        private TextView tv_toreview_nbr;

        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            tv_id = (TextView) itemView.findViewById(R.id.reviewer_id_id);
            tv_name = (TextView) itemView.findViewById(R.id.reviewer_name_id);
            tv_email = (TextView) itemView.findViewById(R.id.reviewer_email_id);
            tv_toreview_nbr = (TextView) itemView.findViewById(R.id.reviewer_toreview_nbr_id);

            cardView = (CardView) itemView;
        }
    }
}
