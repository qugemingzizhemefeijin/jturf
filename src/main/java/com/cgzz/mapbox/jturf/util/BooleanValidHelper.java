package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.shape.*;

import java.util.List;

/**
 * 检查图形是否是有效的辅助类
 */
public final class BooleanValidHelper {

    public BooleanValidHelper() {
        throw new AssertionError("No Instances.");
    }

    public static boolean booleanValid(Geometry geometry) {
        GeometryType t = geometry.type();
        switch (t) {
            case POINT:
                return true;
            case MULTI_POINT:
                return booleanValid(MultiPoint.multiPoint(geometry));
            case LINE:
                return booleanValid(Line.line(geometry));
            case MULTI_LINE:
                return booleanMultiPointValid(MultiLine.multiLine(geometry).coordinates());
            case POLYGON:
                return booleanValid(Polygon.polygon(geometry));
            case MULTI_POLYGON:
                return booleanValid(MultiPolygon.multiPolygon(geometry));
            default:
                return false;
        }
    }

    private static boolean booleanValid(Line line) {
        List<Point> pointList = line.coordinates();

        return pointList != null && pointList.size() >= 2;
    }

    private static boolean booleanValid(MultiPoint multiPoint) {
        List<Point> pointList = multiPoint.coordinates();

        return pointList != null && !pointList.isEmpty();
    }

    private static boolean booleanValid(Polygon polygon) {
        return booleanValidPolygonPoints(polygon.coordinates());
    }

    private static boolean booleanValid(MultiPolygon multiPolygon) {
        List<List<Point>> multiPointList = multiPolygon.coordinates();
        if (multiPointList == null || multiPointList.isEmpty()) {
            return false;
        }

        for (int i = 0, size = multiPointList.size(); i < size; i++) {
            List<Point> pointList = multiPointList.get(i);
            if (!booleanValidPolygonPoints(pointList)) {
                return false;
            }
            if (i == 0) {
                // 如果传入的点集合与另一个点组集合是否正常（不相交，但是又交叉）
                if (!JTurfHelper.checkPolygonAgainstOthers(pointList, multiPointList, i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean booleanValidPolygonPoints(List<Point> pointList) {
        if (pointList == null || pointList.size() < 4) {
            return false;
        }
        if (!JTurfHelper.checkRingsClose(pointList)) {
            return false;
        }
        return !JTurfHelper.checkRingsForSpikesPunctures(pointList);
    }

    private static boolean booleanMultiPointValid(List<List<Point>> multiPointList) {
        if (multiPointList == null || multiPointList.isEmpty()) {
            return false;
        }
        for (List<Point> pointList : multiPointList) {
            if (pointList == null || pointList.isEmpty()) {
                return false;
            }
        }
        return true;
    }


}
