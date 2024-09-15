package com.cgzz.mapbox.jturf.util.clipping.polygonclipping;

import java.util.ArrayList;
import java.util.List;

public class SegmentState {

    List<RingIn> rings;
    List<Integer> windings;
    List<MultiPolyIn> multiPolys;

    public SegmentState(List<RingIn> rings, List<Integer> windings, List<MultiPolyIn> multiPolys) {
        this.rings = rings != null ? new ArrayList<>(rings) : new ArrayList<>();
        this.windings = windings != null ? new ArrayList<>(windings) : new ArrayList<>();
        this.multiPolys = multiPolys != null ? new ArrayList<>(multiPolys) : new ArrayList<>();
    }

}
