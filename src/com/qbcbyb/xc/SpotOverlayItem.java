package com.qbcbyb.xc;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.qbcbyb.xc.model.CGeometry;
import com.qbcbyb.xc.model.CGeometry.CGeometryType;
import com.qbcbyb.xc.model.CGeometry.MapPoint;
import com.qbcbyb.xc.model.SpotModel;

public class SpotOverlayItem extends OverlayItem {

    private SpotModel spotModel;

    public SpotOverlayItem(GeoPoint geopoint, SpotModel spotModel) {
        super(geopoint, spotModel.getName(), spotModel.getDetailAddress());
        this.spotModel = spotModel;
    }

    public SpotModel getSpotModel() {
        return spotModel;
    }

    public void setSpotModel(SpotModel spotModel) {
        this.spotModel = spotModel;
    }

    public static List<SpotOverlayItem> genrateOverlayItemList(SpotModel spotModel) {
        List<SpotOverlayItem> list = new ArrayList<SpotOverlayItem>();
        CGeometry geometry = spotModel.getGeometry();
        if (geometry != null && geometry.getType() == CGeometryType.multipoint) {
            for (MapPoint mapPoint : geometry.getPoints()) {
                list.add(new SpotOverlayItem(
                    new GeoPoint((int) (mapPoint.getY() * 1E6), (int) (mapPoint.getX() * 1E6)), spotModel));
            }
        }
        return list;
    }

    public static SpotOverlayItem genrateOverlayItem(SpotModel spotModel) {
        SpotOverlayItem overlayItem = null;
        CGeometry geometry = spotModel.getGeometry();
        if (geometry.getType() == CGeometryType.multipoint) {
            for (MapPoint mapPoint : geometry.getPoints()) {
                overlayItem = new SpotOverlayItem(new GeoPoint((int) (mapPoint.getY() * 1E6),
                    (int) (mapPoint.getX() * 1E6)), spotModel);
                break;
            }
        }
        return overlayItem;
    }
}
