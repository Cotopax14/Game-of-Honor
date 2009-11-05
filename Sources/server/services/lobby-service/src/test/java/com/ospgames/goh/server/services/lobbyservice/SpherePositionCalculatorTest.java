package com.ospgames.goh.server.services.lobbyservice;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test the position calculator.
 */
public class SpherePositionCalculatorTest {

    private static final float RADIUS = 100.0f;
    private static final float MIN_DIST = 2.0f;
    private static final float MAX_DIST = 7.5f;

    private SpherePositionCalculator mCalculator;
    private List<Position> mPositions;

    private static final List<Position> ISOLATED_POSITIONS = new ArrayList<Position>(
            Arrays.asList(
                    new Position(0, 0, 30),
                    new Position(MAX_DIST + 1, 0, 0),
                    new Position(0, 0, 0)
            ));

    private static final List<Position> CONNECTED_POSITIONS = new ArrayList<Position>(
            Arrays.asList(
                    new Position(0, 0, MAX_DIST),
                    new Position(0 , MAX_DIST, MAX_DIST),
                    new Position(0, 0, 0)
            ));

    private static final List<Position> TWO_CLUSTER_POSITIONS = new ArrayList<Position>(
            Arrays.asList(
                    new Position(0, -2.0f * MAX_DIST, MAX_DIST),
                    new Position(0, -MAX_DIST, MAX_DIST),
                    new Position(0 , MAX_DIST, 0),
                    new Position(0, 2.0f*MAX_DIST, 0)
            ));


    @Before
    public void setUp() throws Exception {
        mCalculator = new SpherePositionCalculator(RADIUS, MIN_DIST, MAX_DIST);
        mPositions = new ArrayList<Position>(
                Arrays.asList(
                        new Position(10, 10, 10),
                        new Position(0, 0, 0),
                        new Position(0, 0, 30)));
    }


    @Test
    public void positionsWithinRadiusAreDetectedAsIn() {

        assertTrue(mCalculator.isInRadius(0.0, 0.0, 0.0));
        assertTrue(mCalculator.isInRadius(99.9, 0.0, 0.0));
        assertTrue(mCalculator.isInRadius(0.0, 99.9, 0.0));
        assertTrue(mCalculator.isInRadius(0.0, 0.0, 99.9));
        assertTrue(mCalculator.isInRadius(0.0, 0.0, -99.9));
        assertTrue(mCalculator.isInRadius(50.0, 50.0, 50.0));
    }

    @Test
    public void positionOnBorderAreDetectedAsIn() {

        assertTrue(mCalculator.isInRadius(RADIUS, 0, 0));
        assertTrue(mCalculator.isInRadius(0, RADIUS, 0));
        assertTrue(mCalculator.isInRadius(0, 0, RADIUS));
        assertTrue(mCalculator.isInRadius(0, 0, RADIUS * -1.0));
    }


    @Test
    public void positionsOutAreDetectedAsOut() {

        assertFalse(mCalculator.isInRadius(100.1d, 0, 0));
        assertFalse(mCalculator.isInRadius(0, 100.1d, 0));
        assertFalse(mCalculator.isInRadius(0, 0, 100.1d));
        assertFalse(mCalculator.isInRadius(0, 0, -100.1d));
        assertFalse(mCalculator.isInRadius(80.0d, 80.0d, 80.0d));
    }

    @Test
    public void collisionsAreNotDetectedIfNotPresent() {

        // find the collision expects false

        // exact match
        assertFalse(mCalculator.hasNoCollision(0, 0, 0, mPositions));
        assertFalse(mCalculator.hasNoCollision(10, 10, 10, mPositions));
        assertFalse(mCalculator.hasNoCollision(0, 0, 30, mPositions));

        // within border
        assertFalse(mCalculator.hasNoCollision(MIN_DIST / 2, MIN_DIST / 2, 0, mPositions));
    }

    @Test
    public void collisionAreDetectedIfPresent() {
        // there is no collision expects true

        // on the border
        assertTrue(mCalculator.hasNoCollision(-MIN_DIST, 0, 0, mPositions));
        assertTrue(mCalculator.hasNoCollision(0, MIN_DIST, 0, mPositions));
        assertTrue(mCalculator.hasNoCollision(0, 0, MIN_DIST, mPositions));

        // out of the border
        assertTrue(mCalculator.hasNoCollision(RADIUS, -RADIUS, RADIUS, mPositions));
    }

