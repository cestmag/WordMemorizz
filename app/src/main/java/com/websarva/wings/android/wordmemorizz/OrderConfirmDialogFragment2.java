package com.websarva.wings.android.wordmemorizz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;


public class OrderConfirmDialogFragment2 extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle a){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning2);
        builder.setMessage(R.string.warningMess2);
        builder.setPositiveButton(R.string.warningBt2, new DialogButtonClickListener());
        builder.setNeutralButton(R.string.editbutton,new DialogButtonClickListener());
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
