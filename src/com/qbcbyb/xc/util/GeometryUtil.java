package com.qbcbyb.xc.util;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.baidu.mapapi.cloud.Bounds;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class GeometryUtil {
    static private PointF ps = new PointF(), pe = new PointF(), pl = new PointF();

    /**
     * 根据上一点及当前点，判断上一点是否有必要画出。 判断的依据是两点至少有一点在矩形范围内， 两点都在矩形范围内，判断位置是否重叠（距离<1）
     * 一点在范围内，一点不在时，将范围外的点位置修改为两点连线与矩形的交点的位置，进而将新得到点同原范围内的一点做重叠判断
     * 如果上一点有必要画，则修改上一点的坐标至矩形范围内
     * 
     * @param ps
     *            上一点
     * @param pe
     *            当前点
     * @param rect
     *            矩形范围
     * @param path
     * @return
     */
    static public boolean checkAndAddPoint(PointF p1, PointF p2, RectF rect, Path path) {
        ps.set(p1);
        pe.set(p2);
        boolean isLastIn = isPointInRect(rect, ps), isNowIn = isPointInRect(rect, pe);
        if (!isLastIn && isNowIn) {
            getOneNode(pe, ps, rect);
        } else if (isLastIn && !isNowIn) {
            getOneNode(ps, pe, rect);
        } else if (!isLastIn && !isNowIn && !getTowNode(ps, pe, rect)) {
            return false;
        }
        if (path.isEmpty()) {
            path.moveTo(ps.x, ps.y);
            pl.set(ps);
        }
        if (!isLastIn) {
            path.lineTo(ps.x, ps.y);
            pl.set(ps);
        }

        if (getDistance(pl, pe) >= 1d) {
            path.lineTo(pe.x, pe.y);
            pl.set(pe);
        }
        return true;
    }

    /**
     * 两点距离
     * 
     * @param p1
     * @param p2
     * @return
     */
    static public double getDistance(PointF p1, PointF p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * 判断矩形外两点连线与矩形是否有交点并获取
     * 
     * @param p12
     * @param p22
     * @param rect
     * @return 返回是否有交点
     */
    static public boolean getTowNode(PointF p1, PointF p2, RectF rect) {
        boolean hasNode = true;
        float left = rect.left, top = rect.top, right = rect.right, bottom = rect.bottom;
        float x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;

        boolean isLeft1 = x1 - left < 0, isRight1 = x1 - right > 0, isTop1 = y1 - top < 0, isBottom1 = y1 - bottom > 0;
        boolean isLeft2 = x2 - left < 0, isRight2 = x2 - right > 0, isTop2 = y2 - top < 0, isBottom2 = y2 - bottom > 0;

        if (isLeft1 && isLeft2) {
            p1.x = left;
            p2.x = left;
            if (isTop1) {
                p1.y = top;
            } else if (isBottom1) {
                p1.y = bottom;
            }
            if (isTop2) {
                p2.y = top;
            } else if (isBottom2) {
                p2.y = bottom;
            }
        } else if (isRight1 && isRight2) {
            p1.x = right;
            p2.x = right;
            if (isTop1) {
                p1.y = top;
            } else if (isBottom1) {
                p1.y = bottom;
            }
            if (isTop2) {
                p2.y = top;
            } else if (isBottom2) {
                p2.y = bottom;
            }
        } else if (isTop1 && isTop2) {
            p1.y = top;
            p2.y = top;
            if (isLeft1) {
                p1.x = left;
            } else if (isRight1) {
                p1.x = right;
            }
            if (isLeft2) {
                p2.x = left;
            } else if (isRight2) {
                p2.x = right;
            }
        } else if (isBottom1 && isBottom2) {
            p1.y = bottom;
            p2.y = bottom;
            if (isLeft1) {
                p1.x = left;
            } else if (isRight1) {
                p1.x = right;
            }
            if (isLeft2) {
                p2.x = left;
            } else if (isRight2) {
                p2.x = right;
            }
        } else {

            if (x1 == x2) {
                if (isTop1) {
                    p1.y = top;
                    p2.y = bottom;
                } else {
                    p1.y = bottom;
                    p2.y = top;
                }
            } else {
                float tan2_1 = (y2 - y1) / (x2 - x1);
                float tanTR_1 = (top - y1) / (right - x1);
                float tanBR_1 = (bottom - y1) / (right - x1);
                float tanTL_1 = (top - y1) / (left - x1);
                float tanBL_1 = (bottom - y1) / (left - x1);
                if (isLeft1) {
                    if (isTop1) {
                        if (tan2_1 < tanTR_1) {
                            p1.set(left, top);
                            p2.set(right, top);
                        } else if (tan2_1 > tanBL_1) {
                            p1.set(left, top);
                            p2.set(left, bottom);
                        } else {
                            if (tan2_1 < tanTL_1) {
                                p1.set(x2 + (top - y2) / tan2_1, top);
                            } else {
                                p1.set(left, y2 + (left - x2) * tan2_1);
                            }
                            if (tan2_1 < tanBR_1) {
                                p2.set(right, y1 + (right - x1) * tan2_1);
                            } else {
                                p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                            }
                        }
                    } else if (isBottom1) {
                        if (tan2_1 < tanTL_1) {
                            p1.set(left, bottom);
                            p2.set(left, top);
                        } else if (tan2_1 > tanBR_1) {
                            p1.set(left, bottom);
                            p2.set(right, bottom);
                        } else {
                            if (tan2_1 < tanBL_1) {
                                p1.set(left, y2 + (left - x2) * tan2_1);
                            } else {
                                p1.set(x2 + (bottom - y2) / tan2_1, bottom);
                            }
                            if (tan2_1 < tanTR_1) {
                                p2.set(x1 + (top - y1) / tan2_1, top);
                            } else {
                                p2.set(right, y1 + (right - x1) * tan2_1);
                            }
                        }
                    } else {
                        if (tan2_1 < tanTL_1) {
                            p1.x = left;
                            p2.set(left, top);
                        } else if (tan2_1 > tanBL_1) {
                            p1.x = left;
                            p2.set(left, bottom);
                        } else {
                            p1.set(left, y2 + (left - x2) * tan2_1);
                            if (tan2_1 < tanTR_1) {
                                p2.set(x1 + (top - y1) / tan2_1, top);
                            } else if (tan2_1 < tanBR_1) {
                                p2.set(right, y1 + (right - x1) * tan2_1);
                            } else {
                                p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                            }
                        }
                    }
                } else if (isRight1) {
                    if (isTop1) {
                        if (tan2_1 < tanBR_1) {
                            p1.set(right, top);
                            p2.set(right, bottom);
                        } else if (tan2_1 > tanTL_1) {
                            p1.set(right, top);
                            p2.set(left, top);
                        } else {
                            if (tan2_1 < tanTR_1) {
                                p1.set(right, y2 + (right - x2) * tan2_1);
                            } else {
                                p1.set(x2 + (top - y2) / tan2_1, top);
                            }
                            if (tan2_1 < tanBL_1) {
                                p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                            } else {
                                p2.set(left, y1 + (left - x1) * tan2_1);
                            }
                        }
                    } else if (isBottom1) {
                        if (tan2_1 < tanBL_1) {
                            p1.set(right, bottom);
                            p2.set(left, bottom);
                        } else if (tan2_1 > tanTR_1) {
                            p1.set(right, bottom);
                            p2.set(right, top);
                        } else {
                            if (tan2_1 < tanBR_1) {
                                p1.set(x2 + (bottom - y2) / tan2_1, bottom);
                            } else {
                                p1.set(right, y2 + (right - x2) * tan2_1);
                            }
                            if (tan2_1 < tanTL_1) {
                                p2.set(left, y1 + (left - x1) * tan2_1);
                            } else {
                                p2.set(x1 + (top - y1) / tan2_1, top);
                            }
                        }
                    } else {
                        if (tan2_1 < tanBR_1) {
                            p1.x = right;
                            p2.set(right, bottom);
                        } else if (tan2_1 > tanTR_1) {
                            p1.x = right;
                            p2.set(right, top);
                        } else {
                            p1.set(right, y2 + (right - x2) * tan2_1);
                            if (tan2_1 < tanBL_1) {
                                p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                            } else if (tan2_1 < tanTL_1) {
                                p2.set(left, y1 + (left - x1) * tan2_1);
                            } else {
                                p2.set(x1 + (top - y1) / tan2_1, top);
                            }
                        }
                    }
                } else {
                    if (isTop1) {
                        if (x2 - x1 > 0) {
                            if (tan2_1 < tanTR_1) {
                                p1.y = top;
                                p2.set(right, top);
                            } else if (tan2_1 < tanBR_1) {
                                p1.set(x2 + (top - y2) / tan2_1, top);
                                p2.set(right, y1 + (right - x1) * tan2_1);
                            } else {
                                p1.set(x2 + (top - y2) / tan2_1, top);
                                p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                            }
                        } else {
                            if (tan2_1 > tanTL_1) {
                                p1.y = top;
                                p2.set(left, top);
                            } else if (tan2_1 > tanBL_1) {
                                p1.set(x2 + (top - y2) / tan2_1, top);
                                p2.set(left, y1 + (left - x1) * tan2_1);
                            } else {
                                p1.set(x2 + (top - y2) / tan2_1, top);
                                p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                            }
                        }
                    } else if (isBottom1) {
                        if (x2 - x1 > 0) {
                            if (tan2_1 > tanBR_1) {
                                p1.y = bottom;
                                p2.set(right, bottom);
                            } else if (tan2_1 > tanTR_1) {
                                p1.set(x2 + (bottom - y2) / tan2_1, bottom);
                                p2.set(right, y1 + (right - x1) * tan2_1);
                            } else {
                                p1.set(x2 + (bottom - y2) / tan2_1, bottom);
                                p2.set(x1 + (top - y1) / tan2_1, top);
                            }
                        } else {
                            if (tan2_1 < tanBL_1) {
                                p1.y = bottom;
                                p2.set(left, bottom);
                            } else if (tan2_1 < tanTL_1) {
                                p1.set(x2 + (bottom - y2) / tan2_1, bottom);
                                p2.set(left, y1 + (left - x1) * tan2_1);
                            } else {
                                p1.set(x2 + (bottom - y2) / tan2_1, bottom);
                                p2.set(x1 + (top - y1) / tan2_1, top);
                            }
                        }
                    }
                }
            }
        }
        return hasNode;
    }

    /**
     * 获取两点连线与矩形交点
     * 
     * @param p1
     *            矩形内的点
     * @param p2
     *            矩形外的点，X、Y坐标会修改为最终交点的X、Y值
     * @param rect
     *            矩形
     */
    static public void getOneNode(PointF p1, PointF p2, RectF rect) {
        float left = rect.left, top = rect.top, right = rect.right, bottom = rect.bottom;
        float x2 = p2.x, y2 = p2.y, x1 = p1.x, y1 = p1.y;

        if (x1 == x2) {
            if (y2 < y1) {
                p2.y = top;
            } else {
                p2.y = bottom;
            }
        } else if (x1 == right || x2 == left) {
            p2.x = x1;
            if (y2 < top) {
                p2.y = top;
            } else if (y2 > bottom) {
                p2.y = bottom;
            }
        } else {

            // 相对距离的正切值
            float tan2_1 = (y2 - y1) / (x2 - x1);
            float tanTR_1 = (top - y1) / (right - x1);
            float tanBR_1 = (bottom - y1) / (right - x1);
            float tanTL_1 = (top - y1) / (left - x1);
            float tanBL_1 = (bottom - y1) / (left - x1);

            // 假设x1、y1为原点
            if (y2 < y1) {// Y轴正方向
                if (x2 > x1) {// 第一象限
                    if (tan2_1 < tanTR_1) {// 与TOP有交点
                        p2.set(x1 + (top - y1) / tan2_1, top);
                    } else {// 与right有交点
                        p2.set(right, y1 + (right - x1) * tan2_1);
                    }
                } else {// 第二象限
                    if (tan2_1 > tanTL_1) {// 与top有交点
                        p2.set(x1 + (top - y1) / tan2_1, top);
                    } else {// 与left有交点
                        p2.set(left, y1 + (left - x1) * tan2_1);
                    }
                }
            } else {
                if (x2 > x1) {// 第四象限
                    if (tan2_1 > tanBR_1) {// 与bottom有交点
                        p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                    } else {// 与right有交点
                        p2.set(right, y1 + (right - x1) * tan2_1);
                    }
                } else {// 第三象限
                    if (tan2_1 < tanBL_1) {// 与bottom有交点
                        p2.set(x1 + (bottom - y1) / tan2_1, bottom);
                    } else {// 与left有交点
                        p2.set(left, y1 + (left - x1) * tan2_1);
                    }
                }
            }
        }
    }

    static public boolean isPointInRect(RectF rect, PointF point) {
        return (point.x - rect.left) * (point.x - rect.right) <= 0
                && (point.y - rect.top) * (point.y - rect.bottom) <= 0;
    }

    static public boolean isPointInRect(Bounds rect, GeoPoint point) {
        return (point.getLongitudeE6() / (double) 1E6 - rect.leftBottom.getLongitudeE6() / (double) 1E6)
                * (point.getLongitudeE6() / (double) 1E6 - rect.rightTop.getLongitudeE6() / (double) 1E6) <= 0
                && (point.getLatitudeE6() / (double) 1E6 - rect.leftBottom.getLatitudeE6() / (double) 1E6)
                        * (point.getLatitudeE6() / (double) 1E6 - rect.rightTop.getLatitudeE6() / (double) 1E6) <= 0;
    }

    static public void unionPointAndRect(Bounds rect, GeoPoint point) {
        if (point.getLongitudeE6() < rect.leftBottom.getLongitudeE6()) {
            rect.leftBottom.setLongitudeE6(point.getLongitudeE6());
        } else if (point.getLongitudeE6() > rect.rightTop.getLongitudeE6()) {
            rect.rightTop.setLongitudeE6(point.getLongitudeE6());
        }
        if (point.getLatitudeE6() > rect.rightTop.getLatitudeE6()) {
            rect.rightTop.setLatitudeE6(point.getLatitudeE6());
        } else if (point.getLatitudeE6() < rect.leftBottom.getLatitudeE6()) {
            rect.leftBottom.setLatitudeE6(point.getLatitudeE6());
        }
    }
}
