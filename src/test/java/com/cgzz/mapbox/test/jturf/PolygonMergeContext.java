package com.cgzz.mapbox.test.jturf;

import com.cgzz.mapbox.jturf.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * 多边形合并的上线文变量传递类
 */
public class PolygonMergeContext {

    /**
     * 当前主围栏的面积，平方米
     */
    private final double mainArea;

    /**
     * 要求的最小切片面积，平方米
     */
    private final double minSliceArea;

    /**
     * 要求的最大空白面积，平方米
     */
    private final double maxBlankArea;

    /**
     * 是否需要第一个超过空白区域的多边形ID
     */
    private final boolean needFstMoreThanBlankId;

    /**
     * 交集围栏的面积，平方米
     */
    private double interArea = 0;

    /**
     * 被合并的多边形
     */
    private List<Polygon> mergePolygon = new ArrayList<>();

    /**
     * 未被合并的多边形
     */
    private List<Polygon> remainingPolygon = new ArrayList<>();

    /**
     * 第一个超过空白区域的围栏ID，读取围栏的properties中的id值，如果没有则为空
     */
    private String fstMoreThanBlankAreaId;

    public PolygonMergeContext(double mainArea, double minSliceArea, double maxBlankArea, boolean needFstMoreThanBlankId) {
        this.mainArea = mainArea;
        this.minSliceArea = minSliceArea;
        this.maxBlankArea = maxBlankArea;
        this.needFstMoreThanBlankId = needFstMoreThanBlankId;
    }

    public double getInterArea() {
        return interArea;
    }

    public void setInterArea(double interArea) {
        this.interArea = interArea;
    }

    public List<Polygon> getMergePolygon() {
        return mergePolygon;
    }

    public void setMergePolygon(List<Polygon> mergePolygon) {
        this.mergePolygon = mergePolygon;
    }

    public List<Polygon> getRemainingPolygon() {
        return remainingPolygon;
    }

    public void setRemainingPolygon(List<Polygon> remainingPolygon) {
        this.remainingPolygon = remainingPolygon;
    }

    public String getFstMoreThanBlankAreaId() {
        return fstMoreThanBlankAreaId;
    }

    public void setFstMoreThanBlankAreaId(String fstMoreThanBlankAreaId) {
        this.fstMoreThanBlankAreaId = fstMoreThanBlankAreaId;
    }

    public double getMainArea() {
        return mainArea;
    }

    public double getMinSliceArea() {
        return minSliceArea;
    }

    public double getMaxBlankArea() {
        return maxBlankArea;
    }

    public boolean isNeedFstMoreThanBlankId() {
        return needFstMoreThanBlankId;
    }
}
