package com.cgzz.mapbox.jturf.models;

import java.util.ArrayList;
import java.util.List;

public class TesselateResult {

    private List<Double> vertices;

    private List<Integer> holes;

    private int dimensions;

    public TesselateResult(int dimensions) {
        this.vertices = new ArrayList<>();
        this.holes = new ArrayList<>();
        this.dimensions = dimensions;
    }

    public List<Double> getVertices() {
        return vertices;
    }

    public void setVertices(List<Double> vertices) {
        this.vertices = vertices;
    }

    public void pushVertices(Double v) {
        this.vertices.add(v);
    }

    public List<Integer> getHoles() {
        return holes;
    }

    public void setHoles(List<Integer> holes) {
        this.holes = holes;
    }

    public void pushHoles(Integer i) {
        this.holes.add(i);
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "TesselateResult{" +
                "vertices=" + vertices +
                ", holes=" + holes +
                ", dimensions=" + dimensions +
                '}';
    }
}
