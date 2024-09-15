package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class LineChunkHelper {

    private LineChunkHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 分割多线段<br>
     * <p>
     * 将一个Line分割成指定长度的块。如果Line比分隔段长度短，则返回原始Line
     *
     * @param geometry      线段，支持 Line、MultiLine
     * @param segmentLength 每个段的长度
     * @param units         距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @param reverse       反转坐标以在末尾开始第一个分块段
     * @return 线段集合
     */
    public static FeatureCollection lineChunk(Geometry geometry, double segmentLength, Units units, boolean reverse) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }

        geometry = JTurfMeta.getGeom(geometry);
        if (geometry.geometryType() != GeometryType.LINE_STRING && geometry.geometryType() != GeometryType.MULTI_LINE_STRING) {
            throw new JTurfException("geometry type must support Line or MultiLine");
        }
        if (segmentLength <= 0) {
            throw new JTurfException("segmentLength must be greater than 0");
        }

        Units u;
        if (units == null) {
            u = Units.KILOMETERS;
        } else {
            u = units;
        }

        List<Feature> results = new ArrayList<>();

        // Flatten each feature to simple Line
        JTurfMeta.flattenEach(geometry, (f, featureIndex, multiFeatureIndex) -> {
            LineString line = LineString.lineString(f.geometry());

            // reverses coordinates to start the first chunked segment at the end
            List<Point> pointList;
            if (reverse) {
                List<Point> temp = new ArrayList<>(line.coordinates());
                Collections.reverse(temp);
                pointList = temp;
            } else {
                pointList = line.coordinates();
            }

            sliceLineSegments(LineString.fromLngLats(pointList), segmentLength, u, results::add);

            return true;
        });

        return FeatureCollection.fromFeatures(results);
    }

    /**
     * 对线段进行切片
     *
     * @param line          要分片的线段
     * @param segmentLength 每个段的长度
     * @param units         距离单位
     * @param callback      回调函数
     */
    private static void sliceLineSegments(LineString line, double segmentLength, Units units, Consumer<Feature> callback) {
        double lineLength = JTurfMeasurement.length(line, units);

        // If the line is shorter than the segment length then the orginal line is returned.
        if (lineLength <= segmentLength) {
            callback.accept(Feature.fromGeometry(line));
            return;
        }

        double numberOfSegments = lineLength / segmentLength;

        // If numberOfSegments is integer, no need to plus 1
        if (numberOfSegments != Math.floor(numberOfSegments)) {
            numberOfSegments = Math.floor(numberOfSegments) + 1;
        }

        for (double i = 0; i < numberOfSegments; i++) {
            Feature outline = JTurfMisc.lineSliceAlong(line, segmentLength * i, segmentLength * (i + 1), units);
            callback.accept(outline);
        }
    }

}
