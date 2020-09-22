
package com.example.pocketuni.organizer.std;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;

import static android.widget.Toast.makeText;

public class TimetableReminderDialog extends AppCompatDialogFragment{
    private EditText editTextMinutes;
    private Switch enableReminders;

    private TimetableReminderDialogListener timetableReminderDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_set_timetable_reminder, null);

        builder.setView(view)
                .setTitle(R.string.timetable_reminder_title)
                .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        editTextMinutes = view.findViewById(R.id.editTextMinutes);
        enableReminders = view.findViewById(R.id.switchEnableReminders);

        if(CurrentUser.isIsRemindersOn() == true) {
            enableReminders.setChecked(true);
            editTextMinutes.setText(CurrentUser.getRemainderMinutes()+"");
        } else {
            enableReminders.setChecked(false);
            editTextMinutes.setEnabled(false);
        }

        enableReminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (enableReminders.isChecked() == true) {
                    showToast("Reminders: ON");
                    editTextMinutes.setEnabled(true);
                } else {
                    showToast("Reminders: OFF");
                    editTextMinutes.setEnabled(false);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isReminderOn = false;
                int minutes = -1;

                if (enableReminders.isChecked() == true) {
                    isReminderOn = true;

                    if(editTextMinutes.getText().toString().isEmpty()){
                        showToast("REMINDER TIME MUST BE SPECIFIED.");
                        return;
                    } else {
                        try {
                            minutes = Integer.parseInt(editTextMinutes.getText().toString());

                            if(minutes < 1 || minutes >60){
                                showToast("REMINDER TIME MUST BE BETWEEN 1-60.");
                                return;
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            return;
                        }
                    }
                } else {
                    isReminderOn = false;
                    minutes = -1;
                }

                timetableReminderDialogListener.getNewTimetableReminderData(isReminderOn, minutes);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            timetableReminderDialogListener = (TimetableReminderDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement a TimetableReminderDialogListener");
        }
    }

    public interface TimetableReminderDialogListener {
        void getNewTimetableReminderData (boolean enabledState, int minutes);
    }

    private void showToast (String message) {
        makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

