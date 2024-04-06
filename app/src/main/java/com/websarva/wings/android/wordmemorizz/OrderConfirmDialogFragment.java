package com.websarva.wings.android.wordmemorizz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class OrderConfirmDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle a){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.warningMess);
        builder.setPositiveButton(R.string.warningBt1, new DialogButtonClickListener());
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
