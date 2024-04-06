package com.websarva.wings.android.wordmemorizz;//package com.example.myapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MemoDeleteComfirm extends DialogFragment {
    public interface DialogListener {
        void onOkButtonClicked(int i);
       // void onCancelButtonClicked();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Set the dialog title (optional)
        builder.setTitle("Confirmation Dialog");

        // Set the dialog message
        builder.setMessage("Are you sure you want to proceed?");

        // Set the positive button (OK button) and its click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the action when OK button is clicked
                // For example, call a method to proceed with the operation
                DialogListener listener = (DialogListener) requireActivity();
                listener.onOkButtonClicked(6);
                performOkAction();
            }
        });

        // Set the negative button (Cancel button) and its click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the action when Cancel button is clicked
                // For example, dismiss the dialog without performing any action

                dialog.dismiss();
            }
        });

        // Create and return the AlertDialog
        return builder.create();
    }

    private void performOkAction() {
        // Implement the action you want to perform when OK is clicked.

    }
}
