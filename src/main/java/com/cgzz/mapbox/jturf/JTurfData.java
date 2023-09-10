package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.util.JTurfHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JTurfData {

    private JTurfData() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 从点集合中随机返回指定数量的点
     *
     * @param pointList 要获取随机点的点集合
     * @param num       要获取的点数量
     * @return 返回随机点集合
     */
    public static List<Point> sample(List<Point> pointList, int num) {
        if (pointList == null || pointList.isEmpty()) {
            throw new JTurfException("pointList is required");
        }
        int size = pointList.size();
        if (num == 0) {
            return JTurfHelper.deepCloneList(pointList);
        } else if (num > 0) {
            if (num >= size) {
                return new ArrayList<>(0);
            } else {
                return getRandomSubarray(pointList, num);
            }
        } else {
            return getRandomSubarray(pointList, Math.abs(num));
        }
    }

    private static List<Point> getRandomSubarray(List<Point> pointList, int size) {
        Point[] shuffled = JTurfHelper.deepCloneArray(pointList);
        int i = pointList.size(), min = i - size;
        while (i-- > min) {
            int index = (int) (Math.floor((i + 1) * Math.random()));
            Point temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }

        if (size == shuffled.length) {
            return Arrays.asList(shuffled);
        } else {
            List<Point> list = new ArrayList<>(size);
            list.addAll(Arrays.asList(shuffled).subList(0, size));

            return list;
        }
    }

}
