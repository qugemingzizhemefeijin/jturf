package com.cgzz.mapbox.jturf.util.pkg.skmeans;

import com.cgzz.mapbox.jturf.exception.JTurfException;

import java.util.*;

public final class SkmeansKinit {

    public static List<double[]> kmrand(List<double[]> data, int k) {
        Set<String> map = new HashSet<>();
        List<double[]> ks = new ArrayList<>();
        int t = k << 2;
        int len = data.size();
        boolean multi = data.get(0).length > 0;

        while (ks.size() < k && (t--) > 0) {
            double[] d = data.get((int) Math.floor(Math.random() * len));
            String key = multi ? join(d) : Arrays.toString(d);
            if (map.add(key)) {
                ks.add(d);
            }
        }

        if (ks.size() < k) {
            throw new JTurfException("Error initializating clusters");
        } else {
            return ks;
        }
    }

    public static String join(double[] d) {
        StringBuilder buf = new StringBuilder(32);
        for (int i = 0, len = d.length; i < len; i++) {
            if (i > 0) {
                buf.append("_");
            }
            buf.append(d);
        }

        return buf.toString();
    }

    // K-means++ initial centroid selection
    public static List<double[]> kmpp(List<double[]> data, int k) {
        // var distance = data[0].length? eudist : dist;
        List<double[]> ks = new ArrayList<>();
        int len = data.size();
        boolean multi = data.get(0).length > 0;
        Set<String> map = new HashSet<>();

        // First random centroid
        double[] c = data.get((int)Math.floor(Math.random() * len));
        String key = multi ? join(c) : Arrays.toString(c);
        ks.add(c);
        map.add(key);

        // Retrieve next centroids
        int lk = 0;
        while ((lk = ks.size()) < k) {
            // Min Distances between current centroids and data points
            double[] dists = new double[len];
            double dsum = 0;
            SkmeansPrs[] prs = new SkmeansPrs[len];

            for (int i = 0; i < len; i++) {
                double min = Double.POSITIVE_INFINITY;
                for (int j = 0; j < lk; j++) {
                    double dist = SkmeansDistance.eudist(data.get(i), ks.get(j), false);
                    if (dist <= min) {
                        min = dist;
                    }
                }
                dists[i] = min;
            }

            // Sum all min distances
            for (int i = 0; i < len; i++) {
                dsum += dists[i];
            }

            // Probabilities and cummulative prob (cumsum)
            for (int i = 0; i < len; i++) {
                prs[i] = new SkmeansPrs(i, data.get(i), dists[i] / dsum, 0);
            }

            // Sort Probabilities
            Arrays.sort(prs, Comparator.comparingDouble(SkmeansPrs::getPr));

            // Cummulative Probabilities
            prs[0].setCs(prs[0].getPr());
            for (int i = 1; i < len; i++) {
                prs[i].setCs(prs[i - 1].getCs() + prs[i].getPr());
            }

            // Randomize
            double rnd = Math.random();

            // Gets only the items whose cumsum >= rnd
            int idx = 0;
            while (idx < len - 1 && prs[idx++].getCs() < rnd) {
                ks.add(prs[idx-1].getV());
            }

//			boolean done = false;
//			while(!done) {
//				// this is our new centroid
//				c = prs[idx-1].getV();
//				key = c.getX() + "_" + c.getY();
//				if(map.add(key)) {
//					ks.add(c);
//					done = true;
//				}
//				else {
//					idx++;
//				}
//			}
        }

        return ks;
    }

}
