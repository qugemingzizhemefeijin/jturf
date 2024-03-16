package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.util.JTurfHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JTurfCoordinateMutation {

    private JTurfCoordinateMutation() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 获取输入feature并将它们的所有坐标从[x, y]翻转为[y, x]，默认会生成新的 geometry 图形组件
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T flip(T geometry) {
        return flip(geometry, false);
    }

    /**
     * 获取输入feature并将它们的所有坐标从[x, y]翻转为[y, x]
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T flip(T geometry, boolean mutate) {
        T newGeometry;

        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        JTurfMeta.coordEach(geometry, (geo, p, index, multiIndex, geomIndex) -> {
            double latitude = p.getLatitude(), longitude = p.getLongitude();

            // 翻转
            p.setLongitude(latitude);
            p.setLatitude(longitude);

            return true;
        });

        return newGeometry;
    }

    /**
     * 坐标小数处理，默认情况保留6位小数
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry) {
        return truncate(geometry, 6, false);
    }

    /**
     * 坐标小数处理
     *
     * @param geometry  图形组件
     * @param precision 保留的小数位数，如果传入空则保留6位小数
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry, Integer precision) {
        return truncate(geometry, 6, false);
    }

    /**
     * 坐标小数处理
     *
     * @param geometry  图形组件
     * @param precision 保留的小数位数，如果传入空则保留6位小数
     * @param mutate    是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry, Integer precision, boolean mutate) {
        if (precision == null || precision < 0) {
            precision = 6;
        }

        T newGeometry;
        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        double factor = Math.pow(10, precision);

        JTurfMeta.coordEach(geometry, (geo, p, index, multiIndex, geomIndex) -> {
            double latitude = p.getLatitude(), longitude = p.getLongitude();

            p.setLongitude(Math.round(longitude * factor) / factor);
            p.setLatitude(Math.round(latitude * factor) / factor);

            return true;
        });

        return newGeometry;
    }

    /**
     * 清除重复坐标点，默认情况不影响原图形
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T cleanCoords(T geometry) {
        return cleanCoords(geometry, false);
    }

    /**
     * 清除重复坐标点
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Geometry> T cleanCoords(T geometry, boolean mutate) {
        GeometryType type = geometry.type();
        if (type == GeometryType.POINT) {
            return geometry;
        }

        T newGeometry;
        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        switch (type) {
            case LINE: {
                List<Point> pointList = cleanLine(Line.line(geometry).coordinates(), type);
                if (pointList == null) {
                    return null;
                }

                Line.line(newGeometry).setCoordinates(pointList);

                return newGeometry;
            }
            case MULTI_LINE: {
                List<List<Point>> newPoints = new ArrayList<>();
                for (List<Point> points : MultiLine.multiLine(geometry).coordinates()) {
                    List<Point> temp = cleanLine(points, type);
                    if (temp == null) {
                        continue;
                    }
                    newPoints.add(temp);
                }
                if (newPoints.isEmpty()) {
                    return null;
                }
                MultiLine.multiLine(newGeometry).setCoordinates(newPoints);

                return newGeometry;
            }
            case POLYGON: {
                List<Point> pointList = cleanLine(Polygon.polygon(geometry).coordinates(), type);
                if (pointList == null) {
                    return null;
                }

                Polygon.polygon(newGeometry).setCoordinates(pointList);

                return newGeometry;
            }
            case MULTI_POLYGON: {
                List<List<Point>> newPoints = new ArrayList<>();
                for (List<Point> points : MultiPolygon.multiPolygon(geometry).coordinates()) {
                    List<Point> temp = cleanLine(points, type);
                    if (temp == null) {
                        continue;
                    }
                    newPoints.add(temp);
                }
                if (newPoints.isEmpty()) {
                    return null;
                }

                MultiPolygon.multiPolygon(newGeometry).setCoordinates(newPoints);

                return newGeometry;
            }
            default:
                throw new JTurfException(type + " geometry not supported");
        }
    }

    /**
     * 处理带有线条性质的重复点集合
     *
     * @param points 点集合
     * @param type   当前处理的多边形类型
     * @return 处理完的新的集合
     */
    private static List<Point> cleanLine(List<Point> points, GeometryType type) {
        int size = points.size();
        if (size == 2 && !JTurfHelper.equals(points.get(0), points.get(1))) {
            return points;
        }

        List<Point> newPoints = new ArrayList<>();
        int secondToLast = points.size() - 1;
        int newPointsLength = newPoints.size();

        newPoints.add(points.get(0));
        for (int i = 1; i < secondToLast; i++) {
            Point prevAddedPoint = newPoints.get(newPoints.size() - 1);
            Point currPoint = points.get(i);

            // 如果当前点与前一个点相同，则直接返回
            if (currPoint.getLongitude() == prevAddedPoint.getLongitude()
                    && currPoint.getLatitude() == prevAddedPoint.getLatitude()) {
                continue;
            }

            newPoints.add(currPoint);
            newPointsLength = newPoints.size();
            if (newPointsLength > 2) {
                // 如果最后第二个元素在最后第三个元素与最后一个元素的线上，则直接将其删除掉
                if (JTurfHelper.isPointOnLineSegment(newPoints.get(newPointsLength - 3),
                        newPoints.get(newPointsLength - 1),
                        newPoints.get(newPointsLength - 2))) {
                    newPoints.remove(newPointsLength - 2);
                }
            }
        }

        Point lastPoint = points.get(points.size() - 1);

        newPoints.add(lastPoint);
        newPointsLength = newPoints.size();

        // 如果第一个点与最后一个点相同，但是点却小于4个，则是一个错误的Polygon图形
        if ((type == GeometryType.POLYGON || type == GeometryType.MULTI_POLYGON) && JTurfHelper.equals(points.get(0), lastPoint) && newPointsLength < 4) {
            return null;
        }

        // 如果是线条类型，则直接返回
        if (type == GeometryType.LINE && newPointsLength < 3) {
            return newPoints;
        }

        // 如果最后第二个元素在最后第三个元素与最后一个元素的线上，则直接将其删除掉
        if (JTurfHelper.isPointOnLineSegment(newPoints.get(newPointsLength - 3),
                newPoints.get(newPointsLength - 1),
                newPoints.get(newPointsLength - 2))) {
            newPoints.remove(newPointsLength - 2);
        }

        return newPoints;
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection 默认按照顺时针返回
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T rewind(T geometry) {
        return rewind(geometry, true, false);
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection 默认按照顺时针返回
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T rewind(T geometry, boolean mutate) {
        return rewind(geometry, true, mutate);
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection true顺时针和false逆时针
     *
     * @param geometry 图形组件
     * @param reverse  true顺时针和false逆时针
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T rewind(T geometry, boolean reverse, boolean mutate) {
        T newGeometry;

        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        if (newGeometry.type() == GeometryType.GEOMETRY_COLLECTION) {
            JTurfMeta.geomEach(newGeometry, (geo, parentGeo, geomIndex) -> {
                rewindGeo(geo, reverse);
                return true;
            });

            return newGeometry;
        } else {
            return rewindGeo(newGeometry, reverse);
        }
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection true顺时针和false逆时针
     *
     * @param geometry 图形组件
     * @param reverse  true顺时针和false逆时针
     * @return 返回处理后的图形组件
     */
    private static <T extends Geometry> T rewindGeo(T geometry, boolean reverse) {
        GeometryType type = geometry.type();

        // Support all GeoJSON Geometry Objects
        switch (type) {
            case GEOMETRY_COLLECTION:
                JTurfMeta.geomEach(geometry, (geo, parentGeo, geomIndex) -> {
                    rewindGeo(geo, reverse);
                    return true;
                });
                return geometry;
            case LINE:
            case POLYGON:
            case MULTI_LINE:
            case MULTI_POLYGON:
                JTurfMeta.coordsEach(geometry, (geo, pointList, multiIndex, geomIndex) -> {
                    rewindCoords(pointList, reverse);
                    return true;
                });
                return geometry;
            case POINT:
            case MULTI_POINT:
                return geometry;
        }
        return null;
    }

    /**
     * Rewind - true顺时针和false逆时针
     *
     * @param coords  点集合
     * @param reverse true顺时针和false逆时针
     */
    private static void rewindCoords(List<Point> coords, boolean reverse) {
        if (JTurfBooleans.booleanClockwise(coords) != reverse) {
            Collections.reverse(coords);
        }
    }

}
