package com.ospgames.goh.server.services.lobbyservice;

import com.ospgames.goh.server.services.lobbyservice.nameprovider.FileBasedNameProvider;
import com.ospgames.goh.server.services.lobbyservice.nameprovider.NumberedNamesNameProvider;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.generic.Vector3D;
import com.ospgames.goh.space.StarCluster;
import com.ospgames.goh.space.StarType;
import com.ospgames.goh.space.Wormhole;
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

    private int mNumberOfStars = 100;
    private long mScale = 1;
    private float mMinDist = 2.0f;
    private float mMaxDist = 10.0f;
    private float mRadius = 100.0f;

    private int mNumberOfWormwholes = 10;


    public WorldGenerator() {

        // initialize names
        try {
            mNames = new NumberedNamesNameProvider(new FileBasedNameProvider(STARNAMES_FILE));
        } catch (FileNotFoundException e) {
            sLog.error(e);
            mNames = new NumberedNamesNameProvider(Arrays.asList("Star"));
        }

        // initialize star types
        mStarTypes = new StarTypeProvider<StarType>(StarTypes.STAR_TYPES, STAR_TYPE_DISTRIBUTION, mNumberOfStars);
    }


    /**
     * Create a random generated map of stars according to the parameters
     * defined on this WorldGenerator.
     * @return list of stars matching the specified settings, not null.
     */
    public List<Star> getStars() {

        SpherePositionCalculator posGen = new SpherePositionCalculator(mRadius, mMinDist, mMaxDist);
        List<Position> positions = posGen.createConnectedRandomPositions(mNumberOfStars,1234);//getRandomPositions(mNumberOfStars);
        posGen.isConnected(positions, mMaxDist);
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

    public StarCluster getCluster() {

        List<Star> stars = getStars();
        List<Wormhole> wormholes = getWormholes(stars);
        return new StarCluster(stars, wormholes);
    }

    private List<Wormhole> getWormholes(List<Star> stars) {

        Random r = new Random(140875);
        List<Wormhole> wormholes = new ArrayList<Wormhole>(mNumberOfWormwholes);
        Set<String> idSet = new HashSet<String>(mNumberOfWormwholes);

        for (int i=0; i<mNumberOfWormwholes; i++) {
            int idStart;
            int idEnd;

            String key;
            // don't generate same wormhole twice.
            do {
                // get two different stars
                do {

                    idStart = r.nextInt(mNumberOfStars);
                    idEnd   = r.nextInt(mNumberOfStars);
                }
                while (idStart == idEnd);

                // make sure idStart < idEnd
                if (idStart > idEnd) {
                    int t = idStart;
                    idStart = idEnd;
                    idEnd = t;
                }

                key = ""+idStart+"-"+idEnd;

            }
            while (idSet.contains(key));

            idSet.add(key);
            wormholes.add( new Wormhole(idStart, idEnd));
        }

        return wormholes;
    }


    private Vector3D getScaledVector3D(Position pos) {
        return new Vector3D(pos.x*mScale, pos.y*mScale, pos.z*mScale);
    }


    public static void main(String[] args) {

        WorldGenerator gen = new WorldGenerator();
        long start = System.currentTimeMillis();

        StarCluster cluster = gen.getCluster();

        List<Star> stars = cluster.stars;
        long end = System.currentTimeMillis();

        System.out.println("Time needed: "+(end-start)+" ms");
        for (Star s : stars) {
            System.out.println("X: "+(s.position.x)+" Y:  "+(s.position.y)+" Z: "+(s.position.z)+" Name: "+s.name+ " Type "+s.type.name);
        }

        for (Wormhole w : cluster.wormholes) {
            System.out.println("From: "+stars.get(w.starA).name+ " to: "+stars.get(w.starB).name );
        }
    }
}
