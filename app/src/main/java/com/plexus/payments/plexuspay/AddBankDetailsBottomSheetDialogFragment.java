package com.plexus.payments.plexuspay;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.plexus.R;

public final class AddBankDetailsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private Callback callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        callback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public @Nullable
    View onCreateView(@NonNull LayoutInflater inflater,
                      @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.plexus_pay_add_bank_details_bottom_sheet, container, false);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        callback.onAddBankDetailsSheetDismissed();
    }

    public interface Callback {
        void onAddBankDetailsSheetDismissed();
    }

}
