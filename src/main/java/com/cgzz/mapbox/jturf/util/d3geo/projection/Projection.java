package com.cgzz.mapbox.jturf.util.d3geo.projection;

import com.cgzz.mapbox.jturf.util.d3geo.Algorithm;
import com.cgzz.mapbox.jturf.util.d3geo.Compose;
import com.cgzz.mapbox.jturf.util.d3geo.rotation.Rotation;
import com.cgzz.mapbox.jturf.util.d3geo.util.MathUtil;

public class Projection {

    private double k = 150; // scale
    private double x = 480, y = 250; // translate
    private double dx, dy, lambda = 0, phi = 0; // center
    private double deltaLambda = 0, deltaPhi = 0, deltaGamma = 0; // rotate
    private Double theta = null;
    // private preclip = clipAntimeridian; // clip angle

    private final Algorithm project;
    private Algorithm rotate = null, projectRotate = null;

    public Projection(Algorithm project) {
        this.project = project;
        recenter();
    }

    public double[] projection(double[] point) {
        point = projectRotate.def(point[0] * MathUtil.RADIANS, point[1] * MathUtil.RADIANS);
        return new double[]{point[0] * k + dx, dy - point[1] * k};
    }

    public double[] invert(double[] point) {
        point = projectRotate.invert((point[0] - dx) / k, (dy - point[1]) / k);
        if (point != null) {
            return new double[]{point[0] * MathUtil.DEGREES, point[1] * MathUtil.DEGREES};
        } else {
            return null;
        }
    }

    public double[] projectTransform(double x, double y) {
        double[] xx = project.def(x, y);
        return new double[]{xx[0] * k + dx, dy - xx[1] * k};
    }

    public Projection scale(double s) {
        k = +s;
        return recenter();
    }

    public Projection clipAngle(double s) {
        /*if (+s != 0) {
            theta = s * MathUtil.RADIANS;
            preclip = clipCircle(theta, 6 * MathUtil.RADIANS);
        } else {
            theta = null;
            preclip = clipAntimeridian;
        }*/
        return reset();
    }

    public Projection rotate(double[] point) {
        deltaLambda = point[0] % 360 * MathUtil.RADIANS;
        deltaPhi = point[1] % 360 * MathUtil.RADIANS;
        deltaGamma = point.length > 2 ? point[2] % 360 * MathUtil.RADIANS : 0;
        return recenter();
    }

    public Projection recenter() {
        rotate = Rotation.rotateRadians(deltaLambda, deltaPhi, deltaGamma);
        projectRotate = new Compose(rotate, project);
        double[] center = project.def(lambda, phi);
        dx = x - center[0] * k;
        dy = y + center[1] * k;

        return reset();
    }

    public Projection reset() {
        // cache = cacheStream = null;
        return this;
    }

}
