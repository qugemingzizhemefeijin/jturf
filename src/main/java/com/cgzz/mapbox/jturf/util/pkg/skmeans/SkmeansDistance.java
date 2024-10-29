package com.cgzz.mapbox.jturf.util.pkg.skmeans;

public final class SkmeansDistance {

    /**
     * Euclidean distance
     *
     * @param v1   数组
     * @param v2   数组
     * @param sqrt 是否需要求平方根
     * @return 返回计算之后的数值
     */
    public static double eudist(double[] v1, double[] v2, boolean sqrt) {
        int len = v1.length;
        double sum = 0;

        for (int i = 0; i < len; i++) {
            double d = v1[i] - v2[i];
            sum += d * d;
        }
        // Square root not really needed
        return sqrt ? Math.sqrt(sum) : sum;
    }

    /**
     * mandist distance
     *
     * @param v1   数组
     * @param v2   数组
     * @param sqrt 是否需要求平方根
     * @return 返回计算之后的数值
     */
    public static double mandist(double[] v1, double[] v2, boolean sqrt) {
        int len = v1.length;
        double sum = 0;

        for (int i = 0; i < len; i++) {
            sum += Math.abs(v1[i] - v2[i]);
        }

        // Square root not really needed
        return sqrt ? Math.sqrt(sum) : sum;
    }

    /**
     * Unidimensional distance
     *
     * @param v1   数值
     * @param v2   数值
     * @param sqrt 是否需要求平方根
     * @return 返回计算之后的数值
     */
    public static double dist(double v1, double v2, boolean sqrt) {
        double d = Math.abs(v1 - v2);
        return sqrt ? d : d * d;
    }

}
