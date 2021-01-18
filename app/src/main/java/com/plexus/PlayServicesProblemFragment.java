package com.plexus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.GoogleApiAvailability;

public class PlayServicesProblemFragment extends DialogFragment {

    @Override
    public @NonNull
    Dialog onCreateDialog(@Nullable Bundle bundle) {
        int    code   = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, 9111);

        if (dialog == null) {
            return new AlertDialog.Builder(requireActivity())
                    .setNegativeButton(android.R.string.ok, null)
                    .setMessage("The version of Google Play Services you have installed is not functioning correctly.  Please reinstall Google Play Services and try again.")
                    .create();
        } else {
            return dialog;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        finish();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        finish();
    }

    private void finish() {
        Activity activity = getActivity();
        if (activity != null) activity.finish();
    }

}
