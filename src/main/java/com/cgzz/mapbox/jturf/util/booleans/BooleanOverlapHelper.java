package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.IntHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

public final class BooleanOverlapHelper {

    private BooleanOverlapHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 判断是否重叠。<br>
     * <br>
     * 比较相同维度的两个几何图形，如果它们的交集集产生的几何图形与两个几何图形不同，但维度相同，则返回true。
     *
     * @param geom1 图形1，支持Line、MultiLine、Polygon、MultiPolygon
     * @param geom2 图形2，支持Line、MultiLine、Polygon、MultiPolygon
     * @return 如果重叠则返回true
     */
    public static boolean booleanOverlap(Geometry geom1, Geometry geom2) {
        Geometry geometry1 = JTurfMeta.getGeom(geom1);
        Geometry geometry2 = JTurfMeta.getGeom(geom2);

        GeometryType type1 = geometry1.geometryType(), type2 = geometry2.geometryType();

        if ((type1 == GeometryType.MULTI_POINT && type2 != GeometryType.MULTI_POINT) ||
                ((type1 == GeometryType.LINE_STRING || type1 == GeometryType.MULTI_LINE_STRING)
                        && type2 != GeometryType.LINE_STRING && type2 != GeometryType.MULTI_LINE_STRING) ||
                ((type1 == GeometryType.POLYGON || type1 == GeometryType.MULTI_POLYGON) && type2 != GeometryType.POLYGON
                        && type2 != GeometryType.MULTI_POLYGON)
        ) {
            throw new JTurfException("features must be of the same type");
        }
        if (type1 == GeometryType.POINT) {
            throw new JTurfException("Point geometry not supported");
        }

        // features must be not equal 不知道为什么要有这一段？？
        if (GeoJsonEquality.compare(geometry1, geometry2, 6)) {
            return false;
        }

        IntHolder overlap = new IntHolder();
        switch (type1) {
            case MULTI_POINT:
                List<Point> pointList1 = MultiPoint.multiPoint(geometry1).coordinates();
                List<Point> pointList2 = MultiPoint.multiPoint(geometry2).coordinates();
                for (Point p1 : pointList1) {
                    for (Point p2 : pointList2) {
                        if (JTurfHelper.equals(p1, p2)) {
                            return true;
                        }
                    }
                }
                return false;
            case LINE_STRING:
            case MULTI_LINE_STRING:
                JTurfMeta.segmentEach(geometry1, (currentSegment1, featureIndex1, multiFeatureIndex1, geometryIndex1, segmentIndex1) -> {
                    JTurfMeta.segmentEach(geometry2, (currentSegment2, featureIndex2, multiFeatureIndex2, geometryIndex2, segmentIndex2) -> {
                        List<LineString> overlapLines = JTurfMisc.lineOverlap(currentSegment1, currentSegment2);
                        if (overlapLines.size() > 0) {
                            overlap.value++;
                        }
                        return true;
                    });

                    return true;
                });
                break;
            case POLYGON:
            case MULTI_POLYGON:
                JTurfMeta.segmentEach(geometry1, (currentSegment1, featureIndex1, multiFeatureIndex1, geometryIndex1, segmentIndex1) -> {
                    JTurfMeta.segmentEach(geometry2, (currentSegment2, featureIndex2, multiFeatureIndex2, geometryIndex2, segmentIndex2) -> {
                        FeatureCollection<Point> intersectLines = JTurfMisc.lineIntersect(currentSegment1, currentSegment2);
                        if (intersectLines != null && intersectLines.size() > 0) {
                            overlap.value++;
                        }
                        return true;
                    });

                    return true;
                });
                break;
        }

        return overlap.value > 0;
    }

}
