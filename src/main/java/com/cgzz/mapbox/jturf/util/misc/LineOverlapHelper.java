package com.cgzz.mapbox.jturf.util.misc;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfHelper;
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
import com.cgzz.mapbox.jturf.util.pkg.RTreeHelper;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Rectangle;
import rx.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LineOverlapHelper {

    private LineOverlapHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 计算两个图形之间重叠的线
     *
     * @param geometry1 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param geometry2 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param tolerance 匹配重叠线段的容差距离（以公里为单位）
     * @return 返回两个图形之间重叠的线段集合
     */
    public static FeatureCollection<LineString> lineOverlap(Geometry geometry1, Geometry geometry2, Integer tolerance) {
        if (geometry1 == null) {
            throw new JTurfException("geometry1 is required");
        }
        if (geometry2 == null) {
            throw new JTurfException("geometry2 is required");
        }

        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);

        GeometryType t1 = geometry1.geometryType(), t2 = geometry2.geometryType();
        if (t1 != GeometryType.LINE_STRING
                && t1 != GeometryType.POLYGON
                && t1 != GeometryType.MULTI_LINE_STRING
                && t1 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry1 type must LineString or Polygon or MultiLineString or MultiPolygon");
        }
        if (t2 != GeometryType.LINE_STRING
                && t2 != GeometryType.POLYGON
                && t2 != GeometryType.MULTI_LINE_STRING
                && t2 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry2 type must LineString or Polygon or MultiLineString or MultiPolygon");
        }

        int disparity = tolerance == null ? 0 : tolerance; // 差距
        Units units = Units.KILOMETERS; // 计算距离的单位为公里

        // 构建R树
        RTree<LineString, Rectangle> rtree = RTreeHelper.initRTree(geometry1);

        List<Feature<LineString>> result = new ArrayList<>();
        ObjectHolder<List<Point>> overlapSegment = new ObjectHolder<>(new ArrayList<>()); // 线段点集合

        JTurfMeta.segmentEach(geometry2, (segment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex) -> {
            boolean doesOverlaps = false;

            Observable<Entry<LineString, Rectangle>> entries = rtree.search(RTreeHelper.createRectangle(segment));
            // 迭代落在相同边界内的每个段
            for (Entry<LineString, Rectangle> p : entries.toBlocking().toIterable()) {
                if (doesOverlaps) {
                    break;
                }

                LineString match = p.value();

                List<Point> coordsSegment = new ArrayList<>(segment.coordinates());
                List<Point> coordsMatch = new ArrayList<>(match.coordinates());

                Collections.sort(coordsSegment);
                Collections.sort(coordsMatch);

                // 线段重叠
                if (JTurfHelper.deepEquals(coordsSegment, coordsMatch)) {
                    doesOverlaps = true;

                    // 重叠已存在 - 仅附加线段的最后一个坐标
                    if (overlapSegment.value.size() > 0) {
                        concatSegment(overlapSegment.value, segment);
                    } else {
                        overlapSegment.value = segment.coordinates();
                    }
                } else if (disparity == 0
                        ? (JTurfBooleans.booleanPointOnLine(coordsSegment.get(0), match)
                        && JTurfBooleans.booleanPointOnLine(coordsSegment.get(1), match))
                        : (JTurfMisc.nearestPointOnLine(match, coordsSegment.get(0), units).getPropertyAsNumber("dist").intValue() <= disparity
                        && JTurfMisc.nearestPointOnLine(match, coordsSegment.get(1), units).getPropertyAsNumber("dist").intValue() <= disparity)) {
                    doesOverlaps = true;
                    if (overlapSegment.value.size() > 0) {
                        concatSegment(overlapSegment.value, segment);
                    } else {
                        overlapSegment.value = segment.coordinates();
                    }
                } else if (disparity == 0
                        ? (JTurfBooleans.booleanPointOnLine(coordsMatch.get(0), segment)
                        && JTurfBooleans.booleanPointOnLine(coordsMatch.get(1), segment))
                        : (JTurfMisc.nearestPointOnLine(segment, coordsMatch.get(0), units).getPropertyAsNumber("dist").intValue() <= disparity
                        && JTurfMisc.nearestPointOnLine(segment, coordsMatch.get(1), units).getPropertyAsNumber("dist").intValue() <= disparity)) {
                    // 不要定义（doesOverlap = true），因为同一段中可能会出现更多匹配项
                    if (overlapSegment.value.size() > 0) {
                        concatSegment(overlapSegment.value, match);
                    } else {
                        overlapSegment.value = match.coordinates();
                    }
                }
            }

            // Segment 不重叠 - 向结果添加重叠并重置
            if (!doesOverlaps && overlapSegment.value.size() > 0) {
                result.add(Feature.fromGeometry(LineString.fromLngLatsShallowCopy(overlapSegment.value)));
                overlapSegment.value.clear();
            }

            return true;
        });

        // 添加最后一个段（如果存在）
        if (overlapSegment.value.size() > 0) {
            result.add(Feature.fromGeometry(LineString.fromLngLatsShallowCopy(overlapSegment.value)));
        }

        return FeatureCollection.fromFeatures(result);
    }

    private static void concatSegment(List<Point> lineCoords, LineString segment) {
        List<Point> coords = segment.coordinates();

        Point start = lineCoords.get(0);
        Point end = lineCoords.get(lineCoords.size() - 1);

        Point s = coords.get(0), e = coords.get(1);

        if (JTurfHelper.equals(s, start)) {
            lineCoords.add(0, e);
        } else if (JTurfHelper.equals(s, end)) {
            lineCoords.add(e);
        } else if (JTurfHelper.equals(e, start)) {
            lineCoords.add(0, s);
        } else if (JTurfHelper.equals(e, end)) {
            lineCoords.add(s);
        }
    }

}
