package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.IntersectsResult;
import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.util.JTurfHelper;
import com.cgzz.mapbox.jturf.util.ObjectHolder;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import org.omg.CORBA.DoubleHolder;
import rx.Observable;

import java.util.*;
import java.util.function.Consumer;

public final class JTurfMisc {

    private JTurfMisc() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 返回自身的相交点
     *
     * @param geometry 图形，支持 POLYGON、LINE、MULTI_LINE、MULTI_POLYGON
     * @return 返回自相交点集合
     */
    public static List<Point> kinks(Geometry geometry) {
        switch (geometry.type()) {
            case POLYGON:
                return kinks(((Polygon) geometry).coordinates());
            case LINE:
                return kinks(((Line) geometry).coordinates());
            case MULTI_LINE:
                return kinksMulti(((MultiLine) geometry).coordinates());
            case MULTI_POLYGON:
                return kinksMulti(((MultiPolygon) geometry).coordinates());
        }
        return null;
    }

    private static List<Point> kinksMulti(List<List<Point>> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        List<Point> pointList = new ArrayList<>();
        for (List<Point> c : coordinates) {
            if (c == null || c.isEmpty()) {
                continue;
            }

            kinks(c, pointList);
        }

        return pointList;
    }

    private static List<Point> kinks(List<Point> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        List<Point> pointList = new ArrayList<>();

        kinks(coordinates, pointList);

        return pointList;
    }

    private static void kinks(List<Point> coordinates, List<Point> pointList) {
        int len = coordinates.size();
        for (int i = 0; i < len - 1; i++) {
            for (int k = i; k < len - 1; k++) {
                // segments are adjacent and always share a vertex, not a kink
                if (Math.abs(i - k) == 1) {
                    continue;
                }
                // first and last segment in a closed lineString or ring always share a vertex, not a kink
                if (i == 0 && k == len - 2 && coordinates.get(i).getLongitude() == coordinates.get(len - 1).getLongitude() && coordinates.get(i).getLatitude() == coordinates.get(len - 1).getLatitude()) {
                    continue;
                }

                IntersectsResult result = lineIntersects(coordinates.get(i), coordinates.get(i + 1), coordinates.get(k), coordinates.get(k + 1));
                if (result != null) {
                    pointList.add(Point.fromLngLat(result.getX(), result.getY()));
                }
            }
        }
    }

    private static IntersectsResult lineIntersects(Point line1Start, Point line1End, Point line2Start, Point line2End) {
        return lineIntersects(line1Start.getLongitude(), line1Start.getLatitude(),
                line1End.getLongitude(), line1End.getLatitude(),
                line2Start.getLongitude(), line2Start.getLatitude(),
                line2End.getLongitude(), line2End.getLatitude());
    }

    private static IntersectsResult lineIntersects(double line1StartX, double line1StartY,
                                                   double line1EndX, double line1EndY,
                                                   double line2StartX, double line2StartY,
                                                   double line2EndX, double line2EndY) {
        double denominator, a, b, numerator1, numerator2;
        IntersectsResult result = new IntersectsResult();
        denominator = (line2EndY - line2StartY) * (line1EndX - line1StartX) - (line2EndX - line2StartX) * (line1EndY - line1StartY);
        if (denominator == 0) {
            if (result.getX() != null && result.getY() != null) {
                return result;
            } else {
                return null;
            }
        }

        a = line1StartY - line2StartY;
        b = line1StartX - line2StartX;
        numerator1 = (line2EndX - line2StartX) * a - (line2EndY - line2StartY) * b;
        numerator2 = (line1EndX - line1StartX) * a - (line1EndY - line1StartY) * b;
        a = numerator1 / denominator;
        b = numerator2 / denominator;

        result.setX(line1StartX + a * (line1EndX - line1StartX));
        result.setY(line1StartY + a * (line1EndY - line1StartY));

        if (a >= 0 && a <= 1) {
            result.setOnLine1(true);
        }
        if (b >= 0 && b <= 1) {
            result.setOnLine2(true);
        }

        if (result.isOnLine1() && result.isOnLine2()) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * 多边型顶点连线，从一个(多)Line或(多)Polygon创建一个2-vertex线段的GeometryCollection
     *
     * @param geometry 支持 Line|MultiLine|MultiPolygon|Polygon
     * @return List<Line>
     */
    public static List<Line> lineSegment(Geometry geometry) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }

