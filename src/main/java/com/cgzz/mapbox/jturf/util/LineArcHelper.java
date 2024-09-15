package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class LineArcHelper {

    private LineArcHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     *
     * @param center     中心点
     * @param radius     圆的半径
     * @param bearing1   方位角1 圆弧第一半径的角度
     * @param bearing2   方位角2 圆弧第二半径的角度
     * @param steps      步长，不传入则默认为64
     * @param units      单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @param properties 返回的Feature的属性信息，一般继承自center
     * @return 返回弧度的线条
     */
    public static Feature lineArc(Point center, double radius, double bearing1, double bearing2, Integer steps, Units units, JsonObject properties) {
        if (steps == null) {
            steps = 64;
        }
        if (units == null) {
            units = Units.KILOMETERS;
        }

        double angle1 = JTurfHelper.convertAngleTo360(bearing1);
        double angle2 = JTurfHelper.convertAngleTo360(bearing2);

        // handle angle parameters
        if (angle1 == angle2) {
            Polygon circle = JTurfTransformation.circle(center, radius, steps, units);
            return Feature.fromGeometry(LineString.fromLngLats(circle.coordinates().get(0)), properties);
        }

        double arcStartDegree = angle1;
        double arcEndDegree = angle1 < angle2 ? angle2 : angle2 + 360;

        double alpha = arcStartDegree;
        List<Point> coordinates = new ArrayList<>();
        int i = 0;

        // How many degrees we'll swing around between each step.
        double arcStep = (arcEndDegree - arcStartDegree) / steps;
        // Add coords to the list, increasing the angle from our start bearing
        // (alpha) by arcStep degrees until we reach the end bearing.
        while (alpha <= arcEndDegree) {
            coordinates.add(JTurfMeasurement.destination(center, radius, alpha, units));
            i++;
            alpha = arcStartDegree + i * arcStep;
        }
        return Feature.fromGeometry(LineString.fromLngLats(coordinates), properties);
    }

}
