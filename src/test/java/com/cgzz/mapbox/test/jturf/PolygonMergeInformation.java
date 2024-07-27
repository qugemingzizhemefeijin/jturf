package com.cgzz.mapbox.test.jturf;

import com.cgzz.mapbox.jturf.shape.Polygon;

import java.util.List;

/**
 * 围栏合并后信息
 */
public class PolygonMergeInformation {

    /**
     * 合并后的多边形
     */
    private Polygon unionPolygon;

    /**
     * 合并后的多边形面积，平方米
     */
    private double unionArea;

    /**
     * 所有围栏的总面积，平方米
     */
    private double totalArea;

    /**
     * 交集面积，平方米
     */
    private double interArea;

    /**
     * 空白面积，平方米
     */
    private double blankArea;

    /**
     * 被合并的围栏ID，读取围栏的properties中的id值，如果没有则为空
     */
    private List<String> mergeIdList;

    /**
     * 剩余未被合并的围栏ID，，读取围栏的properties中的id值，如果没有则为空
     */
    private List<String> remainingIdList;

    /**
     * 第一个超过空白区域的围栏ID，读取围栏的properties中的id值，如果没有则为空
     */
    private String fstMoreThanBlankAreaId;

    public PolygonMergeInformation() {

    }

    public PolygonMergeInformation(Polygon unionPolygon, List<String> remainingIdList) {
        this.unionPolygon = unionPolygon;
        this.remainingIdList = remainingIdList;
    }

    public Polygon getUnionPolygon() {
        return unionPolygon;
    }

    public void setUnionPolygon(Polygon unionPolygon) {
        this.unionPolygon = unionPolygon;
    }

    public double getUnionArea() {
        return unionArea;
    }

    public void setUnionArea(double unionArea) {
        this.unionArea = unionArea;
    }

    public double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(double totalArea) {
        this.totalArea = totalArea;
    }

    public double getInterArea() {
        return interArea;
    }

    public void setInterArea(double interArea) {
        this.interArea = interArea;
    }

    public double getBlankArea() {
        return blankArea;
    }

    public void setBlankArea(double blankArea) {
        this.blankArea = blankArea;
    }

    public List<String> getMergeIdList() {
        return mergeIdList;
    }

    public void setMergeIdList(List<String> mergeIdList) {
        this.mergeIdList = mergeIdList;
    }

    public List<String> getRemainingIdList() {
        return remainingIdList;
    }

    public void setRemainingIdList(List<String> remainingIdList) {
        this.remainingIdList = remainingIdList;
    }

    public String getFstMoreThanBlankAreaId() {
        return fstMoreThanBlankAreaId;
    }

    public void setFstMoreThanBlankAreaId(String fstMoreThanBlankAreaId) {
        this.fstMoreThanBlankAreaId = fstMoreThanBlankAreaId;
    }
}