        List<Line> results = new ArrayList<>();
        JTurfMeta.flattenEach(geometry, (g, multiIndex) -> {
            lineSegmentFeature(g, results);
            return true;
        });

        return results;
    }

    /**
     * 从Line中创建线段
     *
     * @param geometry 支持 Line|Polygon
     * @param results  待返回的线段集合
     */
    private static void lineSegmentFeature(Geometry geometry, List<Line> results) {
        GeometryType type = geometry.type();
        List<Point> coords = null;
        switch (type) {
            case POLYGON:
                coords = ((Polygon) geometry).coordinates();
                break;
            case LINE:
                coords = ((Line) geometry).coordinates();
                break;
        }

        if (coords == null || coords.isEmpty()) {
            return;
        }

        // 组合成N组线段
        List<Line> segments = createSegments(coords);
        if (!segments.isEmpty()) {
            results.addAll(segments);
        }
    }

    /**
     * 将传入的coords中的点两两组合成一组线段
     *
     * @param coords 传入的坐标组
     * @return List<Line>
     */
    private static List<Line> createSegments(List<Point> coords) {
        List<Line> segments = new ArrayList<>();

        for (int i = 1, size = coords.size(); i < size; i++) {
            Point previousCoords = coords.get(i - 1);
            Point currentCoords = coords.get(i);

            segments.add(Line.fromLngLats(previousCoords, currentCoords));
        }

        return segments;
    }

    /**
     * 计算两个图形相交点
     *
     * @param geometry1 图形1，支持 Line、Polygon
     * @param geometry2 图形2，支持 Line、Polygon
     * @return 返回相交点集合
     */
    public static List<Point> lineIntersect(Geometry geometry1, Geometry geometry2) {
        if (geometry1 == null) {
            throw new JTurfException("geometry1 is required");
        }
        if (geometry2 == null) {
            throw new JTurfException("geometry2 is required");
        }

        GeometryType t1 = geometry1.type(), t2 = geometry2.type();
        if (t1 != GeometryType.LINE && t1 != GeometryType.POLYGON) {
            throw new JTurfException("geometry1 type must Line or Polygon");
        }
        if (t2 != GeometryType.LINE && t2 != GeometryType.POLYGON) {
            throw new JTurfException("geometry2 type must Line or Polygon");
        }

        // 如果两个都是一条线段的话，不需要展开处理
        if (t1 == GeometryType.LINE
                && t2 == GeometryType.LINE
                && geometry1.coordsSize() == 2
                && geometry2.coordsSize() == 2) {
            Point intersect = intersects(Line.line(geometry1), Line.line(geometry2));

            return intersect != null ? Collections.singletonList(intersect) : null;
        }

        // 处理复杂的几何图形
        List<Point> results = new ArrayList<>();
        Set<String> unique = new HashSet<>();

        RTree<Line, Rectangle> rtree = initRTree(geometry2);
        for (Line segment : JTurfMisc.lineSegment(geometry1)) {
            Observable<Entry<Line, Rectangle>> entries = rtree.search(createRectangle(segment));

            for (Entry<Line, Rectangle> p : entries.toBlocking().toIterable()) {
                Point intersect = intersects(segment, p.value());
                if (intersect != null) {
                    // 防止重复点
                    if (unique.add(intersect.getX() + "," + intersect.getY())) {
                        results.add(intersect);
                    }
                }
            }
        }

        return results;
    }

    /**
     * 创建 RTree 需要使用到的 Rectangle 对象
     *
     * @param line 线段
     * @return Rectangle
     */
    private static Rectangle createRectangle(Line line) {
        BoundingBox bbox = JTurfMeasurement.bbox(line);

        return Geometries.rectangle(bbox.west(), bbox.south(), bbox.east(), bbox.north());
    }

    /**
     * 构建一棵R树
     *
     * @param geometry 图形
     * @return RTree<Line, Rectangle>
     */
    private static RTree<Line, Rectangle> initRTree(Geometry geometry) {
        // 创建R树时，可以指定最小、最大孩子结点数
        RTree<Line, Rectangle> rtree = RTree.minChildren(2).maxChildren(9).create();
        for (Line line : JTurfMisc.lineSegment(geometry)) {
            rtree = rtree.add(line, createRectangle(line));
        }

        return rtree;
    }

    /**
     * 查找与线串相交的点，每个点有两个坐标
     *
     * @param line1 必须是只有两个点的线段
     * @param line2 必须是只有两个点的线段
     * @return 返回相交点
     */
    private static Point intersects(Line line1, Line line2) {
        List<Point> coords1 = line1.coordinates(), coords2 = line2.coordinates();

        if (coords1.size() != 2) {
            throw new JTurfException("<intersects> line1 must only contain 2 coordinates");
        }
        if (coords2.size() != 2) {
            throw new JTurfException("<intersects> line2 must only contain 2 coordinates");
        }

        double x1 = coords1.get(0).getX(), y1 = coords1.get(0).getY();
        double x2 = coords1.get(1).getX(), y2 = coords1.get(1).getY();
        double x3 = coords2.get(0).getX(), y3 = coords2.get(0).getY();
        double x4 = coords2.get(1).getX(), y4 = coords2.get(1).getY();

        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        double numeA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        double numeB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

        if (denom == 0) {
            if (numeA == 0 && numeB == 0) {
                return null;
            }
            return null;
        }

        double uA = numeA / denom;
        double uB = numeB / denom;

        if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
            double x = x1 + uA * (x2 - x1);
            double y = y1 + uA * (y2 - y1);
            return Point.fromLngLat(x, y);
        }
        return null;
    }

    /**
     * 计算两个图形之间重叠的线，默认容差距离为0公里
     *
     * @param geometry1 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param geometry2 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @return 返回两个图形之间重叠的线段集合
     */
    public static List<Line> lineOverlap(Geometry geometry1, Geometry geometry2) {
        return lineOverlap(geometry1, geometry2, 0);
    }

    /**
     * 计算两个图形之间重叠的线
     *
     * @param geometry1 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param geometry2 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param tolerance 匹配重叠线段的容差距离（以公里为单位）
     * @return 返回两个图形之间重叠的线段集合
     */
    public static List<Line> lineOverlap(Geometry geometry1, Geometry geometry2, Integer tolerance) {
        if (geometry1 == null) {
            throw new JTurfException("geometry1 is required");
        }
        if (geometry2 == null) {
            throw new JTurfException("geometry2 is required");
        }

        GeometryType t1 = geometry1.type(), t2 = geometry2.type();
        if (t1 != GeometryType.LINE && t1 != GeometryType.POLYGON && t1 != GeometryType.MULTI_LINE && t1 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry1 type must Line or Polygon or MultiLine or MultiPolygon");
        }
        if (t2 != GeometryType.LINE && t2 != GeometryType.POLYGON && t2 != GeometryType.MULTI_LINE && t2 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry2 type must Line or Polygon or MultiLine or MultiPolygon");
        }

        int disparity = tolerance == null ? 0 : tolerance; // 差距
        Units units = Units.KILOMETERS; // 计算距离的单位为公里

        // 构建R树
        RTree<Line, Rectangle> rtree = initRTree(geometry1);

        List<Line> result = new ArrayList<>();
        List<Point> overlapSegment = new ArrayList<>(); // 线段点集合

        JTurfMeta.segmentEach(geometry2, (segment, geometryIndex, multiIndex, segmentIndex) -> {
            boolean doesOverlaps = false;

            Observable<Entry<Line, Rectangle>> entries = rtree.search(createRectangle(segment));
            // 迭代落在相同边界内的每个段
            for (Entry<Line, Rectangle> p : entries.toBlocking().toIterable()) {
                if (doesOverlaps) {
                    break;
                }

                Line match = p.value();

                List<Point> coordsSegment = new ArrayList<>(segment.coordinates());
                List<Point> coordsMatch = new ArrayList<>(match.coordinates());

                Collections.sort(coordsSegment);
                Collections.sort(coordsMatch);

                // 线段重叠
                if (JTurfHelper.deepEquals(coordsSegment, coordsMatch)) {
                    doesOverlaps = true;

                    // 重叠已存在 - 仅附加线段的最后一个坐标
                    if (overlapSegment.size() > 0) {
                        concatSegment(overlapSegment, segment);
                    } else {
                        overlapSegment.addAll(segment.coordinates());
                    }
                } else if (disparity == 0
                        ? (JTurfBooleans.booleanPointOnLine(coordsSegment.get(0), match)
                        && JTurfBooleans.booleanPointOnLine(coordsSegment.get(1), match))
                        : (JTurfMeasurement.distance(coordsMatch.get(0), nearestPointOnLine(match, coordsSegment.get(0), units), units) <= tolerance
                        && JTurfMeasurement.distance(coordsMatch.get(1), nearestPointOnLine(match, coordsSegment.get(1), units), units) <= tolerance)) {
                    doesOverlaps = true;
                    if (overlapSegment.size() > 0) {
                        concatSegment(overlapSegment, segment);
                    } else {
                        overlapSegment.addAll(segment.coordinates());
                    }
                } else if (disparity == 0
                        ? (JTurfBooleans.booleanPointOnLine(coordsMatch.get(0), segment)
                        && JTurfBooleans.booleanPointOnLine(coordsMatch.get(1), segment))
                        : (JTurfMeasurement.distance(coordsMatch.get(0), nearestPointOnLine(segment, coordsMatch.get(0), units), units) <= tolerance
                        && JTurfMeasurement.distance(coordsMatch.get(1), nearestPointOnLine(segment, coordsMatch.get(1), units), units) <= tolerance)) {
                    // 不要定义（doesOverlap = true），因为同一段中可能会出现更多匹配项
                    if (overlapSegment.size() > 0) {
                        concatSegment(overlapSegment, match);
                    } else {
                        overlapSegment.addAll(match.coordinates());
                    }
                }
            }

            // Segment 不重叠 - 向结果添加重叠并重置
            if (!doesOverlaps && overlapSegment.size() > 0) {
                result.add(Line.fromLngLatsShallowCopy(overlapSegment));
                overlapSegment.clear();
            }

            return true;
        });

        // 添加最后一个段（如果存在）
        if (overlapSegment.size() > 0) {
            result.add(Line.fromLngLatsShallowCopy(overlapSegment));
        }

        return result;
    }

    private static void concatSegment(List<Point> lineCoords, Line segment) {
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

    /**
     * 计算点到多线段的最短间距的点，默认距离单位：公里
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @return 返回线段上最短距离的点
     */
    public static Point nearestPointOnLine(Geometry lines, Point pt) {
        return nearestPointOnLine(lines, pt, Units.KILOMETERS);
    }

    /**
     * 计算点到多线段的最短间距的点
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @param units 距离单位
     * @return 返回线段上最短距离的点
     */
    public static Point nearestPointOnLine(Geometry lines, Point pt, Units units) {
        if (lines == null) {
            throw new JTurfException("lines is required");
        }
        if (pt == null) {
            throw new JTurfException("pt is required");
        }
        if (lines.type() != GeometryType.LINE && lines.type() != GeometryType.MULTI_LINE) {
            throw new JTurfException("geometry2 type must Line or MultiLine");
        }
        if (units != Units.DEGREES
                && units != Units.RADIANS
                && units != Units.MILES
                && units != Units.KILOMETERS
                && units != Units.KILOMETRES) {
            throw new JTurfException("units can be degrees, radians, miles, or kilometers");
        }

        ObjectHolder<Point> closePt = new ObjectHolder<>();
        DoubleHolder closeDist = new DoubleHolder(Double.POSITIVE_INFINITY);

        JTurfMeta.flattenEach(lines, ((line, multiIndex) -> {
            List<Point> coords = ((Line) line).coordinates();

            for (int i = 0, size = coords.size(); i < size - 1; i++) {
                Point start = coords.get(i);
                double startDist = JTurfMeasurement.distance(pt, start, units);

                Point stop = coords.get(i + 1);
                double stopDist = JTurfMeasurement.distance(pt, stop, units);

                // double sectionLength = TaleMeasurement.distance(start, stop, units);
                double heightDistance = Math.max(startDist, stopDist);
                // 计算方位角
                double direction = JTurfMeasurement.bearing(start, stop);

                Point perpendicularPt1 = JTurfMeasurement.destination(pt, heightDistance, direction + 90, units);
                Point perpendicularPt2 = JTurfMeasurement.destination(pt, heightDistance, direction - 90, units);

                // 计算两个线的相交点
                List<Point> intersect = lineIntersect(Line.fromLngLats(perpendicularPt1, perpendicularPt2), Line.fromLngLats(start, stop));

                Point intersectPt = null;
                double intersectDist = 0;
                if (intersect != null && intersect.size() > 0) {
                    intersectPt = intersect.get(0);
                    intersectDist = JTurfMeasurement.distance(pt, intersectPt, units);
                }

                if (startDist < closeDist.value) {
                    closePt.value = start;
                    closeDist.value = startDist;
                }
                if (stopDist < closeDist.value) {
                    closePt.value = stop;
                    closeDist.value = stopDist;
                }
                if (intersectPt != null && intersectDist < closeDist.value) {
                    closePt.value = intersectPt;
                    closeDist.value = intersectDist;
                }
            }

            return true;
        }));

        return closePt.value;
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     * <p>
     * 默认步长为64，距离单位 KILOMETERS
     *
     * @param coord    中心点
     * @param radius   圆的半径
     * @param bearing1 方位角1 圆弧第一半径的角度
     * @param bearing2 方位角2 圆弧第二半径的角度
     * @return 返回弧度的线条
     */
    public static Line lineArc(Point coord, double radius, double bearing1, double bearing2) {
        return lineArc(coord, radius, bearing1, bearing2, null, null);
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     *
     * @param coord    中心点
     * @param radius   圆的半径
     * @param bearing1 方位角1 圆弧第一半径的角度
     * @param bearing2 方位角2 圆弧第二半径的角度
     * @param steps    步长，不传入则默认为64
     * @param units    单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 返回弧度的线条
     */
    public static Line lineArc(Point coord, double radius, double bearing1, double bearing2, Integer steps, Units units) {
        if (steps == null) {
            steps = 64;
        }
        if (units == null) {
            units = Units.KILOMETERS;
        }

        double angle1 = JTurfHelper.convertAngleTo360(bearing1);
        double angle2 = JTurfHelper.convertAngleTo360(bearing2);

        // handle angle parameters
        if (angle1 == angle2) {
            Polygon circle = JTurfTransformation.circle(coord, radius, steps, units);
            return Line.fromLngLats(circle.coordinates());
        }

        double arcStartDegree = angle1;
        double arcEndDegree = angle1 < angle2 ? angle2 : angle2 + 360;

        double alfa = arcStartDegree;
        List<Point> coordinates = new ArrayList<>();
        int i = 0;
        while (alfa < arcEndDegree) {
            coordinates.add(JTurfMeasurement.destination(coord, radius, alfa, units));
            i++;
            alfa = arcStartDegree + (i * 360D) / steps;
        }
        if (alfa > arcEndDegree) {
            coordinates.add(JTurfMeasurement.destination(coord, radius, arcEndDegree, units));
        }
        return Line.fromLngLats(coordinates);
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param line      线段
     * @param startDist 沿线到起点的起始距离（默认 KILOMETERS）
     * @param stopDist  沿线到终点的停靠点距离（默认 KILOMETERS）
     * @return 切片线段
     */
    public static Line lineSliceAlong(Line line, double startDist, double stopDist) {
        return lineSliceAlong(line, startDist, stopDist, null);
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param line      线段
     * @param startDist 沿线到起点的起始距离
     * @param stopDist  沿线到终点的停靠点距离
     * @param units     距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 切片线段
     */
    public static Line lineSliceAlong(Line line, double startDist, double stopDist, Units units) {
        if (units == null) {
            units = Units.KILOMETERS;
        }

        List<Point> slice = new ArrayList<>();
        List<Point> coords = line.coordinates();
        int origCoordsLength = coords.size();
        double travelled = 0;
        double overshot, direction;
        Point interpolated;
        for (int i = 0; i < origCoordsLength; i++) {
            if (startDist >= travelled && i == origCoordsLength - 1) {
                break;
            } else if (travelled > startDist && slice.size() == 0) {
                overshot = startDist - travelled;
                if (overshot == 0) {
                    slice.add(coords.get(i));
                    return Line.fromLngLats(slice);
                }
                direction = JTurfMeasurement.bearing(coords.get(i), coords.get(i - 1)) - 180;
                interpolated = JTurfMeasurement.destination(coords.get(i), overshot, direction, units);
                slice.add(interpolated);
            }

            if (travelled >= stopDist) {
                overshot = stopDist - travelled;
                if (overshot == 0) {
                    slice.add(coords.get(i));
                    return Line.fromLngLats(slice);
                }
                direction = JTurfMeasurement.bearing(coords.get(i), coords.get(i - 1)) - 180;
                interpolated = JTurfMeasurement.destination(coords.get(i), overshot, direction, units);
                slice.add(interpolated);
                return Line.fromLngLats(slice);
            }

            if (travelled >= startDist) {
                slice.add(coords.get(i));
            }

            if (i == origCoordsLength - 1) {
                return Line.fromLngLats(slice);
            }

            travelled += JTurfMeasurement.distance(coords.get(i), coords.get(i + 1), units);
        }

        if (travelled < startDist) {
            throw new JTurfException("Start position is beyond line");
        }

        Point last = coords.get(origCoordsLength - 1);
        return Line.fromLngLats(last, last);
    }

    /**
     * 分割多线段<br>
     * <p>
     * 将一个Line分割成指定长度的块。如果Line比分隔段长度短，则返回原始Line
     *
     * @param geometry      线段，支持 Line、MultiLine
     * @param segmentLength 每个段的长度，默认距离单位 KILOMETERS
     * @return 线段集合
     */
    public static List<Line> lineChunk(Geometry geometry, double segmentLength) {
        return lineChunk(geometry, segmentLength, null, false);
    }

    /**
     * 分割多线段<br>
     * <p>
     * 将一个Line分割成指定长度的块。如果Line比分隔段长度短，则返回原始Line
     *
     * @param geometry      线段，支持 Line、MultiLine
     * @param segmentLength 每个段的长度
     * @param units         距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 线段集合
     */
    public static List<Line> lineChunk(Geometry geometry, double segmentLength, Units units) {
        return lineChunk(geometry, segmentLength, units, false);
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
    public static List<Line> lineChunk(Geometry geometry, double segmentLength, Units units, boolean reverse) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }
        if (geometry.type() != GeometryType.LINE && geometry.type() != GeometryType.MULTI_LINE) {
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

        List<Line> results = new ArrayList<>();

        // Flatten each feature to simple Line
        JTurfMeta.flattenEach(geometry, (g, multiIndex) -> {
            Line line = Line.line(g);

            // reverses coordinates to start the first chunked segment at the end
            List<Point> pointList;
            if (reverse) {
                List<Point> temp = new ArrayList<>(line.coordinates());
                Collections.reverse(temp);
                pointList = temp;
            } else {
                pointList = line.coordinates();
            }

            sliceLineSegments(Line.fromLngLats(pointList), segmentLength, u, results::add);

            return true;
        });

        return results;
    }

    /**
     * 对线段进行切片
     *
     * @param line          要分片的线段
     * @param segmentLength 每个段的长度
     * @param units         距离单位
     * @param callback      回调函数
     */
    private static void sliceLineSegments(Line line, double segmentLength, Units units, Consumer<Line> callback) {
        double lineLength = JTurfMeasurement.length(line, units);

        // If the line is shorter than the segment length then the orginal line is returned.
        if (lineLength <= segmentLength) {
            callback.accept(line);
            return;
        }

        double numberOfSegments = lineLength / segmentLength;

        // If numberOfSegments is integer, no need to plus 1
        if (numberOfSegments != Math.floor(numberOfSegments)) {
            numberOfSegments = Math.floor(numberOfSegments) + 1;
        }

        for (double i = 0; i < numberOfSegments; i++) {
            Line outline = lineSliceAlong(line, segmentLength * i, segmentLength * (i + 1), units);
            callback.accept(outline);
        }
    }

}
