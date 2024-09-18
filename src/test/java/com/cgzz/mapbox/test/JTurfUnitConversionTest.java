package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfUnitConversion;
import com.cgzz.mapbox.jturf.enums.Units;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JTurfUnitConversionTest {

    @Test
    public void convertLengthTest() {
        double length = 10000;

        double convertLength = JTurfUnitConversion.convertLength(length, Units.KILOMETERS, Units.METERS);

        assertEquals(convertLength, 10000000, 13);
    }

}
