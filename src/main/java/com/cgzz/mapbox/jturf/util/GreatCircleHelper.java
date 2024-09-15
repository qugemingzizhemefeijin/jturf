package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.MultiLineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

public final class GreatCircleHelper {

    private static final double D2R = Math.PI / 180;
    private static final double R2D = 180 / Math.PI;

    // http://en.wikipedia.org/wiki/Great-circle_distance
    private GreatCircleHelper() {
        throw new AssertionError("No Instances.");
    }

    public static Geometry arc(Point start, Point end, int npoints, int offset) {
        double slon = start.longitude(), slat = start.latitude();
        double sx = D2R * slon, sy = D2R * slat;

        double elon = end.longitude(), elat = end.latitude();
        double ex = D2R * elon, ey = D2R * elat;

        double w = sx - ex, h = sy - ey;
        double z = Math.pow(Math.sin(h / 2.0), 2) + Math.cos(sy) * Math.cos(ey) * Math.pow(Math.sin(w / 2.0), 2);
        double g = 2.0 * Math.asin(Math.sqrt(z));

        if (g == Math.PI) {
            throw new JTurfException("it appears " +
                    view(start) +
                    " and " +
                    view(end) +
                    " are 'antipodal', e.g diametrically opposite, thus there is no single route but rather infinite");
        }

        List<double[]> first_pass = new ArrayList<>();
        if (npoints <= 2) {
            first_pass.add(start.getCoords());
            first_pass.add(end.getCoords());
        } else {
            double delta = 1.0 / (npoints - 1);
            for (int i = 0; i < npoints; ++i) {
                double step = delta * i;
                first_pass.add(interpolate(sx, sy, ex, ey, g, step));
            }
        }

        /* partial port of dateline handling from:
            gdal/ogr/ogrgeometryfactory.cpp - does not handle all wrapping scenarios yet
        */
        boolean bHasBigDiff = false;
        double dfMaxSmallDiffLong = 0;
        // from http://www.gdal.org/ogr2ogr.html
        // -datelineoffset:
        // (starting with GDAL 1.10) offset from dateline in degrees (default long. = +/- 10deg, geometries within 170deg to -170deg will be splited)

        int dfDateLineOffset = offset;
        int dfLeftBorderX = 180 - dfDateLineOffset;
        int dfRightBorderX = -180 + dfDateLineOffset;
        int dfDiffSpace = 360 - dfDateLineOffset;

        // https://github.com/OSGeo/gdal/blob/7bfb9c452a59aac958bff0c8386b891edf8154ca/gdal/ogr/ogrgeometryfactory.cpp#L2342
        for (int j = 1, size = first_pass.size(); j < size; ++j) {
            double dfPrevX = first_pass.get(j - 1)[0];
            double dfX = first_pass.get(j)[0];
            double dfDiffLong = Math.abs(dfX - dfPrevX);
            if (dfDiffLong > dfDiffSpace &&
                    ((dfX > dfLeftBorderX && dfPrevX < dfRightBorderX) || (dfPrevX > dfLeftBorderX && dfX < dfRightBorderX))
            ) {
                bHasBigDiff = true;
            } else if (dfDiffLong > dfMaxSmallDiffLong) {
                dfMaxSmallDiffLong = dfDiffLong;
            }
        }

        List<List<Point>> poMulti = new ArrayList<>();
        if (bHasBigDiff && dfMaxSmallDiffLong < dfDateLineOffset) {
            List<Point> poNewLS = new ArrayList<>();
            poMulti.add(poNewLS);

            for (int k = 0, size = first_pass.size(); k < size; ++k) {
                double[] curr = first_pass.get(k);
                double[] last = k > 0 ? first_pass.get(k - 1) : null;

                double dfX0 = curr[0];
                if (k > 0 && Math.abs(dfX0 - last[0]) > dfDiffSpace) {
                    double dfX1 = last[0], dfY1 = last[1];
                    double dfX2 = curr[0], dfY2 = curr[1];
                    if (dfX1 > -180
                            && dfX1 < dfRightBorderX
                            && dfX2 == 180
                            && k + 1 < size
                            && last[0] > -180
                            && last[0] < dfRightBorderX
                    ) {
                        poNewLS.add(Point.fromLngLat(-180, curr[1]));
                        k++;
                        poNewLS.add(Point.fromLngLat(curr[0], curr[1]));
                        continue;
                    } else if (dfX1 > dfLeftBorderX
                            && dfX1 < 180
                            && dfX2 == -180
                            && k + 1 < size
                            && last[0] > dfLeftBorderX
                            && last[0] < 180
                    ) {
                        poNewLS.add(Point.fromLngLat(180, curr[1]));
                        k++;
                        poNewLS.add(Point.fromLngLat(curr[0], curr[1]));
                        continue;
                    }

                    if (dfX1 < dfRightBorderX && dfX2 > dfLeftBorderX) {
                        // swap dfX1, dfX2
                        double tmpX = dfX1;
                        dfX1 = dfX2;
                        dfX2 = tmpX;
                        // swap dfY1, dfY2
                        double tmpY = dfY1;
                        dfY1 = dfY2;
                        dfY2 = tmpY;
                    }
                    if (dfX1 > dfLeftBorderX && dfX2 < dfRightBorderX) {
                        dfX2 += 360;
                    }

                    if (dfX1 <= 180 && dfX2 >= 180 && dfX1 < dfX2) {
                        double dfRatio = (180 - dfX1) / (dfX2 - dfX1);
                        double dfY = dfRatio * dfY2 + (1 - dfRatio) * dfY1;
                        poNewLS.add(Point.fromLngLat(last[0] > dfLeftBorderX ? 180 : -180, dfY));
                        poNewLS = new ArrayList<>();
                        poNewLS.add(Point.fromLngLat(last[0] > dfLeftBorderX ? -180 : 180, dfY));
                        poMulti.add(poNewLS);
                    } else {
                        poNewLS = new ArrayList<>();
                        poMulti.add(poNewLS);
                    }
                    poNewLS.add(Point.fromLngLat(dfX0, curr[1]));
                } else {
                    poNewLS.add(Point.fromLngLat(curr[0], curr[1]));
                }
            }
        } else {
            // add normally
            List<Point> poNewLS0 = new ArrayList<>();
            poMulti.add(poNewLS0);
            for (double[] coords : first_pass) {
                poNewLS0.add(Point.fromLngLat(coords));
            }
        }

        int s = poMulti.size();
        if (s <= 0) {
            return null;
        }

        if (s == 1) {
            return LineString.fromLngLats(poMulti.get(0));
        }

        return MultiLineString.fromLngLats(poMulti);
    }

    // http://williams.best.vwh.net/avform.htm#Intermediate
    private static double[] interpolate(double sx, double sy, double ex, double ey, double g, double f) {
        double A = Math.sin((1 - f) * g) / Math.sin(g);
        double B = Math.sin(f * g) / Math.sin(g);
        double x = A * Math.cos(sy) * Math.cos(sx) + B * Math.cos(ey) * Math.cos(ex);
        double y = A * Math.cos(sy) * Math.sin(sx) + B * Math.cos(ey) * Math.sin(ex);
        double z = A * Math.sin(sy) + B * Math.sin(ey);
        double lat = R2D * Math.atan2(z, Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
        double lon = R2D * Math.atan2(y, x);

        return new double[]{lon, lat};
    }

    private static String view(Point p) {
        return substring(String.valueOf(p.longitude()), 4)
                + ","
                + substring(String.valueOf(p.latitude()), 4);
    }

    private static String substring(String s, int maxLen) {
        if (s.length() < maxLen) {
            return s;
        } else {
            return s.substring(0, maxLen);
        }
    }

}
