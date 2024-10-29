package com.cgzz.mapbox.jturf.util.pkg.skmeans;

import java.util.List;

public final class SkmeansRes {

    private int it;

    private int k;

    private int[] idxs;

    private List<double[]> centroids;

    public int getIt() {
        return it;
    }

    public void setIt(int it) {
        this.it = it;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int[] getIdxs() {
        return idxs;
    }

    public void setIdxs(int[] idxs) {
        this.idxs = idxs;
    }

    public List<double[]> getCentroids() {
        return centroids;
    }

    public void setCentroids(List<double[]> centroids) {
        this.centroids = centroids;
    }
}
