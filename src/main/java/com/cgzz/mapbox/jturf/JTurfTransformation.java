package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.enums.Orientation;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.jturf.util.pkg.clipping.PolygonClippingHelper;
import com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping.PolygonClipping;
import com.cgzz.mapbox.jturf.util.transformation.SimplifyHelper;
import com.cgzz.mapbox.jturf.util.transformation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JTurfTransformation {

    private static final int DEFAULT_STEPS = 64;

    private JTurfTransformation() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 给定一个中心点和要求的半径，使用默认的64步长绘制一个多边形的圆
     *
     * @param center 中心点位
     * @param radius 半径，单位公里
     * @return 多边形圆
     */
    public static Polygon circle(Point center, double radius) {
        return circle(center, radius, DEFAULT_STEPS, Units.KILOMETERS);
    }

    /**
     * 给定一个中心点和要求的半径，使用默认的64步长绘制一个多边形的圆
     *
     * @param center 中心点位
     * @param radius 半径
     * @param units  半径单位
     * @return 多边形圆
     */
    public static Polygon circle(Point center, double radius, Units units) {
        return circle(center, radius, DEFAULT_STEPS, units);
    }

    /**
     * 给定一个中心点和要求的半径，绘制一个多边形的圆
     *
     * @param center 中心点位
     * @param radius 半径
     * @param steps  步长
     * @param units  半径单位
     * @return 多边形圆
     */
    public static Polygon circle(Point center, double radius, int steps, Units units) {
        List<Point> coordinate = new ArrayList<>(steps + 1);
        for (int i = 0; i < steps; i++) {
            coordinate.add(JTurfMeasurement.destination(center, radius, i * 360d / steps, units));
        }

        if (coordinate.size() > 0) {
            coordinate.add(Point.fromLngLat(coordinate.get(0)));
        }
        return Polygon.fromLngLats(Collections.singletonList(coordinate));
    }

    /**
     * 深度克隆一个 geometry 对象
     *
     * @param geometry 图形组件
     * @return 返回克隆后的图形组件
     */
    @SuppressWarnings("unchecked")
    public static <T extends Geometry> T clone(T geometry) {
        if (geometry == null) {
            return null;
        }

        return (T) DeepCloneHelper.deepClone(geometry);
    }

    /**
     * 边界裁剪<br>
     * 使用lineclip将该图形裁切到bbox。在裁剪多边形时可能导致退化边缘。<br>
     * 图形支持：POLYGON、MULTI_POLYGON、LINE、MULTI_LINE
     *
     * @param geometry 图形组件
     * @param bbox     裁剪区域
     * @return 返回裁剪后的图形，这里如果是LINE类型的话有可能会返回MultiLine，需要自己判断。
     */
    public static Geometry bboxClip(Geometry geometry, BoundingBox bbox) {
        return BboxClipHelper.bboxClip(geometry, bbox);
    }

    /**
     * 获取一条直线应用 贝塞尔算法 (opens new window)返回一个贝塞尔曲线。duration默认为 {@link BezierSplineHelper#DURATION}，sharpness 默认为 {@link BezierSplineHelper#SHARPNESS}
     *
     * @param line 线条组件
     * @return 返回线条组件
     */
    public static LineString bezierSpline(LineString line) {
        return bezierSpline(line, BezierSplineHelper.DURATION, BezierSplineHelper.SHARPNESS);
    }

    /**
     * 获取一条直线应用 贝塞尔算法 (opens new window)返回一个贝塞尔曲线。sharpness 默认为 {@link BezierSplineHelper#SHARPNESS}
     *
     * @param line     线条组件
     * @param duration 相邻两个点之间的时间间隔（以毫秒为单位）
     * @return 返回线条组件
     */
    public static LineString bezierSpline(LineString line, int duration) {
        return bezierSpline(line, duration, BezierSplineHelper.SHARPNESS);
    }

    /**
     * 获取一条直线应用 贝塞尔算法 (opens new window)返回一个贝塞尔曲线。
     *
     * @param line      线条组件
     * @param duration  相邻两个点之间的时间间隔（以毫秒为单位）
     * @param sharpness 样条线之间路径的弯曲值
     * @return 返回线条组件
     */
    public static LineString bezierSpline(LineString line, int duration, double sharpness) {
        List<Point> coords = BezierSplineHelper.bezierSpline(line.coordinates(), duration, sharpness);
        if (coords.isEmpty()) {
            return null;
        }
        return LineString.fromLngLats(coords);
    }

    /**
     * 缩放
     * <p>
     * 将GeoJSON从质心缩放一个因子（例如：factor=2会使GeoJSON大200%）。
     *
     * @param geometry 要缩放的组件
     * @param factor   缩放比
     * @return 返回缩放后的图形组件
     */
    public static <T extends Geometry> T transformScale(T geometry, double factor) {
        Point origin = TransformScaleHelper.defineOrigin(geometry, null);

        return transformScale(geometry, factor, origin, false);
    }

    /**
     * 缩放
     * <p>
     * 将GeoJSON从给定点缩放一个因子（例如：factor=2会使GeoJSON大200%）。
     *
     * @param geometry    要缩放的组件
     * @param factor      缩放比
     * @param orientation 缩放开始的点位，默认为质心
     * @return 返回缩放后的图形组件
     */
    public static <T extends Geometry> T transformScale(T geometry, double factor, Orientation orientation) {
        Point origin = TransformScaleHelper.defineOrigin(geometry, orientation);

        return transformScale(geometry, factor, origin, false);
    }

    /**
     * 缩放
     * <p>
     * 将GeoJSON从给定点缩放一个因子（例如：factor=2会使GeoJSON大200%）。
     *
     * @param geometry    要缩放的组件
     * @param factor      缩放比
     * @param orientation 缩放开始的点位，默认为质心
     * @param mutate      是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回缩放后的图形组件
     */
    public static <T extends Geometry> T transformScale(T geometry, double factor, Orientation orientation, boolean mutate) {
        Point origin = TransformScaleHelper.defineOrigin(geometry, orientation);

        return transformScale(geometry, factor, origin, mutate);
    }

    /**
     * 缩放
     * <p>
     * 将GeoJSON从给定点缩放一个因子（例如：factor=2会使GeoJSON大200%）。
     *
     * @param geometry 要缩放的组件
     * @param factor   缩放比
     * @param origin   缩放开始的点
     * @return 返回缩放后的图形组件
     */
    public static <T extends Geometry> T transformScale(T geometry, double factor, Point origin) {
        return transformScale(geometry, factor, origin, false);
    }

    /**
     * 缩放
     * <p>
     * 将GeoJSON从给定点缩放一个因子（例如：factor=2会使GeoJSON大200%）。
     *
     * @param geometry 要缩放的组件
     * @param factor   缩放比
     * @param origin   缩放开始的点
     * @param mutate   是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回缩放后的图形组件
     */
    public static <T extends Geometry> T transformScale(T geometry, double factor, Point origin, boolean mutate) {
        T newGeometry = mutate ? geometry : JTurfTransformation.clone(geometry);

        return TransformScaleHelper.scale(newGeometry, factor, origin);
    }

    /**
     * 平移
     * <p>
     * 在给定的方向角上沿恒向线移动指定距离（单位公里）。
     *
     * @param geometry  要平移的组件
     * @param distance  运动的长度;负值决定相反方向的运动
     * @param direction 向北的角度，顺时针正
     * @param mutate    是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回平移后的图形组件
     */
    public static <T extends Geometry> T transformTranslate(T geometry, double distance, double direction, boolean mutate) {
        return transformTranslate(geometry, distance, direction, null, mutate);
    }

    /**
     * 平移
     * <p>
     * 在给定的方向角上沿恒向线移动指定距离（单位公里）。
     *
     * @param geometry  要平移的组件
     * @param distance  运动的长度;负值决定相反方向的运动
     * @param direction 向北的角度，顺时针正
     * @return 返回平移后的图形组件
     */
    public static <T extends Geometry> T transformTranslate(T geometry, double distance, double direction) {
        return transformTranslate(geometry, distance, direction, null, false);
    }

    /**
     * 平移
     * <p>
     * 在给定的方向角上沿恒向线移动指定距离。
     *
     * @param geometry  要平移的组件
     * @param distance  运动的长度;负值决定相反方向的运动
     * @param direction 向北的角度，顺时针正
     * @param units     距离单位，为空默认为公里，支持 MILES、 KILOMETERS、 DEGREES OR RADIANS
     * @return 返回平移后的图形组件
     */
    public static <T extends Geometry> T transformTranslate(T geometry, double distance, double direction, Units units) {
        return transformTranslate(geometry, distance, direction, units, false);
    }

    /**
     * 平移
     * <p>
     * 在给定的方向角上沿恒向线移动指定距离。
     *
     * @param geometry  要平移的组件
     * @param distance  运动的长度;负值决定相反方向的运动
     * @param direction 向北的角度，顺时针正
     * @param units     距离单位，为空默认为公里，支持 MILES、 KILOMETERS、 DEGREES OR RADIANS
     * @param mutate    是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回平移后的图形组件
     */
    public static <T extends Geometry> T transformTranslate(T geometry, double distance, double direction, Units units, boolean mutate) {
        // 没有产生任何位移，则直接返回
        if (distance == 0) {
            return geometry;
        }

        T newGeometry = mutate ? geometry : JTurfTransformation.clone(geometry);

        double d = distance < 0 ? -distance : distance;
        double bearing = distance < 0 ? direction + 180 : direction;

        JTurfMeta.coordEach(newGeometry, (point, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            point.setCoords(JTurfMeasurement.rhumbDestination(point, d, bearing, units));

            return true;
        });

        return newGeometry;
    }

    /**
     * 旋转（围绕质心）
     * <p>
     * 旋转任何组件，围绕其质心或一个给定的枢轴点;所有的旋转都遵循右手规则:https://en.wikipedia.org/wiki/righthand_rule
     *
     * @param geometry 要旋转的组件
     * @param angle    旋转角度，顺时针方向
     * @return 返回旋转后的图形组件
     */
    public static <T extends Geometry> T transformRotate(T geometry, double angle) {
        return transformRotate(geometry, angle, null, false);
    }

    /**
     * 旋转
     * <p>
     * 旋转任何组件，围绕其质心或一个给定的枢轴点;所有的旋转都遵循右手规则:https://en.wikipedia.org/wiki/righthand_rule
     *
     * @param geometry 要旋转的组件
     * @param angle    旋转角度，顺时针方向
     * @param pivot    围绕其执行旋转的点，为空默认为质心
     * @return 返回旋转后的图形组件
     */
    public static <T extends Geometry> T transformRotate(T geometry, double angle, Point pivot) {
        return transformRotate(geometry, angle, pivot, false);
    }

    /**
     * 旋转
     * <p>
     * 旋转任何组件，围绕其质心或一个给定的枢轴点;所有的旋转都遵循右手规则:https://en.wikipedia.org/wiki/righthand_rule
     *
     * @param geometry 要旋转的组件
     * @param angle    旋转角度，顺时针方向
     * @param pivot    围绕其执行旋转的点，为空默认为质心
     * @param mutate   是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回旋转后的图形组件
     */
    public static <T extends Geometry> T transformRotate(T geometry, double angle, Point pivot, boolean mutate) {
        return TransformRotateHelper.transformRotate(geometry, angle, pivot, mutate);
    }

    /**
     * 多线段偏移
     * <p>
     * 获取一条线并返回具有指定距离偏移量的线。
     *
     * @param geometry 偏移的线段，仅支持 Line、MultiLine
     * @param distance 偏移线的距离（可以是负值），默认单位公里
     * @return 返回偏移后的线
     */
    public static <T extends Geometry> T lineOffset(T geometry, double distance) {
        return lineOffset(geometry, distance, null);
    }

    /**
     * 多线段偏移
     * <p>
     * 获取一条线并返回具有指定距离偏移量的线。
     *
     * @param geometry 偏移的线段，仅支持 Line、MultiLine
     * @param distance 偏移线的距离（可以是负值）
     * @param units    距离单位，支持 DEGREES，RADIANS，MILES，KILOMETERS，INCHES，YARDS，METERS，不填写则默认为 KILOMETERS
     * @return 返回偏移后的线
     */
    public static <T extends Geometry> T lineOffset(T geometry, double distance, Units units) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }

        if (distance == 0) {
            return geometry;
        }
        if (units == null) {
            units = Units.KILOMETERS;
        }

        return LineOffsetHelper.lineOffset(geometry, distance, units);
    }

    /**
     * 多边形划分三角形<br>
     * 使用 earcut 算法将 Polygon、MultiPolygon 细分为多个三角形。
     *
     * @param geometry 仅支持Polygon、MultiPolygon
     * @return 三角形Polygon集合
     */
    public static FeatureCollection<Polygon> tesselate(Geometry geometry) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }

        return TesselateHelper.tesselate(geometry);
    }

    /**
     * 简化多边形<br>
     * 获取对象并返回简化版本。内部使用 simplify-js 使用 Ramer–Douglas–Peucker 算法执行简化。
     *
     * @param geometry 要处理的图形组件
     * @return 返回简化后的图形
     */
    public static <T extends Geometry> T simplify(T geometry) {
        return simplify(geometry, 1, false, false);
    }

    /**
     * 简化多边形<br>
     * 获取对象并返回简化版本。内部使用 simplify-js 使用 Ramer–Douglas–Peucker 算法执行简化。
     *
     * @param geometry  要处理的图形组件
     * @param tolerance 简化公差，不能为负数
     * @return 返回简化后的图形
     */
    public static <T extends Geometry> T simplify(T geometry, double tolerance) {
        return simplify(geometry, tolerance, false, false);
    }

    /**
     * 简化多边形<br>
     * 获取对象并返回简化版本。内部使用 simplify-js 使用 Ramer–Douglas–Peucker 算法执行简化。
     *
     * @param geometry    要处理的图形组件
     * @param tolerance   简化公差，不能为负数
     * @param highQuality 是否花更多时间使用不同的算法创建更高质量的简化
     * @return 返回简化后的图形
     */
    public static <T extends Geometry> T simplify(T geometry, double tolerance, boolean highQuality) {
        return simplify(geometry, tolerance, highQuality, false);
    }

    /**
     * 简化多边形<br>
     * 获取对象并返回简化版本。内部使用 simplify-js 使用 Ramer–Douglas–Peucker 算法执行简化。
     *
     * @param geometry    要处理的图形组件
     * @param tolerance   简化公差，不能为负数
     * @param highQuality 是否花更多时间使用不同的算法创建更高质量的简化
     * @param mutate      是否影响原图形坐标（如果为真，性能将显著提高）
     * @return 返回简化后的图形
     */
    public static <T extends Geometry> T simplify(T geometry, double tolerance, boolean highQuality, boolean mutate) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }
        if (tolerance < 0) {
            throw new JTurfException("invalid tolerance");
        }
        T newGeometry = mutate ? geometry : JTurfTransformation.clone(geometry);

        return SimplifyHelper.simplify(newGeometry, tolerance, highQuality);
    }

    /**
     * 联合<br>
     * 获取两个或多个多边形，并返回一个组合多边形。如果输入的多边形不是连续的，这个函数将返回一个MultiPolygon。
     *
     * @param geometry1 仅支持 Polygon和MultiPolygon
     * @param geometry2 仅支持 Polygon和MultiPolygon
     * @return 返回组合后的图形，如果输入为空，则为 null
     */
    public static Geometry union(Geometry geometry1, Geometry geometry2) {
        return PolygonClippingHelper.polygonClipping(geometry1, geometry2, PolygonClipping::union);
    }

    /**
     * 计算交集<br>
     * 取两个多边形并找到它们的交点。如果它们共享一个边界，返回边界;如果它们不相交，返回
     *
     * @param geometry1 仅支持 Polygon和MultiPolygon
     * @param geometry2 仅支持 Polygon和MultiPolygon
     * @return 返回交集图形，如果输入为空，则为 null。如果它们不共享任何区域，则返回null
     */
    public static Geometry intersect(Geometry geometry1, Geometry geometry2) {
        return PolygonClippingHelper.polygonClipping(geometry1, geometry2, PolygonClipping::intersection);
    }

    /**
     * 计算差异<br>
     * 通过裁剪第二个多边形来找到两个多边形之间的差异。
     *
     * @param geometry1 仅支持 Polygon和MultiPolygon
     * @param geometry2 仅支持 Polygon和MultiPolygon
     * @return 返回差异图形，如果输入为空，则为 null。如果它们有交集，则去除交集，保留差异部分。
     */
    public static Geometry difference(Geometry geometry1, Geometry geometry2) {
        return PolygonClippingHelper.polygonClipping(geometry1, geometry2, PolygonClipping::difference);
    }

    /**
     * 计算补集<br>
     * 通过计算获取第二个图形相对第一个图形的补集。
     *
     * @param geometry1 仅支持 Polygon和MultiPolygon
     * @param geometry2 仅支持 Polygon和MultiPolygon
     * @return 返回补集图形，如果输入为空，则为 null。
     */
    public static Geometry xor(Geometry geometry1, Geometry geometry2) {
        return PolygonClippingHelper.polygonClipping(geometry1, geometry2, PolygonClipping::xor);
    }

    /**
     * 计算缓冲区（辐射区），距离单位默认为KILOMETERS，steps默认为8。<br>
     * 计算组件在给定半径的缓冲区。支持的单位有英里、公里和度数。<br>
     * 使用负半径时，生成的几何图形可能无效，如果与半径大小相比，它太小了。如果是集合类组件，输出集合的成员可能少于输入，甚至为空。
     *
     * @param geometry 要计算的组件
     * @param radius   绘制缓冲区的距离（允许负值）
     * @return 缓冲区的图形组件
     */
    public static Geometry buffer(Geometry geometry, int radius) {
        return buffer(geometry, radius, null, null);
    }

    /**
     * 计算缓冲区（辐射区），steps默认为8。<br>
     * 计算组件在给定半径的缓冲区。支持的单位有英里、公里和度数。<br>
     * 使用负半径时，生成的几何图形可能无效，如果与半径大小相比，它太小了。如果是集合类组件，输出集合的成员可能少于输入，甚至为空。
     *
     * @param geometry 要计算的组件
     * @param radius   绘制缓冲区的距离（允许负值）
     * @param units    距离单位，默认 KILOMETERS
     * @return 缓冲区的图形组件
     */
    public static Geometry buffer(Geometry geometry, int radius, Units units) {
        return buffer(geometry, radius, units, null);
    }

    /**
     * 计算缓冲区（辐射区）<br>
     * 计算组件在给定半径的缓冲区。支持的单位有英里、公里和度数。<br>
     * 使用负半径时，生成的几何图形可能无效，如果与半径大小相比，它太小了。如果是集合类组件，输出集合的成员可能少于输入，甚至为空。
     *
     * @param geometry 要计算的组件
     * @param radius   绘制缓冲区的距离（允许负值）
     * @param units    距离单位，默认 KILOMETERS
     * @param steps    频数，默认为 8
     * @return 缓冲区的图形组件
     */
    public static Geometry buffer(Geometry geometry, int radius, Units units, Integer steps) {
        return BufferHelper.buffer(geometry, radius, units, steps);
    }

}
