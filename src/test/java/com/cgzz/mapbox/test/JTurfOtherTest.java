package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfOther;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JTurfOtherTest {

    @Test
    public void booleanValidTest() {
        LineString line1 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");
        LineString line2 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,1]]}");
        line2.coordinates(null);

        assertTrue(JTurfOther.booleanValid(line1));
        assertFalse(JTurfOther.booleanValid(line2));
    }

}
