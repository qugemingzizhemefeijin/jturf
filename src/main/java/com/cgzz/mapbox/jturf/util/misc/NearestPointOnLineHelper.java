package com.cgzz.mapbox.jturf.util.misc;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.ObjectHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import org.omg.CORBA.DoubleHolder;
import org.omg.CORBA.IntHolder;

import java.util.List;

public final class NearestPointOnLineHelper {

    private NearestPointOnLineHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 计算点到多线段的最短间距的点
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @param units 距离单位
     * @return 返回线段上最短距离的点
     */
    public static Feature nearestPointOnLine(Geometry lines, Point pt, Units units) {
        if (lines == null) {
            throw new JTurfException("lines is required");
        }
        if (pt == null) {
            throw new JTurfException("pt is required");
        }

        lines = JTurfMeta.getGeom(lines);

        if (lines.geometryType() != GeometryType.LINE_STRING && lines.geometryType() != GeometryType.MULTI_LINE_STRING) {
            throw new JTurfException("geometry2 type must Line or MultiLine");
        }
        if (units != Units.DEGREES
                && units != Units.RADIANS
                && units != Units.MILES
                && units != Units.KILOMETERS
                && units != Units.KILOMETRES) {
            throw new JTurfException("units can be degrees, radians, miles, or kilometers");
        }

        ObjectHolder<Point> closestPt = new ObjectHolder<>(Point.fromLngLat(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        DoubleHolder closestDist = new DoubleHolder(Double.POSITIVE_INFINITY);
        IntHolder closestIndex = new IntHolder();
        DoubleHolder closestLocation = new DoubleHolder();

        DoubleHolder length = new DoubleHolder();

        JTurfMeta.flattenEach(lines, ((f, featureIndex, multiFeatureIndex) -> {
            List<Point> coords = LineString.lineString(f.geometry()).coordinates();

            for (int i = 0, size = coords.size(); i < size - 1; i++) {
                Point start = coords.get(i);
                double startDist = JTurfMeasurement.distance(pt, start, units);

                Point stop = coords.get(i + 1);
                double stopDist = JTurfMeasurement.distance(pt, stop, units);

                double sectionLength = JTurfMeasurement.distance(start, stop, units);

                double heightDistance = Math.max(startDist, stopDist);
                // 计算方位角
                double direction = JTurfMeasurement.bearing(start, stop);

                Point perpendicularPt1 = JTurfMeasurement.destination(pt, heightDistance, direction + 90, units);
                Point perpendicularPt2 = JTurfMeasurement.destination(pt, heightDistance, direction - 90, units);

                // 计算两个线的相交点
                FeatureCollection intersect = JTurfMisc.lineIntersect(LineString.fromLngLats(perpendicularPt1, perpendicularPt2), LineString.fromLngLats(start, stop));

                Point intersectPt = null;
                double intersectDist = 0, intersectLocation = 0;
                if (intersect != null && intersect.size() > 0) {
                    intersectPt = (Point)intersect.get(0).geometry();
                    intersectDist = JTurfMeasurement.distance(pt, intersectPt, units);
                    intersectLocation = length.value + JTurfMeasurement.distance(start, intersectPt, units);
                }

                if (startDist < closestDist.value) {
                    closestPt.value = start;
                    closestDist.value = startDist;
                    closestIndex.value = i;
                    closestLocation.value = length.value;
                }
                if (stopDist < closestDist.value) {
                    closestPt.value = stop;
                    closestDist.value = stopDist;
                    closestIndex.value = i + 1;
                    closestLocation.value = length.value + sectionLength;
                }
                if (intersectPt != null && intersectDist < closestDist.value) {
                    closestPt.value = intersectPt;
                    closestDist.value = intersectDist;
                    closestIndex.value = i;
                    closestLocation.value = intersectLocation;
                }

                // update length
                length.value += sectionLength;
            }

            return true;
        }));

        Feature p = Feature.fromGeometry(Point.fromLngLat(closestPt.value));
        p.addProperty("dist", closestDist.value);
        p.addProperty("index", closestIndex.value);
        p.addProperty("location", closestLocation.value);

        return p;
    }

}
