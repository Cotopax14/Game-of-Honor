package com.ospgames.goh.server.services.lobbyservice;

import com.ospgames.goh.space.Star;
import com.ospgames.goh.space.StarType;
import com.ospgames.goh.generic.Vector3D;

import java.util.*;

/**
 * Create star map.
 */
public class WorldGenerator {

    public static final long LM = 300000L*1000*60;
    public static final long LY = LM*60*24*365;
    public static final StarType G = new StarType("G",1000, 23L*LM, 12L*LM);


    private int mNumberOfStars = 100;
    private long mScale = 3;
    private double mMinDist = 2.5d;
    private double mMaxDist = 6.5d;
    private double mRadius = 100.0d;


    private Vector3D getScaledVector3D(Position pos) {
        return new Vector3D(Math.round(pos.x*mScale), Math.round(pos.y*mScale), Math.round(pos.z*mScale));
    }

    /**
     * Create a random generated map of stars according to the parameters
     * defined on this WorldGenerator.
     * @return list of stars matching the specified settings, not null.
     */
    public List<Star> getStars() {

        SpherePositionCalculator posGen = new SpherePositionCalculator(mRadius, mMinDist, mMaxDist);
        List<Position> positions = posGen.getRandomPositions(mNumberOfStars);

        int i=0;
        List<Star> stars = new ArrayList<Star>(mNumberOfStars);
        for (Position p : positions) {
            stars.add(new Star(i++, "Star"+i, getScaledVector3D(p), G));
        }

        return stars;
    }



    

    public static void main(String[] args) {

        WorldGenerator gen = new WorldGenerator();
        long start = System.currentTimeMillis();

        List<Star> stars = gen.getStars();
        long end = System.currentTimeMillis();



        System.out.println("Time needed: "+(end-start)+" ms");
        for (Star s : stars) {
            System.out.println("X: "+(s.position.x/LY)+" Y:  "+(s.position.y/LY)+" Z: "+(s.position.z/LY));
        }
    }
}
