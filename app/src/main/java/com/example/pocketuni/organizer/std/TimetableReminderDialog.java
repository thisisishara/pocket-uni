
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

import static android.widget.Toast.makeText;

public class TimetableReminderDialog extends AppCompatDialogFragment {

    private TimePicker reminderPicker;
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

        reminderPicker = view.findViewById(R.id.remindBeforeTimePicker);
        enableReminders = view.findViewById(R.id.switchEnableReminders);
        reminderPicker.setIs24HourView(true);

        enableReminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (enableReminders.isChecked() == true) {
                    showToast("Switch ON");
                    reminderPicker.setEnabled(false);
                } else {
                    showToast("Swith OFF");
                    reminderPicker.setEnabled(true);
                }
            }
        });

        //semesterSpinner = view.findViewById(R.id.spinnerSemester);
        //batchEditText = view.findViewById(R.id.editTextBatch);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enableReminders.isChecked() == true) {
                    showToast("Final ON");
                    reminderPicker.setEnabled(false);
                } else {
                    showToast("Final OFF");
                    reminderPicker.setEnabled(true);
                }

////                String batch = batchEditText.getText().toString().trim();
////                int batchNumber;
////                try {
//                    batchNumber = Integer.parseInt(batch);
//                } catch (NumberFormatException e) {
//                    Toast.makeText(getContext(), R.string.dialog_batch_number_empty_error,Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (batchNumber <= 0) {
//                    Toast.makeText(getContext(), R.string.dialog_batch_number_invalid_error,Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    String yearSemester = semesterSpinner.getSelectedItem().toString();
//                    String course = courseSpinner.getSelectedItem().toString();
//                    addTimetableDialogListener.getNewTimetableData(yearSemester,course,batchNumber);
                    dialog.dismiss();
 //               }
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
        void getNewTimetableReminderData (String enabledState, String time);
    }

    private void showToast (String message) {
        makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

