package com.example.pocketuni.organizer.admin;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pocketuni.R;

import java.util.Date;

import static android.widget.Toast.makeText;

public class AddTimetableSlotDialog extends AppCompatDialogFragment {

    private Spinner daySpinner;
    private EditText courseCodeEditText, courseNameEditText,  lecInChargeEditText;
    TimePicker startTimePicker, endTimePicker;
    String courseCode, courseName, lecturerInCharge, day ,startTimeString, endTimeString;
    Date startTime, endTime;

    private AddTimetableSlotDialogListener addTimetableSlotDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_admin_add_timetable_slots, null);

        builder.setView(view)
                .setTitle(R.string.dialog_add_slot_title)
                .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        daySpinner = view.findViewById(R.id.spinnerDay);
        courseCodeEditText = view.findViewById(R.id.editTextSubjectCode);
        courseNameEditText = view.findViewById(R.id.editTextSubjectName);
        lecInChargeEditText = view.findViewById(R.id.editTextLecturerInCharge);
        startTimePicker = view.findViewById(R.id.startTimePicker);
        startTimePicker.setIs24HourView(true);
        endTimePicker = view.findViewById(R.id.endTimePicker);
        endTimePicker.setIs24HourView(true);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseCode = courseCodeEditText.getText().toString().trim();
                courseName = courseNameEditText.getText().toString().trim();
                lecturerInCharge = lecInChargeEditText.getText().toString().trim();
                day = daySpinner.getSelectedItem().toString().trim();

                //getStartAndEndTime
                int stHour, stMinute, edHour, edMinute;
                String stam_pm, edam_pm;

                if (Build.VERSION.SDK_INT >= 23 ){
                    stHour = startTimePicker.getHour();
                    stMinute = startTimePicker.getMinute();
                    edHour = endTimePicker.getHour();
                    edMinute = endTimePicker.getMinute();
                }
                else{
                    stHour = startTimePicker.getCurrentHour();
                    stMinute = startTimePicker.getCurrentMinute();
                    edHour = endTimePicker.getCurrentHour();
                    edMinute = endTimePicker.getCurrentMinute();
                }
                if(stHour > 12) {
                    stam_pm = "PM";
                    stHour = stHour - 12;
                }
                else
                {
                    stam_pm="AM";
                }

                if(edHour > 12) {
                    edam_pm = "PM";
                    edHour = edHour - 12;
                }
                else
                {
                    edam_pm="AM";
                }

                startTimeString = stHour+":"+stMinute+":"+stam_pm;
                endTimeString = edHour+":"+edMinute+":"+edam_pm;
                /*
                try {

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), R.string.dialog_batch_number_empty_error,Toast.LENGTH_SHORT).show();
                    return;
                }*/

                    addTimetableSlotDialogListener.getNewTimetableSlotData(courseCode, courseName, lecturerInCharge,day,startTimeString, endTimeString);
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addTimetableSlotDialogListener = (AddTimetableSlotDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement a AddTimetableSlotDialogListener");
        }
    }

    public interface AddTimetableSlotDialogListener {
        void getNewTimetableSlotData (String courseCode, String courseName,  String lecInCharge, String day, String sTime, String eTime);
    }

    private void showToast (String message) {
        makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

