package com.ospgames.goh.server.services.lobbyservice;

import java.util.*;

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

    public List<Position> createConnectedRandomPositions(int number, long seed) {

        List<Position> result = new ArrayList<Position>(number);
        Random r = new Random(seed);
        int maxChainLength = 10;

        result.add(getRandomPosition(r, mRadius / 4));

        for (int i=1; i<number; i++) {

            int remainingTries = 100;
            Position nextPosition = null;
            int currentChainLength = 0;
            do {
                if (remainingTries-- <=0) throw new IllegalStateException("To less space to place '"+number+"' positions");
                Position randomBase;
                if (currentChainLength <=0) {
                    randomBase = result.get(r.nextInt(result.size()));
                    currentChainLength = maxChainLength;
                }
                else {
                    randomBase = nextPosition;
                    currentChainLength--;
                }
                nextPosition = getRelativPosition(randomBase, r, result);
            }
            while (nextPosition == null);

            result.add(nextPosition);
        }

        return result;
    }

    public static double TWO_PI = Math.PI * 2.0d;

    private Position getRelativPosition(Position base, Random r, List<Position> existingPositions) {

        int remainingTries = 100;
        double dx, dy, dz;
        do {

            double distance = r.nextFloat()*(mMaxDist-mMinDist)+mMinDist;
            double zrad = r.nextDouble()*TWO_PI/2;
            double yrad = r.nextDouble()*TWO_PI/2;

            double tanZrad = Math.tan(zrad);
            double tanYrad = Math.tan(yrad);

            dx = Math.sqrt( distance*distance/(1+tanZrad*tanZrad+tanYrad*tanYrad));
            dy = dx * tanZrad;
            dz = dx * tanYrad;

            dx += base.x;
            dy += base.y;
            dz += base.z;

            if (isInRadius(dx, dy, dz) && hasNoCollision(dx, dy, dz, existingPositions)) {
                return new Position((float)dx, (float)dy, (float)dz);
            }

            remainingTries--;
        }
        while (remainingTries>0);

        return null;
    }

    private Position getRandomPosition(Random r, double radius) {

        double d = radius*2;
        double x = r.nextFloat()*d-radius;
        double y = r.nextFloat()*d-radius;
        double z = r.nextFloat()*d-radius;

        return new Position((float)x, (float)y, (float)z);
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
        }
        while (! isConnected(positions, mMaxDist));

        return positions;
    }

    protected boolean isConnected(List<Position> positions, double maxDist) {
        final int SINGLE_CLUSTER_ID = 1;
        // jede position gehört zu einem cluster von positionen die
        int[] clusters = new int[positions.size()];
        int clusterId = SINGLE_CLUSTER_ID;

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

        for (int id : clusters) {
            if (id != SINGLE_CLUSTER_ID) {
                return false;
            }
        }

        return true;
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
