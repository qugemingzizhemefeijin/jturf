package com.cgzz.mapbox.jturf.util.transformation;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.enums.Orientation;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.BoundingBox;
import com.cgzz.mapbox.jturf.shape.impl.Point;

public final class TransformScaleHelper {

    public TransformScaleHelper() {
        throw new AssertionError("No Instances.");
    }

    public static Point defineOrigin(Geometry geometry, Orientation orientation) {
        if (orientation == null) {
            orientation = Orientation.CENTROID;
        }

        BoundingBox bbox = JTurfMeasurement.bbox(geometry);

        double west = bbox.west(), south = bbox.south(), east = bbox.east(), north = bbox.north();

        switch (orientation) {
            case SW:
            case SOUTH_WEST:
            case WEST_SOUTH:
            case BOTTOM_LEFT:
                return Point.fromLngLat(west, south);
            case SE:
            case SOUTH_EAST:
            case EAST_SOUTH:
            case BOTTOM_RIGHT:
                return Point.fromLngLat(east, south);
            case NW:
            case NORTH_WEST:
            case WEST_NORTH:
            case TOP_LEFT:
                return Point.fromLngLat(west, north);
            case NE:
            case NORTH_EAST:
            case EAST_NORTH:
            case TOP_RIGHT:
                return Point.fromLngLat(east, north);
            case CENTER:
                return JTurfMeasurement.center(geometry);
            case CENTROID:
                return JTurfMeasurement.centroid(geometry);
            default:
                throw new JTurfException("invalid origin");
        }
    }

    /**
     * 缩放
     *
     * @param geometry 要缩放的组件
     * @param factor   缩放比
     * @param origin   缩放开始的点
     * @return 返回缩放后的组件
     */
    public static <T extends Geometry> T scale(T geometry, double factor, Point origin) {
        // 点不支持缩放，factory=1也不支持
        if (factor == 1 || geometry.geometryType() == GeometryType.POINT) {
            return geometry;
        }

        // Scale each coordinate
        JTurfMeta.coordEach(geometry, (coord, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            double originalDistance = JTurfMeasurement.rhumbDistance(origin, coord);
            double bearing = JTurfMeasurement.rhumbBearing(origin, coord);
            double newDistance = originalDistance * factor;
            Point newCoord = JTurfMeasurement.rhumbDestination(origin, newDistance, bearing);

            // 修改原坐标点
            coord.setCoords(newCoord);

            return true;
        });

        return geometry;
    }

}
