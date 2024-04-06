package com.websarva.wings.android.wordmemorizz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class OrderConfirmDialogFragment3 extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle a){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning!");
        builder.setMessage("There are some data that have not got stored in Server. Try re-sync.");
        builder.setPositiveButton("Okay", new DialogButtonClickListener());
       // builder.setNeutralButton("",new DialogButtonClickListener());
        AlertDialog dialog=builder.create();
        return dialog;

    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which){
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:


                    break;
            }
        }
    }

}

