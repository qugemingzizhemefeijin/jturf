package com.cgzz.mapbox.test.turf;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfJoins;
import com.mapbox.turf.TurfMeasurement;
import com.mapbox.turf.TurfTransformation;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TurfTest {

    // 官网网站 https://docs.mapbox.com/android/java/guides/turf/
    // https://www.tabnine.com/code/java/methods/com.mapbox.geojson.Point/fromLngLat
    // https://github.com/mapbox/mapbox-java/blob/main/docs/turf-port.md#unit-conversion

    // turf.js 中文官网 https://turfjs.fenxianglu.cn/category/

    @Test
    public void testDistance() {
        Point ggPoint = Point.fromLngLat(116.396939, 39.921382);
        Point xdscPoint = Point.fromLngLat(116.3812, 39.918919);

        System.out.println("两点距离：" + TurfMeasurement.distance(ggPoint, xdscPoint));
    }

    @Test
    public void testCircleContainsPoint() {
        Point ggPoint = Point.fromLngLat(116.396939, 39.921382);
        Point xdscPoint = Point.fromLngLat(116.3812, 39.918919);

        Point point = Point.fromLngLat(116.33009, 39.90728);
        Polygon polygon = TurfTransformation.circle(point, 5000, TurfConstants.UNIT_METRES);

        System.out.println("故宫点是否在圆中：" + TurfJoins.inside(ggPoint, polygon));
        System.out.println("西单商场点是否在圆中：" + TurfJoins.inside(xdscPoint, polygon));
    }

    @Test
    public void testPolygonContainsPoint() {
        Point fxPoint = Point.fromLngLat(116.364528, 39.912832);
        Point gdjiudian = Point.fromLngLat(116.361716, 39.909449);
        Polygon polygon = createPolygon(new double[][]{new double[]{116.28044, 39.913773}, new double[]{116.279945, 39.917263}, new double[]{116.28196, 39.92645}, new double[]{116.28973, 39.92634}, new double[]{116.29138, 39.930504}, new double[]{116.3116, 39.929943}, new double[]{116.316605, 39.9299}, new double[]{116.33293, 39.929085}, new double[]{116.33617, 39.92888}, new double[]{116.34127, 39.9286}, new double[]{116.34135, 39.923607}, new double[]{116.34116, 39.919666}, new double[]{116.34393, 39.919666}, new double[]{116.3483, 39.91964}, new double[]{116.352425, 39.91964}, new double[]{116.35694, 39.919624}, new double[]{116.36066, 39.919624}, new double[]{116.36335, 39.919678}, new double[]{116.3633, 39.916954}, new double[]{116.36323, 39.91481}, new double[]{116.36324, 39.912777}, new double[]{116.36324, 39.90554}, new double[]{116.36328, 39.904793}, new double[]{116.36526, 39.904987}, new double[]{116.36738, 39.905178}, new double[]{116.36971, 39.905346}, new double[]{116.37201, 39.90551}, new double[]{116.37431, 39.905594}, new double[]{116.38103, 39.905926}, new double[]{116.38114, 39.905014}, new double[]{116.38103, 39.90374}, new double[]{116.381, 39.90294}, new double[]{116.38092, 39.902054}, new double[]{116.38103, 39.90061}, new double[]{116.38096, 39.89934}, new double[]{116.3811, 39.897846}, new double[]{116.38117, 39.895355}, new double[]{116.38081, 39.889263}, new double[]{116.35595, 39.8886}, new double[]{116.35559, 39.87398}, new double[]{116.34258, 39.87368}, new double[]{116.33806, 39.873734}, new double[]{116.33338, 39.873844}, new double[]{116.32807, 39.873817}, new double[]{116.30087, 39.872433}, new double[]{116.29033, 39.871975}, new double[]{116.288605, 39.87408}, new double[]{116.28788, 39.875874}, new double[]{116.28687, 39.877316}, new double[]{116.286446, 39.878532}, new double[]{116.28426, 39.88122}, new double[]{116.28386, 39.881996}, new double[]{116.28358, 39.882996}, new double[]{116.28329, 39.883713}, new double[]{116.28322, 39.884487}, new double[]{116.282715, 39.88554}, new double[]{116.28207, 39.887424}, new double[]{116.281136, 39.892017}, new double[]{116.280556, 39.907406}, new double[]{116.28044, 39.913773}});

        // 判断多边形与点的关系
        System.out.println("复兴门是否在围栏中: {}" + TurfJoins.inside(fxPoint, polygon));
        System.out.println("广电国际酒店是否在围栏中: " + TurfJoins.inside(gdjiudian, polygon));


        double area = TurfMeasurement.area(polygon);
        System.out.println("围栏的面积为：" + new BigDecimal(area / 1000000).setScale(2, RoundingMode.HALF_UP).toString() + "平方千米");
    }

    private static Polygon createPolygon(double[][] area) {
        List<Point> points = new ArrayList<Point>(area.length);
        for (double[] coordinate : area) {
            points.add(Point.fromLngLat(coordinate[0], coordinate[1]));
        }
        List<List<Point>> coordinates = new ArrayList<>(1);
        coordinates.add(points);
        return Polygon.fromLngLats(coordinates);
    }

}
