package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Point;

import java.util.Arrays;
import java.util.List;

public final class JTurfHelper {

    private JTurfHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 比较两个坐标点是否一致
     *
     * @param pt1 Point
     * @param pt2 Point
     * @return 一致返回true
     */
    public static boolean equals(Point pt1, Point pt2) {
        return pt1 == pt2;
    }

    /**
     * 将传入的参数转换成集合
     *
     * @param lon 经度
     * @param lat 纬度
     * @return List<Double>
     */
    public static List<Double> lonLatToList(double lon, double lat) {
        return Arrays.asList(lon, lat);
    }

    /**
     * 将传入的参数转换成集合
     *
     * @param lon 经度
     * @param lat 纬度
     * @param alt 高度
     * @return List<Double>
     */
    public static List<Double> lonLatAltToList(double lon, double lat, double alt) {
        return Double.isNaN(alt)
                ? Arrays.asList(lon, lat) :
                Arrays.asList(lon, lat, alt);
    }

}
