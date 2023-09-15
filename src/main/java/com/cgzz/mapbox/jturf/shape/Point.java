package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;

public final class Point extends GeometryProperties implements CoordinateContainer<Point, Point>, Comparable<Point> {

    private double longitude;

    private double latitude;

    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static Point fromLngLat(double longitude, double latitude) {
        return new Point(longitude, latitude);
    }

    public static Point fromLngLat(double[] coords) {
        if (coords.length == 2) {
            return Point.fromLngLat(coords[0], coords[1]);
        }
        throw new JTurfException("point must two number");
    }

    public static Point fromLngLat(Point p) {
        return Point.fromLngLat(p.getLongitude(), p.getLatitude());
    }

    public static Point point(Geometry geometry) {
        return (Point)geometry;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getX() {
        return longitude;
    }

    public double getY() {
        return latitude;
    }

    @Override
    public String toString() {
        return "Point{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public Point coordinates() {
        return this;
    }

    @Override
    public void setCoordinates(Point coordinates) {
        this.longitude = coordinates.longitude;
        this.latitude = coordinates.latitude;
    }

    @Override
    public int coordsSize() {
        return 1;
    }

    @Override
    public String toViewCoordsString() {
        return "[" + this.longitude + "," + this.latitude + "]\n";
    }

    public double[] getCoord() {
        return new double[]{longitude, latitude};
    }

    @Override
    public Point deepClone() {
        return fromLngLat(this.longitude, this.latitude);
    }

    @Override
    public GeometryType type() {
        return GeometryType.POINT;
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
