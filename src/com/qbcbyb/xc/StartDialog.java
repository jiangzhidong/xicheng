package com.qbcbyb.xc;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class StartDialog extends Dialog {

    public StartDialog(Context context) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        ImageView view = new ImageView(StartDialog.this.getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        view.setScaleType(ScaleType.FIT_XY);
        view.setImageResource(R.drawable.apploading);
        setContentView(view);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                StartDialog.this.dismiss();
            }
        }, 3000);
    }
}
