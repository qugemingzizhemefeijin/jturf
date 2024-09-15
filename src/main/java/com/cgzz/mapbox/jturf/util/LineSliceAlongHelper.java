package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class LineSliceAlongHelper {

    private LineSliceAlongHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param line        线段
     * @param startDist   沿线到起点的起始距离
     * @param stopDist    沿线到终点的停靠点距离
     * @param units       距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @param properties  属性信息，一般继承自line
     * @return 切片线段
     */
    public static Feature lineSliceAlong(LineString line, double startDist, double stopDist, Units units, JsonObject properties) {
        if (units == null) {
            units = Units.KILOMETERS;
        }

        List<Point> slice = new ArrayList<>();
        List<Point> coords = line.coordinates();
        int origCoordsLength = coords.size();
        double travelled = 0;
        double overshot, direction;
        Point interpolated;
        for (int i = 0; i < origCoordsLength; i++) {
            if (startDist >= travelled && i == origCoordsLength - 1) {
                break;
            } else if (travelled > startDist && slice.size() == 0) {
                overshot = startDist - travelled;
                if (overshot == 0) {
                    slice.add(coords.get(i));
                    return Feature.fromGeometry(LineString.fromLngLats(slice), properties);
                }
                direction = JTurfMeasurement.bearing(coords.get(i), coords.get(i - 1)) - 180;
                interpolated = JTurfMeasurement.destination(coords.get(i), overshot, direction, units);
                slice.add(interpolated);
            }

            if (travelled >= stopDist) {
                overshot = stopDist - travelled;
                if (overshot == 0) {
                    slice.add(coords.get(i));
                    return Feature.fromGeometry(LineString.fromLngLats(slice), properties);
                }
                direction = JTurfMeasurement.bearing(coords.get(i), coords.get(i - 1)) - 180;
                interpolated = JTurfMeasurement.destination(coords.get(i), overshot, direction, units);
                slice.add(interpolated);
                return Feature.fromGeometry(LineString.fromLngLats(slice), properties);
            }

            if (travelled >= startDist) {
                slice.add(coords.get(i));
            }

            if (i == origCoordsLength - 1) {
                return Feature.fromGeometry(LineString.fromLngLats(slice), properties);
            }

            travelled += JTurfMeasurement.distance(coords.get(i), coords.get(i + 1), units);
        }

        if (travelled < startDist) {
            throw new JTurfException("Start position is beyond line");
        }

        Point last = coords.get(origCoordsLength - 1);
        return Feature.fromGeometry(LineString.fromLngLats(last, last), properties);
    }

}