    @Test
    public void getPositionsResultsMatchCriteria() {

        int number = 150;
        List<Position> positions = mCalculator.getPositions(number, 1173);

        assertNotNull(positions);
        assertTrue(positions.size() == number);
        assertAllPositionsAreWithInSphere( positions);
        assertNoPositionsAreCloserThan(positions, MIN_DIST);
        assertEachPosHasAPosCloserThan(positions, MAX_DIST);
    }

    @Test
    public void getRandomPositionResultsMatchCriteria() {

        List<Position> positions = mCalculator.getRandomPositions(100);

        assertAllPositionsAreWithInSphere( positions);
        assertNoPositionsAreCloserThan(positions, MIN_DIST);
        assertEachPosHasAPosCloserThan(positions, MAX_DIST);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getPositionThrowsIllegalArgumentExceptionForSinglePosition() {

        mCalculator.getPositions(1, 1173);
    }

    @Test
    public void twoElementPositionReturnsValidPositions() {

        List<Position> positions = mCalculator.getPositions(2, 1173);
        assertAllPositionsAreWithInSphere( positions);
        assertNoPositionsAreCloserThan(positions, MIN_DIST);
        assertEachPosHasAPosCloserThan(positions, MAX_DIST);
    }


    @Test(timeout=2000, expected=IllegalStateException.class)
    public void toSmallSphereDoesNotCreateEndlessLoop() {
        SpherePositionCalculator calculator = new SpherePositionCalculator(5, 5, 10);
        calculator.getPositions(20, 1173);
    }

    private void assertAllPositionsAreWithInSphere(List<Position> positions) {
        for (Position p : positions) {
            assertTrue( mCalculator.isInRadius(p.x,  p.y, p.z));
        }
    }

    private void assertNoPositionsAreCloserThan(List<Position> positions, double minDist) {

        for (Position p : positions) {
            for (Position t : positions) {
                if (p != t) {
                    double d = p.distanceTo(t);
                    assertTrue(d >= minDist);
                }
            }
        }
    }

    @Test(expected=AssertionError.class)
    public void assertNoPositionAreCloser_assertsCloserPositions() {

        List<Position> invalidPos =  new ArrayList<Position>(
                Arrays.asList(
                        new Position(0, 0, 30),
                        new Position(MIN_DIST / 2, 0, 0),
                        new Position(0, 0, 0)
                ));

        assertNoPositionsAreCloserThan(invalidPos, MIN_DIST);
    }

    @Test
    public void assertNoPositionAreCloserThan_doesNotAssertForValidData() {
        List<Position> validPos =  new ArrayList<Position>(
                Arrays.asList(
                        new Position(0, 0, 30),
                        new Position(MIN_DIST , 0, 0),
                        new Position(0, 0, 0)
                ));


        assertNoPositionsAreCloserThan(validPos, MIN_DIST);

    }

    private void assertEachPosHasAPosCloserThan(List<Position> positions, double maxDist) {

        for (Position p : positions) {
            boolean isolated = true;
            for (Position t : positions) {
                if (t != p) {
                    double d = p.distanceTo(t);
                    if (d <=maxDist) {
                        isolated = false;
                        break;
                    }
                }
            }
            assertFalse(isolated);
        }
    }

    @Test (expected=AssertionError.class)
    public void assertEachPosHasAPosCloserThan_assertsIsolatedPositions() {

        assertEachPosHasAPosCloserThan(ISOLATED_POSITIONS, MAX_DIST);
    }

    @Test
    public void assertEachPosHasAPosCloserThan_doesNotAssertValidPositions() {


        assertEachPosHasAPosCloserThan(CONNECTED_POSITIONS, MAX_DIST);
    }

    @Test (expected=AssertionError.class)
    public void assertSingleCluster_assertsIsolatedPositions() {

        assertSingleCluster(ISOLATED_POSITIONS, MAX_DIST);
    }

    @Test
    public void assertSingleCluster_doesNotAssertConnectedPositions() {
        assertSingleCluster(CONNECTED_POSITIONS, MAX_DIST);
    }

    @Test (expected=AssertionError.class)
    public void assertSingleCluster_assertTwoClusterPositions() {
        assertSingleCluster(TWO_CLUSTER_POSITIONS, MAX_DIST);
    }

    @Test (timeout=120000)
    public void getConnectedRandomPositions_returnsConnectedGraph() {
        assertSingleCluster(mCalculator.getConnectedRandomPositions(100), MAX_DIST);
    }

    private void assertSingleCluster(List<Position> positions, double maxDist) {

        assertTrue(mCalculator.isConnected(positions, maxDist));
    }

}



        
