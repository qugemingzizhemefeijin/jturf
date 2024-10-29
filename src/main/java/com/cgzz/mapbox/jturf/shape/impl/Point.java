package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;

import java.io.Serializable;
import java.util.List;

public final class Point implements CoordinateContainer<Point>, Serializable, Comparable<Point> {

    private double longitude;

    private double latitude;

    private double altitude;

    Point(double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public static Point fromLngLat(double longitude, double latitude) {
        return new Point(longitude, latitude, Double.NaN);
    }

    public static Point fromLngLat(double longitude, double latitude, double altitude) {
        return new Point(longitude, latitude, altitude);
    }

    public static Point fromLngLat(double[] coords) {
        if (coords.length == 2) {
            return Point.fromLngLat(coords[0], coords[1]);
        } else if (coords.length > 2) {
            return Point.fromLngLat(coords[0], coords[1], coords[2]);
        }
        throw new JTurfException("point must two number");
    }

    public static Point fromLngLat(List<Double> coordinates) {
        if (coordinates.size() == 2) {
            return Point.fromLngLat(coordinates.get(0), coordinates.get(1));
        } else if (coordinates.size() > 2) {
            return Point.fromLngLat(coordinates.get(0), coordinates.get(1), coordinates.get(2));
        }
        throw new JTurfException("point must two number");
    }

    public static Point fromLngLat(Point p) {
        return Point.fromLngLat(p.longitude, p.latitude, p.altitude);
    }

    public static Point fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, Point.class);
    }

    public static Point point(Geometry geometry) {
        return (Point) geometry;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.POINT;
    }

    @Override
    public Point coordinates() {
        return this;
    }

    @Override
    public void coordinates(Point coordinates) {
        throw new JTurfException("not support operator");
    }

    @Override
    public String toViewCoordsString() {
        if (hasAltitude()) {
            return "[" + this.longitude + "," + this.latitude + "," + this.altitude + "]\n";
        } else {
            return "[" + this.longitude + "," + this.latitude + "]\n";
        }
    }

    @Override
    public String toJson() {
        return GeoJsonUtils.getGson().toJson(this);
    }

    public double longitude() {
        return longitude;
    }

    public double latitude() {
        return latitude;
    }

    public double altitude() {
        return altitude;
    }

    public boolean hasAltitude() {
        return !Double.isNaN(altitude);
    }

    public double getX() {
        return longitude;
    }

    public double getY() {
        return latitude;
    }

    public double getZ() {
        return altitude;
    }

    public double[] getCoords() {
        if (hasAltitude()) {
            return new double[]{longitude, latitude, altitude};
        } else {
            return new double[]{longitude, latitude};
        }
    }

    public void setCoords(Point p) {
        this.longitude = p.longitude;
        this.latitude = p.latitude;
        this.altitude = p.altitude;
    }

    public void setCoords(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setCoords(double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Point) {
            Point that = (Point) obj;

            boolean z1 = this.hasAltitude(), z2 = that.hasAltitude();
            if (z1 == z2) {
                if (z1) {
                    return this.longitude == that.longitude && this.latitude == that.latitude && this.altitude == that.altitude;
                } else {
                    return this.longitude == that.longitude && this.latitude == that.latitude;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= Double.hashCode(longitude);
        hashCode *= 1000003;
        hashCode ^= Double.hashCode(latitude);
        if (hasAltitude()) {
            hashCode *= 1000003;
            hashCode ^= Double.hashCode(altitude);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (hasAltitude()) {
            return "Point{"
                    + "coordinates=[" + longitude + ", " + latitude + ", " + altitude + "]"
                    + "}";
        } else {
            return "Point{"
                    + "coordinates=[" + longitude + ", " + latitude + "]"
                    + "}";
        }
    }

    @Override
    public int compareTo(Point o) {
        if (this.longitude == o.longitude) {
            if (this.latitude == o.latitude) {
                return 0;
            }
            return this.latitude > o.latitude ? 1 : -1;
        }
        if (this.longitude > o.longitude) {
            return 1;
        } else {
            return -1;
        }
    }

}
