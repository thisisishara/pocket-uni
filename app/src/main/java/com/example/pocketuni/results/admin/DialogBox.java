package com.example.pocketuni.results.admin;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DialogBox extends AppCompatDialogFragment {
    private EditText regNum;
    private Spinner yearSem;

    FirebaseFirestore db = FirebaseFirestore.getInstance();;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_layout, null);

        builder
                .setView(view).setTitle("Search by Student ID and Semester of the relevant module")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sRegNum = regNum.getText().toString().toLowerCase().trim();
                        String sYearSem = yearSem.getSelectedItem().toString().trim();
                        Intent intent = new Intent(getActivity(), ListActivity.class);
                        intent.putExtra("DialogRegNum", sRegNum);
                        intent.putExtra("DialogYearSem", sYearSem);
                        startActivity(intent);
                    }
                });
        regNum = view.findViewById(R.id.dialogBox);
        yearSem = view.findViewById(R.id.dialogBoxSpinnerSemester);

        return builder.create();
    }

    private void showToast (String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
