package com.esy.jaha.osmdroid.nuevo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String min = String.valueOf(minute);
        String hora = String.valueOf(hourOfDay);
        if(minute < 10) {
             min = "0" + minute;
        }
        if(hourOfDay < 10){
            hora = "0" + hourOfDay;
        }
        NuevoEvento.hora_event.setText(hora + ":" + min + " hs.");
        NuevoEvento.hora_mysql = hora + ":" + min +":00";
    }
}