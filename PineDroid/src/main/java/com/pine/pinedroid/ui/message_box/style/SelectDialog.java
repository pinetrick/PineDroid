package com.pine.pinedroid.ui.message_box.style;

import android.content.Context;
import android.os.Bundle;

import com.pine.pinedroid.R;


public class SelectDialog extends AbsAlertDialog {

    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }


    public SelectDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_box);


    }

    public SelectDialog getView() {
        return this;
    }

}
