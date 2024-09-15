package com.cgzz.mapbox.jturf.util.measurement;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.impl.Point;

public final class RhumbDestinationHelper {

    private RhumbDestinationHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 根据点、距离和角度计算目标点
     * <p>
     * 返回从原点出发，沿大圆线行驶给定距离和给定方位角后到达的终点。
     *
     * @param origin   开始点
     * @param distance 从起点的距离
     * @param bearing  方位角从北向南的范围在 -180 到 180 度之间
     * @param units    距离单位
     * @return 返回目标点
     */
    public static Point rhumbDestination(Point origin, double distance, double bearing, Units units) {
        if (units == null) {
            units = Units.KILOMETERS;
        }

        boolean wasNegativeDistance = distance < 0;
        double distanceInMeters = JTurfHelper.convertLength(Math.abs(distance), units, Units.METERS);

        if (wasNegativeDistance) {
            distanceInMeters = -Math.abs(distanceInMeters);
        }

        double[] coords = origin.getCoords();
        double[] destination = calculateRhumbDestination(coords, distanceInMeters, bearing);

        // compensate the crossing of the 180th meridian (https://macwright.org/2016/09/26/the-180th-meridian.html)
        // solution from https://github.com/mapbox/mapbox-gl-js/issues/3250#issuecomment-294887678

        destination[0] += destination[0] - coords[0] > 180 ? -360 : coords[0] - destination[0] > 180 ? 360 : 0;

        return Point.fromLngLat(destination);
    }

    /**
     * 返回从给定的原点沿恒向线行进的目标点给定方位角上的距离。
     * <p>
     * Adapted from Geodesy: http://www.movable-type.co.uk/scripts/latlong.html#rhumblines
     *
     * @param origin   坐标点
     * @param distance 距离，单位与地球半径相同（默认值：米）。
     * @param bearing  角度，范围从 -180 到 180
     * @return 返回目标点坐标
     */
    private static double[] calculateRhumbDestination(double[] origin, double distance, double bearing) {
        double delta = distance / JTurfHelper.EARTH_RADIUS; // angular distance in radians
        double lambda1 = (origin[0] * Math.PI) / 180; // to radians, but without normalize to 𝜋
        double phi1 = JTurfHelper.degreesToRadians(origin[1]);
        double theta = JTurfHelper.degreesToRadians(bearing);

        double DeltaPhi = delta * Math.cos(theta);
        double phi2 = phi1 + DeltaPhi;

        // check for some daft bugger going past the pole, normalise latitude if so
        if (Math.abs(phi2) > Math.PI / 2) {
            phi2 = phi2 > 0 ? Math.PI - phi2 : -Math.PI - phi2;
        }

        double DeltaPsi = Math.log(Math.tan(phi2 / 2 + Math.PI / 4) / Math.tan(phi1 / 2 + Math.PI / 4));
        // E-W course becomes ill-conditioned with 0/0
        double q = Math.abs(DeltaPsi) > 10e-12 ? DeltaPhi / DeltaPsi : Math.cos(phi1);

        double DeltaLambda = (delta * Math.sin(theta)) / q;
        double lambda2 = lambda1 + DeltaLambda;

        return new double[]{
                (((lambda2 * 180) / Math.PI + 540) % 360) - 180,
                (phi2 * 180) / Math.PI,
        }; // normalise to −180..+180°
    }

}
