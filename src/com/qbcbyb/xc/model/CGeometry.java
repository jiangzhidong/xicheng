package com.qbcbyb.xc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.qbcbyb.libandroid.model.BaseModel;

public class CGeometry extends BaseModel {
    private static final long serialVersionUID = 1L;

    public enum CGeometryType {
        multipoint, polygon
    }

    private CGeometryType type;
    private MapPoint[] points;

    public CGeometryType getType() {
        return type;
    }

    public void setType(CGeometryType type) {
        this.type = type;
    }

    public MapPoint[] getPoints() {
        return points;
    }

    public void setPoints(MapPoint[] points) {
        this.points = points;
    }

    public static class MapPoint extends BaseModel{

        private static final long serialVersionUID = 8554792563978378367L;
        private Double x;
        private Double y;

        public MapPoint(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }

    }
}
