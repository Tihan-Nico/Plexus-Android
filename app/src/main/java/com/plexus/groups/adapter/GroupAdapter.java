package com.plexus.groups.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.components.components.ImageView.Constants;
import com.plexus.groups.activity.GroupActivity;
import com.plexus.model.group.Group;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    Context context;
    Bitmap bitmap;
    List<Group> groups;

    FirebaseUser firebaseUser;

    public GroupAdapter(Context context, List<Group> groups){
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @NotNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupAdapter.ViewHolder holder, int position) {
        Group group = groups.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.group_name.setText(MasterCipher.decrypt(group.getName()));
        Glide.with(context).asBitmap().load(MasterCipher.decrypt(group.getCoverImageUrl())).into(holder.group_cover);
        groupCoverBlur(holder.group_background, holder.group_cover, group);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, GroupActivity.class);
            intent.putExtra("group_id", group.getId());
            intent.putExtra("user_id", firebaseUser.getUid());
            intent.putExtra("cover_url", group.getCoverImageUrl());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView group_name;
        ImageView group_cover, group_background;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            group_name = itemView.findViewById(R.id.group_name);
            group_cover = itemView.findViewById(R.id.group_cover);
            group_background = itemView.findViewById(R.id.group_background);

        }
    }

    private void groupCoverBlur(ImageView group_background, ImageView group_cover, Group group){
        if (bitmap != null) {
            group_background.setBackground(
                    new BitmapDrawable(
                            context.getResources(),
                            Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
            group_cover.setImageBitmap(bitmap);
        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(MasterCipher.decrypt(group.getCoverImageUrl()))
                    .error(R.mipmap.ic_launcher)
                    .listener(
                            new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(
                                        @Nullable GlideException e,
                                        Object model,
                                        Target<Bitmap> target,
                                        boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(
                                        Bitmap resource,
                                        Object model,
                                        Target<Bitmap> target,
                                        DataSource dataSource,
                                        boolean isFirstResource) {
                                    if (Build.VERSION.SDK_INT >= 16) {
                                        group_background.setBackground(
                                                new BitmapDrawable(
                                                        context.getResources(),
                                                        Constants.fastblur(
                                                                Bitmap.createScaledBitmap(resource, 50, 50, true)))); // ));
                                    } else {
                                        onPalette(Palette.from(resource).generate(), group_background);
                                    }
                                    group_cover.setImageBitmap(resource);
                                    return false;
                                }
                            })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(group_cover);
        }
    }

    public void onPalette(Palette palette, ImageView group_background) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) group_background.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }

}
