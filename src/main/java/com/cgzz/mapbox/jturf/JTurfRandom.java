package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Line;
import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.shape.Polygon;
import com.cgzz.mapbox.jturf.util.JTurfHelper;

import java.util.ArrayList;
import java.util.List;

public final class JTurfRandom {

    private JTurfRandom() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 随机点<br><br>
     *
     * @return 返回一个随机点
     */
    public static Point randomPosition() {
        return randomPositionUnchecked(null);
    }

    /**
     * 随机点<br><br>
     *
     * @param bbox 返回指定范围内的点
     * @return 返回一个随机点
     */
    public static Point randomPosition(double[] bbox) {
        return randomPositionUnchecked(bbox);
    }

    /**
     * 随机点<br><br>
     * <p>
     * 返回指定数量的随机点
     *
     * @param num 将生成多少个点，最少为1个
     * @return 返回随机点集合
     */
    public static List<Point> randomPoint(int num) {
        return randomPoint(num, null);
    }

    /**
     * 随机点<br><br>
     * <p>
     * 返回指定数量的随机点
     *
     * @param num  将生成多少个点，最少为1个
     * @param bbox 指定随机点的生成边界
     * @return 返回随机点集合
     */
    public static List<Point> randomPoint(int num, double[] bbox) {
        if (bbox != null) {
            JTurfHelper.validateBBox(bbox);
        }
        if (num <= 0) {
            num = 1;
        }
        List<Point> pointList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            pointList.add(randomPositionUnchecked(bbox));
        }
        return pointList;
    }

    private static Point randomPositionUnchecked(double[] bbox) {
        if (bbox != null) {
            return coordInBBox(bbox);
        }

        return Point.fromLngLat(lon(), lat());
    }

    /**
     * 随机线段<br><br>
     * <p>
     * 返回一个随机的Line
     *
     * @return 返回指定数量的线段
     */
    public static Line randomLine() {
        return randomLine(1, null).get(0);
    }

    /**
     * 随机线段<br><br>
     * <p>
     * 返回一个随机的Line
     *
     * @param bbox 在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @return 返回指定数量的线段
     */
    public static Line randomLine(double[] bbox) {
        return randomLine(1, bbox).get(0);
    }

    /**
     * 随机线段<br><br>
     * <p>
     * 返回一组随机的Line
     *
     * @param count 将生成多少个线
     * @return 返回指定数量的线段
     */
    public static List<Line> randomLine(int count) {
        return randomLine(count, null);
    }

    /**
     * 随机线段<br><br>
     * <p>
     * 返回一组随机的Line
     *
     * @param count 将生成多少个线
     * @param bbox  在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @return 返回指定数量的线段
     */
    public static List<Line> randomLine(int count, double[] bbox) {
        return randomLine(count, bbox, 10);
    }

    /**
     * 随机线段<br><br>
     * <p>
     * 返回一组随机的Line
     *
     * @param count       将生成多少个线
     * @param bbox        在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @param numVertices 每个线段包含多少个坐标，默认值：10
     * @return 返回指定数量的线段
     */
    public static List<Line> randomLine(int count, double[] bbox, int numVertices) {
        return randomLine(count, bbox, numVertices, 0.0001D, Math.PI / 8);
    }

    /**
     * 随机线段<br><br>
     * <p>
     * 返回一组随机的Line
     *
     * @param count       将生成多少个线
     * @param bbox        在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @param numVertices 每个线段包含多少个坐标，默认值：10
     * @param maxLength   点与其前一个点相比的最大经纬度数，默认值：0.0001
     * @param maxRotation 线段可以从前一段转弯的最大弧度数，默认值：Math.PI/8
     * @return 返回指定数量的线段
     */
    public static List<Line> randomLine(int count, double[] bbox, int numVertices, double maxLength, double maxRotation) {
        if (count <= 0) {
            count = 1;
        }
        if (numVertices < 2) {
            numVertices = 10;
        }
        if (maxLength <= 0) {
            maxLength = 0.0001;
        }
        if (maxRotation <= 0) {
            maxRotation = Math.PI / 8;
        }

        List<Line> lineList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Point startingPoint = randomPosition(bbox);

            List<Point> vertices = new ArrayList<>(numVertices);
            vertices.add(startingPoint);

            for (int j = 0; j < numVertices - 1; j++) {
                Point p = vertices.get(j);

                double priorAngle = 0D;
                if (j == 0) {
                    priorAngle = Math.random() * 2 * Math.PI;
                } else {
                    Point n = vertices.get(j - 1);

                    priorAngle = Math.tan((p.getY() - n.getY()) / (p.getX() - n.getX()));
                }

                double angle = priorAngle + (Math.random() - 0.5) * maxRotation * 2;
                double distance = Math.random() * maxLength;

                vertices.add(Point.fromLngLat(p.getX() + distance * Math.cos(angle), p.getY() + distance * Math.sin(angle)));
            }

            lineList.add(Line.fromLngLats(vertices));
        }

        return lineList;
    }

    /**
     * 随机一个多边形
     *
     * @return 返回一个多边形
     */
    public static Polygon randomPolygon() {
        return randomPolygon(1, null).get(0);
    }

    /**
     * 随机一个多边形
     *
     * @param bbox 在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @return 返回一个多边形
     */
    public static Polygon randomPolygon(double[] bbox) {
        return randomPolygon(1, bbox).get(0);
    }

    /**
     * 随机多边形
     *
     * @param count 将生成多少个多边形
     * @return 返回指定数量的多边形
     */
    public static List<Polygon> randomPolygon(int count) {
        return randomPolygon(count, null);
    }

    /**
     * 随机多边形
     *
     * @param count 将生成多少个多边形
     * @param bbox  在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @return 返回指定数量的多边形
     */
    public static List<Polygon> randomPolygon(int count, double[] bbox) {
        return randomPolygon(count, bbox, 10);
    }

    /**
     * 随机多边形
     *
     * @param count       将生成多少个多边形
     * @param bbox        在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @param numVertices 每个多边形包含多少个坐标，默认值：10
     * @return 返回指定数量的多边形
     */
    public static List<Polygon> randomPolygon(int count, double[] bbox, int numVertices) {
        return randomPolygon(count, bbox, numVertices, 0.06);
    }

    /**
     * 随机多边形
     *
     * @param count           将生成多少个多边形
     * @param bbox            在其中放置线段的边界框，默认值：[-180,-90,180,90]
     * @param numVertices     每个多边形包含多少个坐标，默认值：10
     * @param maxRadialLength 点可以从多边形中心到达的最大纬度或经度数，默认值：0.06
     * @return 返回指定数量的多边形
     */
    public static List<Polygon> randomPolygon(int count, double[] bbox, int numVertices, double maxRadialLength) {
        if (count <= 0) {
            count = 1;
        }
        if (numVertices <= 0) {
            numVertices = 10;
        }
        if (maxRadialLength <= 0) {
            maxRadialLength = 0.06;
        }

        List<Polygon> polygonList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            List<Point> vertices = new ArrayList<>(numVertices);

            double[] circleOffsets = new double[numVertices + 1];
            // Sum Offsets
            for (int j = 0; j <= numVertices; j++) {
                circleOffsets[j] = Math.random();
                double cur = circleOffsets[j];
                circleOffsets[j] = j > 0 ? cur + circleOffsets[j - 1] : cur;
            }
            // scaleOffsets
            for (double cur : circleOffsets) {
                cur = (cur * 2 * Math.PI) / circleOffsets[circleOffsets.length - 1];
                double radialScaler = Math.random();

                vertices.add(Point.fromLngLat(radialScaler * maxRadialLength * Math.sin(cur), radialScaler * maxRadialLength * Math.cos(cur)));
            }
            vertices.add(Point.fromLngLat(vertices.get(0))); // close the ring

            // center the polygon around something
            Point center = randomPosition(bbox);
            for (Point p : vertices) {
                p.setLongitude(p.getX() + center.getX());
                p.setLatitude(p.getY() + center.getY());
            }

            polygonList.add(Polygon.fromLngLats(vertices));
        }

        return polygonList;
    }

    private static Point coordInBBox(double[] bbox) {
        return Point.fromLngLat(
                Math.random() * (bbox[2] - bbox[0]) + bbox[0],
                Math.random() * (bbox[3] - bbox[1]) + bbox[1]
        );
    }

    private static double lon() {
        return rnd() * 360;
    }

    private static double lat() {
        return rnd() * 180;
    }

    private static double rnd() {
        return Math.random() - 0.5;
    }

}
