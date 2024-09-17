package com.cgzz.mapbox.jturf.util.coordinatemutation;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.shape.Geometry;

public class TruncateHelper {

    private TruncateHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 坐标小数处理
     *
     * @param geometry    图形组件
     * @param precision   保留的小数位数，如果传入空则保留6位小数
     * @param coordinates 最大坐标数 (主要用于删除 Z 坐标)（默认3）
     * @param mutate      是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry, Integer precision, Integer coordinates, boolean mutate) {
        if (precision == null || precision < 0) {
            precision = 6;
        }
        if (coordinates == null || coordinates <= 0 || coordinates > 3) {
            coordinates = 3;
        }

        T newGeometry;
        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        double factor = Math.pow(10, precision);
        int coords = coordinates;

        JTurfMeta.coordEach(geometry, (p, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            if (coords == 1) {
                p.setCoords(calc(p.longitude(), factor), Double.NaN, Double.NaN);
            } else if (coords == 2) {
                p.setCoords(calc(p.longitude(), factor), calc(p.latitude(), factor), Double.NaN);
            } else {
                if (p.hasAltitude()) {
                    p.setCoords(calc(p.longitude(), factor), calc(p.latitude(), factor));
                } else {
                    p.setCoords(calc(p.longitude(), factor), calc(p.latitude(), factor), calc(p.altitude(), factor));
                }
            }

            return true;
        });

        return newGeometry;
    }

    private static double calc(double d, double factor) {
        return Math.round(d * factor) / factor;
    }

}
