package com.plexus.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.core.utils.logging.Log;

public class CommunicationActions {

    private static final String TAG = Log.tag(CommunicationActions.class);

    public static void openBrowserLink(@NonNull Context context, @NonNull String link) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openEmail(@NonNull Context context, @NonNull String address, @Nullable String subject, @Nullable String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, Util.emptyIfNull(subject));
        intent.putExtra(Intent.EXTRA_TEXT, Util.emptyIfNull(body));

        context.startActivity(Intent.createChooser(intent, "Send Mail"));
    }

}
