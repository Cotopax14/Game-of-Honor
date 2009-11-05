package com.ospgames.goh.server.framework.utils;

/**
 * Provides math methods missing from java.
 */
public class MathHelpers {

    /**
     * Calculate numbers of entries to match the given percent values and distributing the given number
     * across the fields of the array.
     *
     * @param percents array of percent between 0..1 incl.
     * @param points the sum to distribute.
     * @return  an array of int of same length as the input percent array that contains values matching the
     *         given percent values by distributing the given scale.
     *
     */
    public static int[] percentToAbsoluteDistribution(float[] percents, int points) {

        int[] result = new int[percents.length];
        float[] rest = new float[percents.length];

        int current = 0;
        for (int i =0; i<percents.length; i++) {
            float exact = percents[i] * points;
            int rounded =  Math.round(exact);
            rest[i] = exact - rounded;
            result[i] = rounded;
            current += rounded;
        }

        int delta = points - current;
        while ( delta != 0) {

            int pos = 0;
            float value=0f;

            for (int i=0; i< rest.length;i++) {

                if (delta > 0) {
                    if (value < rest[i]) {
                        value = rest[i];
                        pos = i;
                    }
                } else {
                    // delta < 0
                    if (value > rest[i]) {
                        value = rest[i];
                        pos = i;
                    }
                }
            }

            int inc;

            if (delta > 0)
                inc = 1;
            else
                inc = -1;

            rest[pos]   -= inc;
            delta       -= inc;
            result[pos] += inc;
        }

        return result;
    }

    

}
