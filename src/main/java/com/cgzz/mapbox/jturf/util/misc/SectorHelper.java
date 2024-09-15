package com.cgzz.mapbox.jturf.util.misc;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SectorHelper {

    private SectorHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 计算扇形多边形<br>
     * <p>
     * 创建给定半径和中心点的圆的一个扇形，位于(顺时针)bearing1和bearing2之间;0方位为中心点以北，顺时针正。
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 扇区第一半径的角度
     * @param bearing2 扇区第二半径的角度
     * @param steps    步长，不传入则默认为64
     * @param units    距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 扇区多边形
     */
    public static Polygon sector(Point center, int radius, double bearing1, double bearing2, Integer steps, Units units) {
        if (center == null) {
            throw new JTurfException("center is required");
        }
        if (units == null) {
            units = Units.KILOMETERS;
        }
        if (steps == null) {
            steps = 64;
        }

        if (JTurfHelper.convertAngleTo360(bearing1) == JTurfHelper.convertAngleTo360(bearing2)) {
            return JTurfTransformation.circle(center, radius, steps, units);
        }

        Feature arc = JTurfMisc.lineArc(center, radius, bearing1, bearing2);

        List<Point> sliceCoords = new ArrayList<>();
        sliceCoords.add(center);

        JTurfMeta.coordEach(arc, (point, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            sliceCoords.add(point);

            return true;
        });
        sliceCoords.add(center);

        return Polygon.fromLngLats(Collections.singletonList(sliceCoords));
    }

}
