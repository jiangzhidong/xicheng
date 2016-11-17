package com.qbcbyb.xc;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.qbcbyb.xc.model.SpotModel;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpotPopupOverlay extends ItemizedOverlay<OverlayItem> {

    private SpotModel spotModel;
    private AtomicBoolean canTap = new AtomicBoolean(true);
    private Runnable action = new Runnable() {
        @Override
        public void run() {
            SpotPopupOverlay.this.canTap.set(true);
        }
    };
    private boolean handled = false;

    public SpotModel getSpotModel() {
        return spotModel;
    }

    public void setSpotModel(SpotModel spotModel) {
        this.spotModel = spotModel;
    }

    private PopupClickListener popupClickListener;

    public SpotPopupOverlay(Drawable drawable, MapView mapView) {
        super(drawable, mapView);
    }

    public boolean isHandled() {
        return handled;
    }

    public void onTapBeforeOther(GeoPoint pt, MapView mapView) {
        mapView.removeCallbacks(action);
        mapView.postDelayed(action, 500);
        if (!canTap.getAndSet(false)) {
            return;
        }
        final Projection projection = mapView.getProjection();
        final Point point = new Point();
        projection.toPixels(pt, point);
        double minDistance = Double.MAX_VALUE;
        Point itemPoint = new Point();
        for (OverlayItem overlayItem : getAllItem()) {
            projection.toPixels(overlayItem.getPoint(), itemPoint);
            double sqrt = Math.sqrt(Math.pow(itemPoint.x - point.x, 2) + Math.pow(itemPoint.y - point.y, 2));
            if (sqrt < minDistance) {
                minDistance = sqrt;
            }
        }
        if (minDistance < mapView.getWidth() / 20) {
            if (popupClickListener != null) {
                popupClickListener.onClickedPopup(0);
            }
            this.handled = true;
        } else {
            this.handled = false;
        }
    }

    public void showPopup(GeoPoint arg1, PopupClickListener popupClickListener) {
        this.popupClickListener = popupClickListener;
        removeAll();
        addItem(new OverlayItem(arg1, spotModel.getName(), spotModel.getDetailAddress()));
        mMapView.refresh();
    }

    public void hidePop() {
        removeAll();
        mMapView.refresh();
    }

}
