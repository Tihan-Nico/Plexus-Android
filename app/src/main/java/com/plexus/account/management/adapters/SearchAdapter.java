package com.plexus.account.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.plexus.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    List<String> mSearches;
    Context mContext;

    public SearchAdapter(Context context, List<String> mSearches) {
        mContext = context;
        this.mSearches = mSearches;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
