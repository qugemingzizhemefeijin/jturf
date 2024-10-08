package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.geojson.GeoJson;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;

import java.io.Serializable;

public final class BoundingBox implements GeoJson, Serializable {

    /**
     * 西南点
     */
    private final Point southwest;

    /**
     * 东北点
     */
    private final Point northeast;

    BoundingBox(Point southwest, Point northeast) {
        if (southwest == null) {
            throw new NullPointerException("Null southwest");
        }
        this.southwest = southwest;
        if (northeast == null) {
            throw new NullPointerException("Null northeast");
        }
        this.northeast = northeast;
    }

    public static BoundingBox fromLngLats(Point southwest, Point northeast) {
        return new BoundingBox(southwest, northeast);
    }

    public static BoundingBox fromLngLats(double west, double south, double east, double north) {
        return new BoundingBox(Point.fromLngLat(west, south), Point.fromLngLat(east, north));
    }

    public static BoundingBox fromLngLats(double west, double south, double southwestAltitude, double east, double north, double northEastAltitude) {
        return new BoundingBox(Point.fromLngLat(west, south, southwestAltitude), Point.fromLngLat(east, north, northEastAltitude));
    }

    public static BoundingBox fromLngLats(double[] bbox) {
        if (bbox == null || (bbox.length != 4 && bbox.length != 6)) {
            throw new IllegalArgumentException("bbox can not null, and length not eq 4 or 6");
        }

        if (bbox.length == 4) {
            return new BoundingBox(Point.fromLngLat(bbox[0], bbox[1]), Point.fromLngLat(bbox[2], bbox[3]));
        } else {
            return new BoundingBox(Point.fromLngLat(bbox[0], bbox[1], bbox[2]), Point.fromLngLat(bbox[3], bbox[4], bbox[5]));
        }
    }

    public static BoundingBox fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, BoundingBox.class);
    }

    public Point southwest() {
        return southwest;
    }

    public Point northeast() {
        return northeast;
    }

    public Point southeast() {
        return Point.fromLngLat(east(), south());
    }

    public Point northwest() {
        return Point.fromLngLat(west(), north());
    }

    public final double west() {
        return southwest.longitude();
    }

    public final double south() {
        return southwest.latitude();
    }

    public final double east() {
        return northeast.longitude();
    }

    public final double north() {
        return northeast.latitude();
    }

    /**
     * 根据索引获取坐标点，0西，1南，2东，3北
     *
     * @param index 坐标点
     * @return double
     */
    public final double get(int index) {
        if (index == 0) {
            return west();
        } else if (index == 1) {
            return south();
        } else if (index == 2) {
            return east();
        } else {
            return north();
        }
    }

    /**
     * 返回一个double数组，按照 [west, south, east, north] 顺序
     *
     * @return 坐标点数组
     */
    public final double[] bbox() {
        return new double[]{southwest.longitude(), southwest.latitude(), northeast.longitude(), northeast.latitude()};
    }

    @Override
    public String toJson() {
        return GeoJsonUtils.getGson().toJson(this, BoundingBox.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BoundingBox) {
            BoundingBox that = (BoundingBox) obj;
            return (this.southwest.equals(that.southwest()))
                    && (this.northeast.equals(that.northeast()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= southwest.hashCode();
        hashCode *= 1000003;
        hashCode ^= northeast.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "BoundingBox{"
                + "southwest=" + southwest + ", "
                + "northeast=" + northeast
                + "}";
    }

}
