package com.plexus.settings.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.plexus.R;
import com.plexus.model.settings.Notices;

import java.util.List;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.ViewHolder> {

    private Context context;
    private List<Notices> noticesList;

    public NoticesAdapter(Context context, List<Notices> movieList) {
        this.context = context;
        this.noticesList = movieList;
    }

    @NonNull
    @Override
    public NoticesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.settings_notice_item, parent, false);
        return new NoticesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticesAdapter.ViewHolder holder, int position) {
        final Notices notices = noticesList.get(position);
        holder.name.setText(notices.getName());
        holder.date.setText(notices.getDate());
        holder.description_1.setText(notices.getDescription_1());
        holder.description_2.setText(notices.getDescription_2());

        holder.image_url_1.setImageURI(notices.getImage_url_1());
        holder.image_url_2.setImageURI(notices.getImage_url_2());

        if (notices.isHasImages()) {
            holder.image_url_2.setVisibility(View.VISIBLE);
            holder.image_url_1.setVisibility(View.VISIBLE);
        } else {
            holder.image_url_2.setVisibility(View.GONE);
            holder.image_url_1.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return noticesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, name, description_1, description_2;
        SimpleDraweeView image_url_1, image_url_2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            description_1 = itemView.findViewById(R.id.description_1);
            description_2 = itemView.findViewById(R.id.description_2);
            image_url_1 = itemView.findViewById(R.id.image_url_1);
            image_url_2 = itemView.findViewById(R.id.image_url_2);

        }
    }
}
