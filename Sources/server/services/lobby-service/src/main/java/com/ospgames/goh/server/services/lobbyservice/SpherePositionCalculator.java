package com.ospgames.goh.server.services.lobbyservice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Get a list of positions.
 */
public class SpherePositionCalculator {

    private final double mMinDist;
    private final double mMaxDist;
    private final double mRadius;
    private long  mMaxTriesScale = 1000;


    /**
     * Creates a calculator that provides positions within a sphere
     * of the given radius with at least the given minimal distance
     * each position has at least one position whose distance is
     * not greater than the given max distance.
     *
     * @param radius   of the sphere to place the positions in, inclusive border.
     * @param minDist  minimal allowed distance between positions
     * @param maxDist  maximal allowed distance to next position <= radius
     */
    public SpherePositionCalculator(double radius, double minDist, double maxDist) {
        mRadius = radius;
        mMinDist = minDist;
        mMaxDist = maxDist;
    }


    /**
     * Creates a new list of number positions.
     * Can be used to create reproducible results.
     *
     * @param number of positions to be placed in the sphere, >=2 expected
     * @param seed of the random generator
     * @return list of positions matching the input parameter and world parameter
     * by this Calculator.
     */
    public List<Position> getPositions(int number, long seed) {
        if (number < 2) throw new IllegalArgumentException("number >= 2 expected but is "+number);
        Random r = new Random(seed);
        double d = mRadius * 2.0d;
        long max = number * mMaxTriesScale;

        Position lastNotIsolated = null;
        LinkedList<Position> positions = new LinkedList<Position>();
        do {
            // place random points that do not collide within sphere
            long tries = 0;
            do {

                double x = r.nextDouble()*d-mRadius;
                double y = r.nextDouble()*d-mRadius;
                double z = r.nextDouble()*d-mRadius;

                if (isInRadius( x, y, z) && hasNoCollision( x, y, z, positions)) {

                    positions.addLast( new Position((float)x, (float)y, (float)z));
                }

                // Give up if there is not enough space
                if (tries++ > max) {
                    throw new IllegalStateException("Not enough space to place "+
                            number+" positions after " +
                            tries+"tries");
                }
            }
            while (positions.size() < number);

            // drop isolated positions
            for (Iterator<Position> iterator = positions.descendingIterator(); iterator.hasNext();) {
                Position p = iterator.next();

                if (lastNotIsolated == p) {
                    break;
                }

                boolean isolated = true;
                for (Position t : positions) {
                    if (p != t && p.distanceTo(t) <= mMaxDist) {
                        isolated = false;
                        break;
                    }
                }
                if (isolated) iterator.remove();
            }
            lastNotIsolated = positions.size() > 0 ? positions.getLast() : null;
        }
        while (positions.size() < number);
        return positions;
    }


    /**
     * Shortcut that uses the current time to initialize the random generator.
     * And to return quasi random lists of positions.
     * @param number of positions to be placed.
     * @return list of positions.
     */
    public List<Position> getRandomPositions(int number) {

        return getPositions(number, System.currentTimeMillis());

    }

    public List<Position> getConnectedRandomPositions(int number) {

        List<Position> positions;
        int i=0;
        do {
            positions = getRandomPositions(number);
            System.out.println(i++);
        }
        while (! isConnected(positions, mMaxDist));

        return positions;
    }

    protected boolean isConnected(List<Position> positions, double maxDist) {
        // jede position gehört zu einem cluster von positionen die
        int[] clusters = new int[positions.size()];
        int clusterId = 1;

        for (int i=0; i<clusters.length; i++) {
            clusters[i] = Integer.MAX_VALUE;
        }

        int pos = 0;
        for (Position p : positions) {

            // initialisiere die cluster id
            if (clusters[pos] > clusterId) {
                clusters[pos] = clusterId;
                clusterId++;
            }

            // finde alle positionen die verbunden sind und setzte cluster ids
            // wenn p > als cluster eintrag ist setze den eigenen cluster eintrag auf den entsprechenden wert
            for (int i=pos+1; i<positions.size(); i++) {
                Position t = positions.get(i);

                double d = p.distanceTo(t);
                if (d <= maxDist) {

                    // schreibe kleinste cluster id
                    if (clusters[i]>clusters[pos]) {
                        clusters[i]=clusters[pos];
                    }
                    else {
                        clusters[pos]=clusters[i];
                    }
                }
            }
            pos++;
        }

        return clusters[positions.size()-1] == 1;
    }

    boolean isInRadius( double x, double y, double z) {

        double dist = Math.sqrt(x*x+y*y+z*z);
        return dist <= mRadius ;
    }

    boolean hasNoCollision(double x, double y, double z, List<Position> positions) {
        for (Position s : positions) {

            double dx = s.x-x;
            double dy = s.y-y;
            double dz = s.z-z;

            double dist = Math.sqrt(dx*dx+dy*dy+dz*dz);

            if (dist < mMinDist) {
                return false;
            }
        }
        return true;
    }

}
