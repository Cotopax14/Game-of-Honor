package com.ospgames.goh.server.services.lobbyservice;

import com.ospgames.goh.server.framework.utils.MathHelpers;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides a set of StarTypes following the given distribution.
 */
public class StarTypeProvider<T> implements Iterable<T>{

    private final     T[] mElements;
    private final float[] mRelDist;
    private           int mScale;

    public StarTypeProvider(T[] elements, float[] relDist, int scale) {

        if (elements == null || relDist == null || elements.length != relDist.length) {
            throw new IllegalArgumentException();
        }
        mElements = elements;
        mRelDist  = relDist;
        mScale    = scale;
    }

    public Iterator<T> iterator() {

        final int[] absDist = MathHelpers.percentToAbsoluteDistribution(mRelDist, mScale);

        return new Iterator<T>() {

            private int currentCount=0;
            private int currentPos =0;


            public boolean hasNext() {
                return currentPos < absDist.length;
            }

            public T next() {

                if (currentCount >= absDist[currentPos]) {
                    currentPos++;
                    if (!hasNext()) throw new NoSuchElementException();
                    currentCount = 0;
                }

                currentCount++;
                return mElements[currentPos];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
