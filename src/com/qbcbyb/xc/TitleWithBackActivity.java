package com.qbcbyb.xc;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class TitleWithBackActivity extends ActivityBase {
    private LinearLayout contentView;
    private ImageButton btnBack;
    private TextView txtTitle;

    @Override
    protected void setLayout() {
        contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);
        View.inflate(this, R.layout.view_titlebar, contentView);
        super.setContentView(contentView, new LayoutParams(-1, -1));
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        btnBack.setColorFilter(0xffffffff);
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TitleWithBackActivity.this.finish();
            }
        });

        initLayout(txtTitle);
    }

    protected abstract void initLayout(TextView title);

    private void removeOtherViewExcludeTitle() {
        while (contentView.getChildCount() > 1) {
            contentView.removeViewAt(1);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        removeOtherViewExcludeTitle();
        View.inflate(this, layoutResID, contentView);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        removeOtherViewExcludeTitle();
        contentView.addView(view, params);
    }

    @Override
    public void setContentView(View view) {
        removeOtherViewExcludeTitle();
        contentView.addView(view);
    }

}
