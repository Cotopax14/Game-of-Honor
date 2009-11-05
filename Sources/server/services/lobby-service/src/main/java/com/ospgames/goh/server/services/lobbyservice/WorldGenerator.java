package com.ospgames.goh.server.services.lobbyservice;

import com.ospgames.goh.server.framework.utils.MathHelpers;
import com.ospgames.goh.server.services.lobbyservice.nameprovider.FileBasedNameProvider;
import com.ospgames.goh.server.services.lobbyservice.nameprovider.NumberedNamesNameProvider;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.generic.Vector3D;
import com.ospgames.goh.space.StarType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Create star map.
 */
public class WorldGenerator {

    private static final Log sLog = LogFactory.getLog(WorldGenerator.class);
    private static final String STARNAMES_FILE = "starnames.txt";
    public static final float[] STAR_TYPE_DISTRIBUTION = new float[] {
                0.01f, // O
                0.09f, // B
                0.15f, // A
                0.25f, // F
                0.30f, // G
                0.18f, // K
                0.02f  // M
        };


    private Iterable<String> mNames;
    private Iterable<StarType> mStarTypes;

    private int mNumberOfStars = 200;
    private long mScale = 1;
    private float mMinDist = 2.5f;
    private float mMaxDist = 6.5f;
    private float mRadius = 100.0f;


    public WorldGenerator() {
        try {
            mNames = new NumberedNamesNameProvider(new FileBasedNameProvider(STARNAMES_FILE));
        } catch (FileNotFoundException e) {
            sLog.error(e);
            mNames = new NumberedNamesNameProvider(Arrays.asList("Star"));
        }

        // quick flat model iterator is a list containing one startype per startype to iterate over
        int[] numberOfStarsPerType = MathHelpers.percentToAbsoluteDistribution(STAR_TYPE_DISTRIBUTION, mNumberOfStars);
        List<StarType> starTypes = new ArrayList<StarType>(mNumberOfStars);
        for (int i = 0, numberOfStarsPerTypeLength = numberOfStarsPerType.length; i < numberOfStarsPerTypeLength; i++) {
            int n = numberOfStarsPerType[i];
            StarType type = StarTypes.STAR_TYPES[i];
            for (int s = 0; s<n; s++) {
                 starTypes.add(type);
            }
        }
        mStarTypes = Collections.unmodifiableList(starTypes);
    }


    /**
     * Create a random generated map of stars according to the parameters
     * defined on this WorldGenerator.
     * @return list of stars matching the specified settings, not null.
     */
    public List<Star> getStars() {

        SpherePositionCalculator posGen = new SpherePositionCalculator(mRadius, mMinDist, mMaxDist);
        List<Position> positions = posGen.getRandomPositions(mNumberOfStars);
        Iterator<String> names = mNames.iterator();
        Iterator<StarType> starTypes = mStarTypes.iterator();

        int i=0;
        List<Star> stars = new ArrayList<Star>(mNumberOfStars);
        for (Position p : positions) {

            StarType type = starTypes.next();
            String   name = names.next();
            stars.add(new Star(i++, name, getScaledVector3D(p), type));
        }

        return stars;
    }




    


    private Vector3D getScaledVector3D(Position pos) {
        return new Vector3D(pos.x*mScale, pos.y*mScale, pos.z*mScale);
    }


    public static void main(String[] args) {

        WorldGenerator gen = new WorldGenerator();
        long start = System.currentTimeMillis();

        List<Star> stars = gen.getStars();
        long end = System.currentTimeMillis();



        System.out.println("Time needed: "+(end-start)+" ms");
        for (Star s : stars) {
            System.out.println("X: "+(s.position.x)+" Y:  "+(s.position.y)+" Z: "+(s.position.z)+" Name: "+s.name+ " Type "+s.type.name);
        }
    }
}
