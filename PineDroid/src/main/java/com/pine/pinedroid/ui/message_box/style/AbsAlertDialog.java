package com.pine.pinedroid.ui.message_box.style;

import android.app.AlertDialog;
import android.content.Context;

public abstract class AbsAlertDialog extends AlertDialog {

    protected AbsAlertDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }


    protected AbsAlertDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    protected AbsAlertDialog(Context context, boolean cancelable,
                             OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }


    public abstract AbsAlertDialog getView();
}
