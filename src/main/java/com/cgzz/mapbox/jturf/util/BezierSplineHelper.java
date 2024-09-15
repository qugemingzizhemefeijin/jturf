package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * BezierSpline
 * https://github.com/leszekr/bezier-spline-js
 * <p>
 * https://github.com/Turfjs/turf/blob/v6.5.0/packages/turf-bezier-spline/lib/spline.ts#L33
 *
 * @private
 * @copyright Copyright (c) 2013 Leszek Rybicki
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public final class BezierSplineHelper {

    /**
     * 相邻两个点之间的时间间隔（以毫秒为单位），默认值 10000
     */
    public static final int DURATION = 10000;

    /**
     * 样条线之间路径应该有多弯曲值，默认值 0.85
     */
    public static final double SHARPNESS = 0.85d;

    private BezierSplineHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 获取一条直线应用 贝塞尔算法 (opens new window)返回一个贝塞尔曲线。
     * @param points    线条点集合
     * @param duration  相邻两个点之间的时间间隔（以毫秒为单位）
     * @param sharpness 样条线之间路径的弯曲值
     * @return 贝塞尔曲线点集合
     */
    public static List<Point> bezierSpline(List<Point> points, int duration, double sharpness) {
        // int stepLength = 60;
        int length = points.size();
        List<Point> centers = new ArrayList<>(length - 1);
        List<Point[]> controls = new ArrayList<>(length);

        for (int i = 0; i < length - 1; i++) { // length - 1 = points.size() - 1
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            centers.add(Point.fromLngLat((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2));
        }

        controls.add(new Point[]{points.get(0), points.get(0)});
        for (int i = 0; i < length - 2; i++) { // length - 2 = centers.size() - 1
            double dx = points.get(i + 1).getX() - (centers.get(i).getX() + centers.get(i + 1).getX()) / 2;
            double dy = points.get(i + 1).getY() - (centers.get(i).getY() + centers.get(i + 1).getY()) / 2;

            controls.add(new Point[]{
                    sharpnessPoint(points.get(i + 1), centers.get(i), sharpness, dx, dy),
                    sharpnessPoint(points.get(i + 1), centers.get(i + 1), sharpness, dx, dy)
            });
        }
        controls.add(new Point[]{points.get(length - 1), points.get(length - 1)});

        // List<Integer> steps = cacheSteps(points, controls, stepLength, duration);

        List<Point> coords = new ArrayList<>(duration / 10 / 2 + 1);
        for (int i = 0; i < duration; i += 10) {
            pushCoord(points, controls, i, duration, coords);
        }
        // 将自己的最后一个点加入（6.5.0才加入的，看线上的 https://turfjs.fenxianglu.cn/category/transformation/bezierSpline.html 没有加入）
        pushCoord(points, controls, duration, duration, coords);

        return coords;
    }

    private static void pushCoord(List<Point> points, List<Point[]> controls, int time, int duration, List<Point> coords) {
        Point pos = pos(points, controls, time, duration);
        if (Math.floor(time / 100d) % 2 == 0) {
            coords.add(pos);
        }
    }

    /*
     * Caches an array of equidistant (more or less) points on the curve.
     */
//    private static List<Integer> cacheSteps(List<Point> points, List<Point[]> controls, int mindist, int duration) {
//        List<Integer> steps = new ArrayList<>(duration / 10 + 1);
//        steps.add(0);
//
//        Point laststep = pos(points, controls, 0, duration);
//        for (int t = 0; t < duration; t += 10) {
//            Point step = pos(points, controls, t, duration);
//            double dist = Math.sqrt((step.getX() - laststep.getX()) * (step.getX() - laststep.getX()) + (step.getY() - laststep.getY()) * (step.getY() - laststep.getY()));
//            if (dist > mindist) {
//                steps.add(t);
//                laststep = step;
//            }
//        }
//
//        return steps;
//    }

    /**
     * Gets the position of the point, given time.
     * <p>
     * WARNING: The speed is not constant. The time it takes between control points is constant.
     * <p>
     * For constant speed, use Spline.steps[i];
     */
    private static Point pos(List<Point> points, List<Point[]> controls, int time, int duration) {
        int t = time;
        if (t < 0) {
            t = 0;
        }
        if (t > duration) {
            t = duration - 1;
        }

        int length = points.size();
        double t2 = (double) t / duration;
        if (t2 >= 1) {
            return points.get(length - 1);
        }

        int n = (int) Math.floor((length - 1) * t2);
        double t1 = (length - 1) * t2 - n;

        return bezier(t1, points.get(n), controls.get(n)[1], controls.get(n + 1)[0], points.get(n + 1));
    }

    private static Point bezier(double t, Point p1, Point c1, Point c2, Point p2) {
        double[] b = b(t);

        return Point.fromLngLat(
                p2.getX() * b[0] + c2.getX() * b[1] + c1.getX() * b[2] + p1.getX() * b[3],
                p2.getY() * b[0] + c2.getY() * b[1] + c1.getY() * b[2] + p1.getY() * b[3]);
    }

    private static double[] b(double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return new double[]{t3, 3 * t2 * (1 - t), 3 * t * (1 - t) * (1 - t), (1 - t) * (1 - t) * (1 - t)};
    }

    private static Point sharpnessPoint(Point point, Point center, double sharpness, double dx, double dy) {
        return Point.fromLngLat(
                (1.0 - sharpness) * point.getX() + sharpness * (center.getX() + dx),
                (1.0 - sharpness) * point.getY() + sharpness * (center.getY() + dy)
        );
    }

}
