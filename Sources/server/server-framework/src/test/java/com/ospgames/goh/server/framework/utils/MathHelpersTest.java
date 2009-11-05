package com.ospgames.goh.server.framework.utils;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

/**
 * Test the math methods.
 */
public class MathHelpersTest {

    @Test
    public void percentToAbsoluteDistributionExactMatches() {
        float[] in = new float[] {
                0.1f,
                0.2f,
                0.4f,
                0.2f,
                0.05f,
                0.05f
        };

        int[] expected = new int[] {
                10, 20, 40, 20, 5, 5
        };

        assertArrayEquals(expected, MathHelpers.percentToAbsoluteDistribution(in, 100));
    }

    @Test
    public void percentToAbsoluteDistributionToLessElementsMatches() {
        float[] in = new float[] {
                0.104f,
                0.104f,
                0.792f
        };

        int[] expected = new int[] {
                11, 10, 79
        };

        assertArrayEquals(expected, MathHelpers.percentToAbsoluteDistribution(in, 100));
    }

    @Test
    public void percentToAbsoluteDistributionToMuchElementsMatches() {
        float[] in = new float[] {
                0.106f,
                0.106f,
                0.788f
        };

        int[] expected = new int[] {
                10, 11, 79
        };

        assertArrayEquals(expected, MathHelpers.percentToAbsoluteDistribution(in, 100));
    }

}
