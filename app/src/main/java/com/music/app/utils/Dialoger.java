package com.music.app.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import com.music.app.R;

public class Dialoger
{
    public static void createListDialog
        (
            Context context,
            String title,
            int itemsID,
            DialogInterface.OnClickListener onClickListener
        )
    {
        AlertDialog.Builder dialog = getDialogBuilder(context);
        dialog.setTitle(title).setItems(itemsID, onClickListener);
        dialog.create().show();
    }

    public static void createAlertDialog
        (
            Context context,
            String title,
            String message,
            DialogInterface.OnClickListener positiveButtonListener
        )
    {
        AlertDialog.Builder dialog = getDialogBuilder(context);

        if(!message.equals(""))
            dialog.setMessage(message);

        dialog.setTitle(title)
              .setPositiveButton("Confirm", positiveButtonListener)
              .setNegativeButton("Cancel", null)
              .create()
              .show();
    }

    public static AlertDialog.Builder getDialogBuilder(Context context)
    {
        return new AlertDialog.Builder(context, R.style.AppTheme_AlertDialog);
    }
}
