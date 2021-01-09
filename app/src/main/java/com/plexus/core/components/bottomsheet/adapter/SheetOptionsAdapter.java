package com.plexus.core.components.bottomsheet.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.core.components.bottomsheet.model.SheetOptions;

import java.util.ArrayList;

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

public class SheetOptionsAdapter extends BaseAdapter {

    ArrayList<SheetOptions> options;
    Context mContext;
    FirebaseUser firebaseUser;

    public SheetOptionsAdapter(Context context, ArrayList<SheetOptions> arrayList) {
        this.options = arrayList;
        this.mContext = context;
    }

    /*private view holder class*/
    private static class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sheet_item, null);
            holder = new ViewHolder();
            holder.txtTitle = convertView.findViewById(R.id.text);
            holder.imageView = convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        SheetOptions sheetOptions = (SheetOptions) getItem(position);

        holder.txtTitle.setText(sheetOptions.getText());
        holder.imageView.setImageResource(sheetOptions.getIcon());

        return convertView;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return options.indexOf(getItem(position));
    }
}
