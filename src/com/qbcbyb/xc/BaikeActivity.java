package com.qbcbyb.xc;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.qbcbyb.xc.model.SpotModel;

import android.content.DialogInterface;
import android.os.Message;
import android.widget.TextView;

public class BaikeActivity extends TitleWithBackActivity {
    private SpotModel model;

    @Override
    protected void initLayout(TextView title) {
        model = (SpotModel) getIntent().getSerializableExtra(KEY_SPOT);
        title.setText(model.getName());
    }

    @Override
    protected void addEventListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleMessage(Message arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onCancelProgress(DialogInterface arg0) {
        // TODO Auto-generated method stub

    }

}
