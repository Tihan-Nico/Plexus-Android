package com.plexus.giph.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.ByteBufferUtil;
import com.plexus.R;
import com.plexus.color.MaterialColor;
import com.plexus.giph.model.ChunkedImageUrl;
import com.plexus.giph.model.GiphyImage;
import com.plexus.utils.Util;
import com.plexus.utils.logging.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;


class GiphyAdapter extends RecyclerView.Adapter<GiphyAdapter.GiphyViewHolder> {

    private static final String TAG = GiphyAdapter.class.getSimpleName();

    private final Context       context;

    private List<GiphyImage>     images;
    private OnItemClickListener  listener;

    class GiphyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RequestListener<Drawable> {

        public AspectRatioImageView thumbnail;
        public GiphyImage           image;
        public ProgressBar          gifProgress;
        public volatile boolean     modelReady;

        GiphyViewHolder(View view) {
            super(view);
            thumbnail   = view.findViewById(R.id.thumbnail);
            gifProgress = view.findViewById(R.id.gif_progress);
            thumbnail.setOnClickListener(this);
            gifProgress.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onClick(this);
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Log.w(TAG, e);

            synchronized (this) {
                if (new ChunkedImageUrl(image.getGifUrl(), image.getGifSize()).equals(model)) {
                    this.modelReady = true;
                    notifyAll();
                }
            }

            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            synchronized (this) {
                if (new ChunkedImageUrl(image.getGifUrl(), image.getGifSize()).equals(model)) {
                    this.modelReady = true;
                    notifyAll();
                }
            }

            return false;
        }


        public byte[] getData(boolean forMms) throws ExecutionException, InterruptedException {
            synchronized (this) {
                while (!modelReady) {
                    Util.wait(this, 0);
                }
            }

            GifDrawable drawable = Glide.with(context).asGif()
                    .load(forMms ? new ChunkedImageUrl(image.getGifMmsUrl(), image.getMmsGifSize()) :
                            new ChunkedImageUrl(image.getGifUrl(), image.getGifSize()))
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();

            return ByteBufferUtil.toBytes(drawable.getBuffer());
        }

        public synchronized void setModelReady() {
            this.modelReady = true;
            notifyAll();
        }
    }

    GiphyAdapter(@NonNull Context context, @NonNull List<GiphyImage> images) {
        this.context       = context.getApplicationContext();
        this.images        = images;
    }

    public void setImages(@NonNull List<GiphyImage> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void addImages(List<GiphyImage> images) {
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    @Override
    public @NonNull GiphyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.giphy_thumbnail, parent, false);

        return new GiphyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GiphyViewHolder holder, int position) {
        GiphyImage image = images.get(position);

        holder.modelReady = false;
        holder.image      = image;
        holder.thumbnail.setAspectRatio(image.getGifAspectRatio());
        holder.gifProgress.setVisibility(View.GONE);

        RequestBuilder<Drawable> thumbnailRequest = Glide.with(context)
                .load(new ChunkedImageUrl(image.getStillUrl(), image.getStillSize()))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if (Util.isLowMemory(context)) {
            Glide.with(context)
                    .load(image.getStillUrl())
                    .placeholder(new ColorDrawable(Util.getRandomElement(MaterialColor.values()).toConversationColor(context)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);

            holder.setModelReady();
        } else {
            Glide.with(context)
                    .load(image.getGifUrl())
                    .thumbnail(thumbnailRequest)
                    .placeholder(new ColorDrawable(Util.getRandomElement(MaterialColor.values()).toConversationColor(context)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(holder)
                    .into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(GiphyViewHolder viewHolder);
    }
}