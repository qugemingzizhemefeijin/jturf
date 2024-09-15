package com.cgzz.mapbox.jturf.util.transformation;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Point;

public final class TransformRotateHelper {

    private TransformRotateHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 旋转
     * <p>
     * 旋转任何组件，围绕其质心或一个给定的枢轴点;所有的旋转都遵循右手规则:https://en.wikipedia.org/wiki/righthand_rule
     *
     * @param geometry 要旋转的组件
     * @param angle    旋转角度，顺时针方向
     * @param pivot    围绕其执行旋转的点，为空默认为质心
     * @param mutate   是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回旋转后的图形组件
     */
    public static <T extends Geometry> T transformRotate(T geometry, double angle, Point pivot, boolean mutate) {
        if (angle == 0) {
            return geometry;
        }

        Point rotatePoint = pivot == null ? JTurfMeasurement.centroid(geometry) : pivot;
        T newGeometry = mutate ? geometry : JTurfTransformation.clone(geometry);

        // Rotate each coordinate
        JTurfMeta.coordEach(newGeometry, (p, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            double initialAngle = JTurfMeasurement.rhumbBearing(rotatePoint, p);
            double finalAngle = initialAngle + angle;
            double distance = JTurfMeasurement.rhumbDistance(pivot, p);

            // 计算新的点位
            Point newCoords = JTurfMeasurement.rhumbDestination(pivot, distance, finalAngle);
            // 更新点坐标
            p.setCoords(newCoords);

            return true;
        });

        return newGeometry;
    }

}
