package com.plexus.editor.image.filters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plexus.R;
import com.plexus.core.editor.image.PlexusImageFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.ViewHolder> {

    private FilterListener mFilterListener;
    private List<Pair<String, PlexusImageFilter>> mPairList = new ArrayList<>();

    public FilterViewAdapter(FilterListener filterListener) {
        mFilterListener = filterListener;
        setupFilters();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_image_row_filter_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, PlexusImageFilter> filterPair = mPairList.get(position);
        Bitmap fromAsset = getBitmapFromAsset(holder.itemView.getContext(), filterPair.first);
        holder.mImageFilterView.setImageBitmap(fromAsset);
        holder.mTxtFilterName.setText(filterPair.second.name().replace("_", " "));
    }

    @Override
    public int getItemCount() {
        return mPairList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageFilterView;
        TextView mTxtFilterName;

        ViewHolder(View itemView) {
            super(itemView);
            mImageFilterView = itemView.findViewById(R.id.imgFilterView);
            mTxtFilterName = itemView.findViewById(R.id.txtFilterName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterListener.onFilterSelected(mPairList.get(getLayoutPosition()).second);
                }
            });
        }
    }

    private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
            return BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupFilters() {
        mPairList.add(new Pair<>("filters/original.jpg", PlexusImageFilter.NONE));
        mPairList.add(new Pair<>("filters/auto_fix.png", PlexusImageFilter.AUTO_FIX));
        mPairList.add(new Pair<>("filters/brightness.png", PlexusImageFilter.BRIGHTNESS));
        mPairList.add(new Pair<>("filters/contrast.png", PlexusImageFilter.CONTRAST));
        mPairList.add(new Pair<>("filters/documentary.png", PlexusImageFilter.DOCUMENTARY));
        mPairList.add(new Pair<>("filters/dual_tone.png", PlexusImageFilter.DUE_TONE));
        mPairList.add(new Pair<>("filters/fill_light.png", PlexusImageFilter.FILL_LIGHT));
        mPairList.add(new Pair<>("filters/fish_eye.png", PlexusImageFilter.FISH_EYE));
        mPairList.add(new Pair<>("filters/grain.png", PlexusImageFilter.GRAIN));
        mPairList.add(new Pair<>("filters/gray_scale.png", PlexusImageFilter.GRAY_SCALE));
        mPairList.add(new Pair<>("filters/lomish.png", PlexusImageFilter.LOMISH));
        mPairList.add(new Pair<>("filters/negative.png", PlexusImageFilter.NEGATIVE));
        mPairList.add(new Pair<>("filters/posterize.png", PlexusImageFilter.POSTERIZE));
        mPairList.add(new Pair<>("filters/saturate.png", PlexusImageFilter.SATURATE));
        mPairList.add(new Pair<>("filters/sepia.png", PlexusImageFilter.SEPIA));
        mPairList.add(new Pair<>("filters/sharpen.png", PlexusImageFilter.SHARPEN));
        mPairList.add(new Pair<>("filters/temprature.png", PlexusImageFilter.TEMPERATURE));
        mPairList.add(new Pair<>("filters/tint.png", PlexusImageFilter.TINT));
        mPairList.add(new Pair<>("filters/vignette.png", PlexusImageFilter.VIGNETTE));
        mPairList.add(new Pair<>("filters/cross_process.png", PlexusImageFilter.CROSS_PROCESS));
        mPairList.add(new Pair<>("filters/b_n_w.png", PlexusImageFilter.BLACK_WHITE));
        mPairList.add(new Pair<>("filters/flip_horizental.png", PlexusImageFilter.FLIP_HORIZONTAL));
        mPairList.add(new Pair<>("filters/flip_vertical.png", PlexusImageFilter.FLIP_VERTICAL));
        mPairList.add(new Pair<>("filters/rotate.png", PlexusImageFilter.ROTATE));
    }
}
