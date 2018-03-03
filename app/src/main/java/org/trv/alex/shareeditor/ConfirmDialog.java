package org.trv.alex.shareeditor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmDialog extends DialogFragment {

    public interface DialogAction {
        void onPositiveAction(int index);
        void onNegativeAction();
    }

    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String ID = "id";

    public static ConfirmDialog newInstance(int titleId, int messageId, int index) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putInt(TITLE, titleId);
        args.putInt(MESSAGE, messageId);
        args.putInt(ID, index);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int titleId = getArguments().getInt(TITLE);
        final int messageId = getArguments().getInt(MESSAGE);
        final int index = getArguments().getInt(ID);

        return new AlertDialog.Builder(getActivity())
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogAction) getActivity()).onPositiveAction(index);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create();
    }
}
