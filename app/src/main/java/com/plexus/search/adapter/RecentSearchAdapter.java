package com.plexus.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.plexus.R;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {

    List<String> mSearches;
    private final Context mContext;

    public RecentSearchAdapter(Context context, List<String> mSearches) {
        mContext = context;
        this.mSearches = mSearches;

    }

    @NonNull
    @Override
    public RecentSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_search, parent, false);
        return new RecentSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchAdapter.ViewHolder holder, int position) {
        holder.search_term.setText(mSearches.get(position));

    }

    @Override
    public int getItemCount() {
        return mSearches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView search_term;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            search_term = itemView.findViewById(R.id.search_term);

        }
    }
}
