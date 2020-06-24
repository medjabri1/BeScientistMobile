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

import com.example.bescientist.R;
import com.example.bescientist.Classes.ArticleToVerify;

import java.util.List;

public class ArticlesToVerifyAdapter extends RecyclerView.Adapter<ArticlesToVerifyAdapter.MyViewHolder> {

    private Context mContext;
    private List<ArticleToVerify> mData;

    public ArticlesToVerifyAdapter(Context mContext, List<ArticleToVerify> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_article_to_verify, parent, false);

        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_id.setText(String.valueOf(mData.get(position).getId()));
        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_domain.setText(mData.get(position).getDomain());
        holder.tv_sent_at.setText("Envoyé le : "+ mData.get(position).getCreated_at().substring(0, mData.get(position).getCreated_at().length() - 3));

        int animationDelay = position * 200;

        holder.cardView.setAlpha(0);
        holder.cardView.setTranslationX(-300);
        holder.cardView.animate().alpha(1f).setDuration(300).setStartDelay(animationDelay).start();
        holder.cardView.animate().translationX(0).setDuration(400).setStartDelay(animationDelay).start();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id;
        private TextView tv_title;
        private TextView tv_domain;
        private TextView tv_sent_at;

        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            tv_id = (TextView) itemView.findViewById(R.id.article_to_verify_id_id);
            tv_title = (TextView) itemView.findViewById(R.id.article_to_verify_title_id);
            tv_domain = (TextView) itemView.findViewById(R.id.article_to_verify_domain_id);
            tv_sent_at = (TextView) itemView.findViewById(R.id.article_to_verify_sent_at_id);

            cardView = (CardView) itemView;
        }
    }
}
