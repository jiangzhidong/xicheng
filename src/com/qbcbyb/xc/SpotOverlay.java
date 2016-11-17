package com.qbcbyb.xc;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.qbcbyb.xc.model.SpotModel;

import java.util.List;

public class SpotOverlay extends ItemizedOverlay<SpotOverlayItem> implements PopupClickListener {

    public interface OnOverlayPopTap {
        public void onTap(SpotModel spotItem);
    }

    private final SpotPopupOverlay pop;
    private final Drawable drawable;

    private OnOverlayPopTap onOverlayPopTap;

    public OnOverlayPopTap getOnOverlayPopTap() {
        return onOverlayPopTap;
    }

    public void setOnOverlayPopTap(OnOverlayPopTap onOverlayPopTap) {
        this.onOverlayPopTap = onOverlayPopTap;
    }

    public SpotOverlay(Drawable arg0, MapView mapView, SpotPopupOverlay pop) {
        super(arg0, mapView);
        this.pop = pop;
        this.drawable = arg0;
    }

    @Override
    public boolean onTap(GeoPoint pt, MapView mapView) {
        // hidePopup();
        pop.onTapBeforeOther(pt, mapView);
        if (pop.isHandled()) {
            return true;
        }
        final Projection projection = mapView.getProjection();
        final Point point = new Point();
        projection.toPixels(pt, point);
        double minDistance = Double.MAX_VALUE;
        OverlayItem finalItem = null;
        Point itemPoint = new Point();
        for (OverlayItem overlayItem : getAllItem()) {
            projection.toPixels(overlayItem.getPoint(), itemPoint);
            double sqrt = Math.sqrt(Math.pow(itemPoint.x - point.x, 2) + Math.pow(itemPoint.y - point.y, 2));
            if (sqrt < minDistance) {
                minDistance = sqrt;
                finalItem = overlayItem;
            }
        }
        if (minDistance < mapView.getWidth() / 20) {
            showPopup(((SpotOverlayItem) finalItem));
            return true;
        }
        return false;
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

    @Override
    public void onClickedPopup(int index) {
        if (null != SpotOverlay.this.getOnOverlayPopTap()) {
            if (pop.getSpotModel() != null) {
                SpotOverlay.this.getOnOverlayPopTap().onTap(pop.getSpotModel());
            }
        }
    }

    public void hidePopup() {
        if (pop != null) {
            pop.hidePop();
        }
        pop.setSpotModel(null);
    }

    public void showPopup(SpotOverlayItem spotOverlayItem) {
        pop.setSpotModel(spotOverlayItem.getSpotModel());
        pop.showPopup(spotOverlayItem.getPoint(), this);
    }

}
