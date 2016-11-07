package com.qbcbyb.xc;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.qbcbyb.xc.model.SpotModel;

public class SpotPopupOverlay extends PopupOverlay {

    private SpotModel spotModel;

    public SpotModel getSpotModel() {
        return spotModel;
    }

    public void setSpotModel(SpotModel spotModel) {
        this.spotModel = spotModel;
    }

    public SpotPopupOverlay(MapView arg0, PopupClickListener arg1) {
        super(arg0, arg1);
    }

    @Override
    public void showPopup(Bitmap[] arg0, GeoPoint arg1, int arg2) {
        arg0[1] = addTitle(arg0[1]);
        super.showPopup(arg0, arg1, arg2);
    }

    @Override
    public void showPopup(Bitmap arg0, GeoPoint arg1, int arg2) {
        try {
            super.showPopup(addTitle(arg0), arg1, arg2);
        } catch (Exception e) {
        }
    }

    private Bitmap addTitle(Bitmap bitmap) {
        if (spotModel != null) {
            String title = spotModel.getName();
            Bitmap toDrawBitmap = bitmap.copy(Config.ARGB_8888, true);
            Canvas canvas = new Canvas(toDrawBitmap);
            Paint paint = new Paint();
            paint.setTextSize(17);
            paint.setColor(Color.BLACK);
            Rect bounds = new Rect();
            paint.getTextBounds(title, 0, title.length(), bounds);
            canvas.drawText(title, (toDrawBitmap.getWidth() - bounds.width()) / 2f,
                (toDrawBitmap.getHeight() - 10 + bounds.height()) / 2f, paint);
            return toDrawBitmap;
        }
        return bitmap;
    }

}
