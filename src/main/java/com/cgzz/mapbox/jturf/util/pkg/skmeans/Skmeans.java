package com.cgzz.mapbox.jturf.util.pkg.skmeans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Skmeans {

    private static final int MAX = 10000;

    private Skmeans() {
        throw new AssertionError("No Instance.");
    }

    /**
     * Inits an array with values
     *
     * @param len 初始化数据长度
     * @param val 初始化的值
     * @param v   如果传入的数组不为空，则初始化此数值指定长度的数值
     * @return 数组
     */
    private static double[] init(int len, int val, double[] v) {
        v = v == null ? new double[len] : v;
        for (int i = 0; i < len; i++) {
            v[i] = val;
        }
        return v;
    }

    public static SkmeansRes skmeans(List<double[]> data, int k, List<double[]> initial, String type, Integer maxit) {
        List<double[]> ks = new ArrayList<>();
        boolean conv = false;
        int it = maxit == null ? MAX : maxit;
        int len = data.size(), vlen = data.get(0).length;
        double[] count = new double[k];
        int[] idxs = new int[len];

        if (initial == null) {
            Set<Integer> ix = new HashSet<>();
            while (ks.size() < k) {
                int idx = (int) Math.floor(Math.random() * len);
                if (ix.add(idx)) {
                    ks.add(data.get(idx));
                }
            }
        } else if ("kmrand".equals(type)) {
            ks = SkmeansKinit.kmrand(data, k);
        } else if ("kmpp".equals(type)) {
            ks = SkmeansKinit.kmpp(data, k);
        } else {
            ks = initial;
        }

        do {
            // Reset k count
            init(k, 0, count);

            // For each value in data, find the nearest centroid
            for (int i = 0; i < len; i++) {
                double min = Double.POSITIVE_INFINITY;
                int idx = 0;
                for (int j = 0; j < k; j++) {
                    // Multidimensional or unidimensional
                    double dist = SkmeansDistance.eudist(data.get(i), ks.get(j), false);
                    if (dist <= min) {
                        min = dist;
                        idx = j;
                    }
                }
                idxs[i] = idx;    // Index of the selected centroid for that value
                count[idx]++;        // Number of values for this centroid
            }

            // Recalculate centroids
            double[][] sum = new double[k][vlen];
            double[][] old = new double[k][vlen];
            for (int j = 0; j < k; j++) {
                // Multidimensional or unidimensional
                sum[j] = init(2, 0, sum[j]);
                old[j] = ks.get(j);
            }

            // If multidimensional
            for (int j = 0; j < k; j++) {
                ks.set(j, new double[vlen]);
            }
            // Sum values and count for each centroid
            for (int i = 0; i < len; i++) {
                int idx = idxs[i];            // Centroid for that item
                double[] vsum = sum[idx];        // Sum values for this centroid
                double[] vect = data.get(i);   // Current vector

                // Accumulate value on the centroid for current vector
                for (int h = 0; h < vlen; h++) {
                    vsum[h] += vect[h];
                }
            }

            // Calculate the average for each centroid
            conv = true;
            for (int j = 0; j < k; j++) {
                double[] ksj = ks.get(j);        // Current centroid
                double[] sumj = sum[j];        // Accumulated centroid values
                double[] oldj = old[j];        // Old centroid value
                double cj = count[j];            // Number of elements for this centroid

                // New average
                for (int h = 0; h < vlen; h++) {
                    ksj[h] = cj == 0 ? 0 : (sumj[h] / cj);    // New centroid
                }

                // Find if centroids have moved
                if (conv) {
                    for (int h = 0; h < vlen; h++) {
                        if (oldj[h] != ksj[h]) {
                            conv = false;
                            break;
                        }
                    }
                }
            }

            conv = conv || (--it <= 0);
        } while (!conv);

        SkmeansRes res = new SkmeansRes();
        res.setIt(MAX - it);
        res.setK(k);
        res.setIdxs(idxs);
        res.setCentroids(ks);

        return res;
    }

}
