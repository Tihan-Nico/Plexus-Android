package com.plexus.giph.ui;

import android.content.Context;

import com.plexus.utils.SecurePreferences;

public class GiphyActivityToolbarPreferencesPersistence implements GiphyActivityToolbar.Persistence{

    static GiphyActivityToolbar.Persistence fromContext(Context context) {
        return new GiphyActivityToolbarPreferencesPersistence(context.getApplicationContext());
    }

    private final Context context;

    private GiphyActivityToolbarPreferencesPersistence(Context context) {
        this.context = context;
    }

    @Override
    public boolean getGridSelected() {
        return SecurePreferences.isGifSearchInGridLayout(context);
    }

    @Override
    public void setGridSelected(boolean isGridSelected) {
        SecurePreferences.setIsGifSearchInGridLayout(context, isGridSelected);
    }

}
