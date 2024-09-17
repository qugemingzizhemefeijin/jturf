package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

public final class BooleanPointOnLineHelper {

    private BooleanPointOnLineHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 如果点位于直线上，则返回 true。接受可选参数以忽略线串的开始和结束顶点。
     *
     * @param point             要判断的点
     * @param lineString        线
     * @param ignoreEndVertices 是否忽略线段的起始和终止顶点
     * @param epsilon           要与交叉乘积结果进行比较的小数。用于处理浮点，例如经纬度点
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, LineString lineString, boolean ignoreEndVertices, Number epsilon) {
        if (point == null) {
            throw new JTurfException("point is required");
        }
        if (lineString == null) {
            throw new JTurfException("line is required");
        }

        List<Point> pointList = lineString.coordinates();
        if (pointList == null || pointList.isEmpty()) {
            return false;
        }

        for (int i = 0, size = pointList.size(); i < size - 1; i++) {
            int ignoreBoundary = 0;
            if (ignoreEndVertices) {
                if (i == 0) {
                    ignoreBoundary = 1; // start
                }
                if (i == size - 2) {
                    ignoreBoundary = 2; // end
                }
                if (i == 0 && i + 1 == size - 1) {
                    ignoreBoundary = 3; // both
                }
            }

            if (JTurfHelper.isPointOnLineSegment(pointList.get(i), pointList.get(i + 1), point, ignoreBoundary, epsilon)) {
                return true;
            }
        }

        return false;
    }

}
