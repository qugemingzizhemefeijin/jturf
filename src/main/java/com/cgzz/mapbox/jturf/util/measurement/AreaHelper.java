package com.cgzz.mapbox.jturf.util.measurement;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.List;

public final class AreaHelper {

    private AreaHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 计算面积（以平方米为单位）。
     *
     * @param geometry 支持 POLYGON、MULTI_POLYGON、GEOMETRY_COLLECTION（Feature也要符合以上规则）
     * @return 面积（平方米）
     */
    public static double area(Geometry geometry) {
        geometry = JTurfMeta.getGeom(geometry);
        double total = 0.0D;

        switch (geometry.geometryType()) {
            case POLYGON:
                total = polygonArea(Polygon.polygon(geometry).coordinates());
                break;
            case MULTI_POLYGON:
                for (List<List<Point>> coordinate : MultiPolygon.multiPolygon(geometry).coordinates()) {
                    total += polygonArea(coordinate);
                }
                break;
            case GEOMETRY_COLLECTION:
                for (Geometry singleGeometry : GeometryCollection.geometryCollection(geometry).geometries()) {
                    total += area(singleGeometry);
                }
                break;
            case FEATURE_COLLECTION:
                for (Feature<Geometry> feature : FeatureCollection.featureCollection(geometry).geometries()) {
                    total += area(feature.geometry());
                }
        }

        return total;
    }

    /**
     * 计算Polygon的多边形面积（减除了内圈的面积）
     *
     * @param coordinates Polygon的点坐标集合
     * @return 面的近似符号测地线面积（以平方米为单位）
     */
    private static double polygonArea(List<List<Point>> coordinates) {
        double total = 0;

        if (coordinates == null || coordinates.isEmpty()) {
            return total;
        }

        total += Math.abs(ringArea(coordinates.get(0)));

        for (int i = 1, size = coordinates.size(); i < size; i++) {
            total -= Math.abs(ringArea(coordinates.get(i)));
        }
        return total;
    }

    /**
     * 计算投影到地球上的多边形的大致面积。请注意，如果环方向为顺时针方向，则此区域将为正，否则它将是负面的。
     *
     * @param coordinates 坐标点集合
     * @return 面的近似符号测地线面积（以平方米为单位）。
     */
    private static double ringArea(List<Point> coordinates) {
        Point p1, p2, p3;
        int lowerIndex, middleIndex, upperIndex;
        double total = 0.0f;
        final int coordsLength = coordinates.size();

        if (coordsLength > 2) {
            for (int i = 0; i < coordsLength; i++) {
                if (i == coordsLength - 2) { // i = N-2
                    lowerIndex = coordsLength - 2;
                    middleIndex = coordsLength - 1;
                    upperIndex = 0;
                } else if (i == coordsLength - 1) { // i = N-1
                    lowerIndex = coordsLength - 1;
                    middleIndex = 0;
                    upperIndex = 1;
                } else { // i = 0 to N-3
                    lowerIndex = i;
                    middleIndex = i + 1;
                    upperIndex = i + 2;
                }
                p1 = coordinates.get(lowerIndex);
                p2 = coordinates.get(middleIndex);
                p3 = coordinates.get(upperIndex);
                total += (JTurfHelper.angleToRadians(p3.longitude()) - JTurfHelper.angleToRadians(p1.longitude())) * Math.sin(JTurfHelper.angleToRadians(p2.latitude()));
            }

            // FIXME 这里在6.5.0的源码中，显示使用的是 6378137 ，但是实际js却使用的是 6371008.8
            total = total * JTurfMeasurement.EARTH_RADIUS * JTurfMeasurement.EARTH_RADIUS / 2;
        }
        return total;
    }

}
