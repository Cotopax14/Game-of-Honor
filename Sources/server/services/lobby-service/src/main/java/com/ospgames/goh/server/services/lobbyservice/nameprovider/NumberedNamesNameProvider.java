package com.ospgames.goh.server.services.lobbyservice.nameprovider;

import com.ospgames.goh.server.services.lobbyservice.INameProvider;

import java.util.Iterator;

/**
 * Uses a given set of names as base to create more names by adding numbers to them.
 * Creates up to (baseNames elements number * Integer.MAX_VALUES) names. 
 */
public class NumberedNamesNameProvider implements INameProvider {

    private static final int START_ID = 1;
    private volatile int mNumber;
    private final Iterable<String> mBaseNames ;

    public NumberedNamesNameProvider(Iterable<String> baseNames) {

        mBaseNames = baseNames;
        mNumber = START_ID;
    }

    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private Iterator<String> iter=mBaseNames.iterator();

            public boolean hasNext() {
                return mNumber < Integer.MAX_VALUE;
            }

            public String next() {

                if (!iter.hasNext()) {
                    mNumber++;
                    iter = mBaseNames.iterator();
                }

                String baseName = iter.next();
                return mNumber == START_ID ? baseName : baseName+" "+mNumber;
            }

            public void remove() {
                throw new UnsupportedOperationException("Unmodifiable");
            }
        };
    }

    
}
