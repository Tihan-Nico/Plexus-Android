package com.plexus.giph.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

import com.plexus.giph.model.GiphyImage;
import com.plexus.giph.net.GiphyGifLoader;

import java.util.List;

public class GiphyGifFragment extends GiphyFragment {

    @Override
    public Loader<List<GiphyImage>> onCreateLoader(int id, Bundle args) {
        return new GiphyGifLoader(getActivity(), searchString);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<GiphyImage>> loader) {

    }

}
