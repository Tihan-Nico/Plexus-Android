package com.plexus.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ListView;
import android.widget.TextView;

import com.plexus.R;

import java.text.MessageFormat;

public class DialogViews {

    public static void genderDialog(Context context, TextView textView){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_list_gender);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        ListView gender_list = dialog.findViewById(R.id.list);
        gender_list.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedFromList = (String) gender_list.getItemAtPosition(position);
            textView.setText(selectedFromList);
            dialog.dismiss();
        });

        dialog.show();
    }

    public static void selectTime(Context context, TextView textView, int hours, int minutes){
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, (timePicker, selectedHour, selectedMinute) -> textView.setText(MessageFormat.format("{0}:{1}", selectedHour, selectedMinute)), hours, minutes, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

}
