package com.qbcbyb.xc;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.qbcbyb.libandroid.msg.Msg;
import com.qbcbyb.libandroid.msg.Msg.MsgCallback;
import com.qbcbyb.libandroid.msg.Msg.Which;
import com.qbcbyb.xc.model.CGeometry;
import com.qbcbyb.xc.model.CGeometry.CGeometryType;
import com.qbcbyb.xc.model.CGeometry.MapPoint;
import com.qbcbyb.xc.model.SpotModel;
import com.qbcbyb.xc.util.GeometryUtil;

import android.content.DialogInterface;
import android.os.Message;

public class PanoActivity extends ActivityBase {
    private PanoramaView mPanoView;

    @Override
    protected void setLayout() {
        mPanoView = new PanoramaView(this);
        setContentView(mPanoView);

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

    @Override
    protected void doInit() {
        super.doInit();
        SpotModel model = (SpotModel) getIntent().getSerializableExtra(KEY_SPOT);
        CGeometry geometry = model.getGeometry();
        if (geometry != null) {
            double lon = 0, lat = 0;
            // if(geometry.getType()==CGeometryType.multipoint){
            MapPoint point = geometry.getPoints()[0];
            lon = point.getX();
            lat = point.getY();
            // }
            mPanoView.setPanorama(lon, lat);
        } else {
            Msg.confirm(this, "提示", "未找到景点位置！", "确定", null, new MsgCallback() {

                @Override
                public void callBack(Which arg0) {
                    PanoActivity.this.finish();
                }
            });
        }
    }

}
