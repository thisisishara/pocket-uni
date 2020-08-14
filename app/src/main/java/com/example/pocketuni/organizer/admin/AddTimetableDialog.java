package com.example.pocketuni.organizer.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pocketuni.R;

import static android.widget.Toast.makeText;

public class AddTimetableDialog extends AppCompatDialogFragment {

    private Spinner semesterSpinner, courseSpinner;
    private EditText batchEditText;
    private AddTimetableDialogListener addTimetableDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_addtimetable, null);

        builder.setView(view)
                .setTitle(R.string.admin_add_timetable)
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

        courseSpinner = view.findViewById(R.id.spinnerCourse);
        semesterSpinner = view.findViewById(R.id.spinnerSemester);
        batchEditText = view.findViewById(R.id.editTextBatch);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batch = batchEditText.getText().toString().trim();
                int batchNumber;
                try {
                    batchNumber = Integer.parseInt(batch);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), R.string.dialog_batch_number_empty_error,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (batchNumber <= 0) {
                    Toast.makeText(getContext(), R.string.dialog_batch_number_invalid_error,Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String yearSemester = semesterSpinner.getSelectedItem().toString();
                    String course = courseSpinner.getSelectedItem().toString();
                    addTimetableDialogListener.getNewTimetableData(yearSemester,course,batchNumber);
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addTimetableDialogListener = (AddTimetableDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement a AddTimetableDialogListener");
        }
    }

    public interface AddTimetableDialogListener {
        void getNewTimetableData (String yearSemesterSpinner, String courseSpinner, int batch);
    }

    private void showToast (String message) {
        makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
