package com.cgzz.mapbox.jturf.util.polygonclipping;

import com.cgzz.mapbox.jturf.shape.Point;

import java.util.ArrayList;
import java.util.List;

public final class Location {

    double x;

    double y;

    private List<SweepEvent> events;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Location(Point p) {
        this(p.getX(), p.getY());
    }

    public static Location location(double x, double y) {
        return new Location(x, y);
    }

    public static Location location(Point p) {
        return new Location(p);
    }

    public void addEvent(SweepEvent event) {
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
    }

    public void clearEvent() {
        events = null;
    }

    public List<SweepEvent> getEvents() {
        return events;
    }

    public int eventSize() {
        if (events == null) {
            return 0;
        } else {
            return events.size();
        }
    }
}
