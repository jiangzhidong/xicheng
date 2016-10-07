package com.qbcbyb.xc;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.qbcbyb.xc.model.SpotModel;
import com.qbcbyb.xc.util.ApplicationMain;

public class SpotOverlay extends ItemizedOverlay<SpotOverlayItem> {

    public interface OnOverlayPopTap {
        public void onTap(SpotModel spotItem);
    }

    static SpotPopupOverlay pop = null;

    private OnOverlayPopTap onOverlayPopTap;

    public OnOverlayPopTap getOnOverlayPopTap() {
        return onOverlayPopTap;
    }

    public void setOnOverlayPopTap(OnOverlayPopTap onOverlayPopTap) {
        this.onOverlayPopTap = onOverlayPopTap;
    }

    public SpotOverlay(Drawable arg0, MapView mapView) {
        super(arg0, mapView);

        pop = new SpotPopupOverlay(mapView, new PopupClickListener() {

            @Override
            public void onClickedPopup(int index) {
                if (null != SpotOverlay.this.getOnOverlayPopTap()) {
                    if (pop.getSpotModel() != null) {
                        SpotOverlay.this.getOnOverlayPopTap().onTap(pop.getSpotModel());
                    }
                }
            }
        });
    }

    @Override
    public boolean onTap(GeoPoint pt, MapView mapView) {
       // hidePopup();
        super.onTap(pt, mapView);
        return false;
    }

    @Override
    protected boolean onTap(int index) {
        SpotOverlayItem spotOverlayItem = (SpotOverlayItem) getItem(index);
        showPopup(spotOverlayItem);
        return true;
    }

    @Override
    public void addItem(List arg0) {
        super.addItem(arg0);
        hidePopup();
    }

    @Override
    public void addItem(OverlayItem arg0) {
        super.addItem(arg0);
        hidePopup();
    }

    @Override
    public boolean removeAll() {
        hidePopup();
        return super.removeAll();
    }

    public void hidePopup() {
        if (pop != null) {
            pop.hidePop();
        }
        pop.setSpotModel(null);
    }

    public void showPopup(SpotOverlayItem spotOverlayItem) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(ApplicationMain.getInstance().getAssets().open("map_check_spot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pop.setSpotModel(spotOverlayItem.getSpotModel());
        pop.showPopup(bmp, spotOverlayItem.getPoint(), 10);
    }

}
