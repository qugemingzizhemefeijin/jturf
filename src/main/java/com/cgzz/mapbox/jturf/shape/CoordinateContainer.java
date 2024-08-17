package com.cgzz.mapbox.jturf.shape;

public interface CoordinateContainer<T> extends Geometry {

    /**
     * 定义几何图形的坐标。通常是一个点列表，但对于某些几何图形（如多边形），这可以是点列表的列表，因此此处的返回值是通用的。
     *
     * @return 组成定义几何图形的坐标
     */
    T coordinates();

}
