package com.example.pocketuni.organizer.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pocketuni.R;

import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.makeText;

public class DeleteTimetableDialog extends AppCompatDialogFragment {
    private String timetableName;
    private Boolean response = false;
    private EditText body;

    private DeleteTimetableDialog.DeleteTimetableDialogListener deleteTimetableDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_delete_confirmation_box, null);
        body = view.findViewById(R.id.textViewBody);

        builder.setView(view)
                .setTitle("Deleting Timetable")
                .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing yet
                    }
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                response = true;
                deleteTimetableDialogListener.getConfirmation(response);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            deleteTimetableDialogListener = (DeleteTimetableDialog.DeleteTimetableDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement a DeleteTimetableDialogListener");
        }
    }

    public interface DeleteTimetableDialogListener {
        void getConfirmation (Boolean response);
    }

    private void showToast (String message) {
        makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
