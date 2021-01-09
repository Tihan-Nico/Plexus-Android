package com.plexus.account.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.plexus.R;
import com.plexus.model.account.Violations;

import java.util.List;

public class ViolationsAdapter extends RecyclerView.Adapter<ViolationsAdapter.ViewHolder>  {

    Context mContext;
    List<Violations> mViolations;

    Runnable runnable;

    public ViolationsAdapter(Context context, List<Violations> violations) {
        mContext = context;
        mViolations = violations;
    }

    @NonNull
    @Override
    public ViolationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_account_status_item, parent, false);
        return new ViolationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationsAdapter.ViewHolder holder, int position) {
        Violations violations = mViolations.get(position);

        String type = violations.getType();
        holder.timestamp.setReferenceTime(violations.getTimestamp());

        switch (type) {
            case "community":
                runnable = () -> holder.type.setText("You have been banned for breaking our community rules.");
                break;
            case "fake_account":
                runnable = () -> holder.type.setText("Your account was banned due to a suspicion of being a fake account.");
                break;
            case "buying_followers":
                runnable = () -> holder.type.setText("Buying followers is against Plexus rules and therefor your account is temporarely banned");
                break;
            case "spamming":
                runnable = () -> holder.type.setText("Spamming in Plexus is against our community standards and therefor resulted in a ban.");
                break;
            case "copyright_content":
                runnable = () -> holder.type.setText("You were reported for multiple counts of using copyrighted content on Plexus.");
                break;
            default:
                runnable = () -> holder.type.setText("Vulgar and abusive languages or content will not be tolerated on Plexus.");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mViolations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeTimeTextView timestamp;
        TextView type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);

        }
    }
}
