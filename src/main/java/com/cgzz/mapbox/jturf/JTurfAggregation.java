package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.pkg.skmeans.Skmeans;
import com.cgzz.mapbox.jturf.util.pkg.skmeans.SkmeansRes;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JTurfAggregation {

    private JTurfAggregation() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 获取一组 {@link Point}，并使用 K均值算法聚类 将它们划分为多个群集。使用 [k-means 算法]（https://en.wikipedia.org/wiki/K-means_clustering）
     *
     * @param points 一组Point集合
     * @return 具有与每个 Feature 关联的另外两个属性的簇点：关联的 clusterId, 集群的质心 [经度、纬度]
     */
    public static FeatureCollection<Point> clustersKmeans(FeatureCollection<Point> points) {
        return clustersKmeans(points, null, false);
    }

    /**
     * 获取一组 {@link Point}，并使用 K均值算法聚类 将它们划分为多个群集。使用 [k-means 算法]（https://en.wikipedia.org/wiki/K-means_clustering）
     *
     * @param points           一组Point集合
     * @param numberOfClusters 指定累积概率
     * @return 具有与每个 Feature 关联的另外两个属性的簇点：关联的 clusterId, 集群的质心 [经度、纬度]
     */
    public static FeatureCollection<Point> clustersKmeans(FeatureCollection<Point> points, Integer numberOfClusters) {
        return clustersKmeans(points, numberOfClusters, false);
    }

    /**
     * 获取一组 {@link Point}，并使用 K均值算法聚类 将它们划分为多个群集。使用 [k-means 算法]（https://en.wikipedia.org/wiki/K-means_clustering）
     *
     * @param points           一组Point集合
     * @param numberOfClusters 指定累积概率
     * @param mutate           允许更改 GeoJSON 输入（如果为 true，则性能会显著提高）
     * @return 具有与每个 Feature 关联的另外两个属性的簇点：关联的 clusterId, 集群的质心 [经度、纬度]
     */
    public static FeatureCollection<Point> clustersKmeans(FeatureCollection<Point> points, Integer numberOfClusters, boolean mutate) {
        // Default Params
        int count = points.size();
        numberOfClusters = numberOfClusters != null ? numberOfClusters : (int) Math.round(Math.sqrt(count / 2D));

        // numberOfClusters can't be greater than the number of points
        // fallbacks to count
        if (numberOfClusters > count) {
            numberOfClusters = count;
        }

        // Clone points to prevent any mutations (enabled by default)
        if (mutate) {
            points = JTurfTransformation.clone(points);
        }

        // collect points coordinates
        List<double[]> data = JTurfMeta.coordAllToArray(points);

        // create seed to avoid skmeans to drift
        List<double[]> initialCentroids = data.subList(0, Math.min(data.size(), numberOfClusters));

        // create skmeans clusters
        SkmeansRes skmeansResult = Skmeans.skmeans(data, numberOfClusters, initialCentroids, null, null);

        // store centroids {clusterId: [number, number]}
        Map<String, double[]> centroids = new HashMap<>();
        List<double[]> cs = skmeansResult.getCentroids();
        for (int i = 0, size = cs.size(); i < size; i++) {
            centroids.put(String.valueOf(i), cs.get(i));
        }

        // add associated cluster number
        JTurfMeta.featureEach(points, (point, index) -> {
            String clusterId = String.valueOf(skmeansResult.getIdxs()[index]);
            point.addProperty("cluster", clusterId);

            JsonArray array = new JsonArray();
            for (double d : centroids.get(clusterId)) {
                array.add(d);
            }
            point.addProperty("centroid", array);

            return true;
        });

        return points;
    }

}
