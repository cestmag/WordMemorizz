package com.websarva.wings.android.wordmemorizz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class ModeOptionDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.warningMess);
      //  builder.setPositiveButton(R.string.warningBt1, new OrderConfirmDialogFragment.DialogButtonClickListener());
        AlertDialog dialog=builder.create();
        return dialog;
    }

}
