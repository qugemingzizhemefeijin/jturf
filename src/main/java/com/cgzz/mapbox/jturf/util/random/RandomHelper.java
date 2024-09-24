package com.cgzz.mapbox.jturf.util.random;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public final class RandomHelper {

    private RandomHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 返回一个随机的点
     *
     * @param bbox 指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON Feature of point
     */
    public static Feature<Point> randomPoint(BoundingBox bbox) {
        double[] b = bbox != null ? bbox.bbox() : null;

        return Feature.fromGeometry(randomPositionUnchecked(b));
    }

    /**
     * 返回一组随机的点
     *
     * @param count 指定生成随机点的数量
     * @param bbox  指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON FeatureCollection of point
     */
    public static FeatureCollection<Point> randomPoint(int count, BoundingBox bbox) {
        if (count <= 0) {
            count = 1;
        }
        double[] b = bbox != null ? bbox.bbox() : null;

        List<Feature<Point>> features = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            features.add(Feature.fromGeometry(randomPositionUnchecked(b)));
        }

        return FeatureCollection.fromFeatures(features);
    }

    /**
     * 返回随机的线段
     *
     * @param bbox        指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices 每个 LineString 将包含的坐标数。（默认 10）
     * @param maxLength   点与其前驱点之间可以相差的最大水平距离（默认为 0.0001）
     * @param maxRotation 线段可以从上一条线段转动的最大弧度数。（默认为 Math.PI/8）
     * @return GeoJSON Feature of linestring
     */
    public static Feature<LineString> randomLineString(BoundingBox bbox, Integer numVertices, Double maxLength, Double maxRotation) {
        return randomLineString(1, bbox, numVertices, maxLength, maxRotation).get(0);
    }

    /**
     * 返回一组随机的线段
     *
     * @param count       指定生成随机线段的数量
     * @param bbox        指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices 每个 LineString 将包含的坐标数。（默认 10）
     * @param maxLength   点与其前驱点之间可以相差的最大水平距离（默认为 0.0001）
     * @param maxRotation 线段可以从上一条线段转动的最大弧度数。（默认为 Math.PI/8）
     * @return GeoJSON FeatureCollection of linestrings
     */
    public static FeatureCollection<LineString> randomLineString(int count, BoundingBox bbox, Integer numVertices, Double maxLength, Double maxRotation) {
        if (count <= 0) {
            count = 1;
        }
        double[] b = bbox != null ? bbox.bbox() : null;
        // 至少也要求2个点
        if (numVertices == null || numVertices < 2) {
            numVertices = 10;
        }
        if (maxLength == null) {
            maxLength = 0.0001D;
        }
        if (maxRotation == null) {
            maxRotation = Math.PI / 8;
        }

        List<Feature<LineString>> features = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Point startingPoint = randomPositionUnchecked(b);

            List<Point> vertices = new ArrayList<>(numVertices);
            vertices.add(startingPoint);

            for (int j = 0; j < numVertices - 1; j++) {
                Point prev = vertices.get(j);
                double priorAngle;
                if (j == 0) {
                    priorAngle = Math.random() * 2 * Math.PI;
                } else {
                    Point prev2 = vertices.get(j - 1);
                    priorAngle = Math.tan((prev.getY() - prev2.getY()) / (prev.getX() - prev2.getX()));
                }

                double angle = priorAngle + (Math.random() - 0.5) * maxRotation * 2;
                double distance = Math.random() * maxLength;

                vertices.add(Point.fromLngLat(prev.getX() + distance * Math.cos(angle), prev.getY() + distance * Math.sin(angle)));
            }
            features.add(Feature.fromGeometry(LineString.fromLngLats(vertices)));
        }

        return FeatureCollection.fromFeatures(features);
    }

    /**
     * 返回随机的多边形
     *
     * @param bbox            指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices     每个 Polygon 将包含的坐标数。（默认 10）
     * @param maxRadialLength 点到达多边形中心的最大的纬度或经度数。（默认 10）
     * @return GeoJSON FeatureCollection of polygon
     */
    public static Feature<Polygon> randomPolygon(BoundingBox bbox, Integer numVertices, Double maxRadialLength) {
        return randomPolygon(1, bbox, numVertices, maxRadialLength).get(0);
    }

    /**
     * 返回一组随机的多边形
     *
     * @param count           指定生成随机线段的数量
     * @param bbox            指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices     每个 Polygon 将包含的坐标数。（默认 10）
     * @param maxRadialLength 点到达多边形中心的最大的纬度或经度数。（默认 10）
     * @return GeoJSON FeatureCollection of polygons
     */
    public static FeatureCollection<Polygon> randomPolygon(int count, BoundingBox bbox, Integer numVertices, Double maxRadialLength) {
        if (count <= 0) {
            count = 1;
        }
        double[] b = bbox != null ? bbox.bbox() : new double[]{-180, -90, 180, 90};
        if (numVertices == null) {
            numVertices = 10;
        }
        if (maxRadialLength == null) {
            maxRadialLength = 10D;
        }

        double bboxWidth = Math.abs(b[0] - b[2]);
        double bboxHeight = Math.abs(b[1] - b[3]);

        double maxRadius = Math.min(bboxWidth / 2, bboxHeight / 2);

        if (maxRadialLength > maxRadius) {
            throw new JTurfException("maxRadialLength is greater than the radius of the bbox");
        }

        // Create a padded bbox to avoid the polygons to be too close to the border
        double[] paddedBbox = new double[]{b[0] + maxRadialLength, b[1] + maxRadialLength, b[2] - maxRadialLength, b[3] - maxRadialLength};

        List<Feature<Polygon>> features = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            List<Point> vertices = new ArrayList<>(numVertices + 1);
            double[] circleOffsets = DoubleStream.generate(Math::random).limit(10).toArray();
            // 将每个元素替换为它与前一个元素的和
            for (int j = 1; j < circleOffsets.length; j++) {
                circleOffsets[j] += circleOffsets[j - 1];
            }
            // scaleOffsets
            double last = circleOffsets[circleOffsets.length - 1];
            for (double cur : circleOffsets) {
                cur = (cur * 2 * Math.PI) / last;
                double radialScaler = Math.random();

                vertices.add(Point.fromLngLat(radialScaler * maxRadialLength * Math.sin(cur), radialScaler * maxRadialLength * Math.cos(cur)));
            }
            vertices.add(vertices.get(0)); // close the ring

            // center the polygon around something
            Collections.reverse(vertices); // Make counter-clockwise to adhere to right hand rule.

            Polygon polygon = Polygon.fromOuterInner(vertices.stream().map(cur -> {
                Point hub = randomPositionUnchecked(paddedBbox);
                return Point.fromLngLat(cur.getX() + hub.getX(), cur.getY() + hub.getY());
            }).collect(Collectors.toList()));

            features.add(Feature.fromGeometry(polygon));
        }

        return FeatureCollection.fromFeatures(features);
    }

    private static Point randomPositionUnchecked(double[] bbox) {
        if (bbox == null) {
            return Point.fromLngLat(lon(), lat());
        } else {
            return coordInBBox(bbox);
        }
    }

    private static Point coordInBBox(double[] bbox) {
        return Point.fromLngLat(Math.random() * (bbox[2] - bbox[0]) + bbox[0], Math.random() * (bbox[3] - bbox[1]) + bbox[1]);
    }

    private static double rnd() {
        return Math.random() - 0.5;
    }

    private static double lon() {
        return rnd() * 360;
    }

    private static double lat() {
        return rnd() * 180;
    }

}
