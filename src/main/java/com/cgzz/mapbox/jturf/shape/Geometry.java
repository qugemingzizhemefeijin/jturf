package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;

public interface Geometry {

    GeometryType type();

    /**
     * 坐标点的数量，仅适合Point、Line、Polygon、MultiPoint
     *
     * @return int
     */
    default int coordsSize() {
        throw new JTurfException("geometry not support coordsSize");
    }

    /**
     * 输出被读取的点阵字符
     *
     * @return String
     */
    String toViewCoordsString();

}
