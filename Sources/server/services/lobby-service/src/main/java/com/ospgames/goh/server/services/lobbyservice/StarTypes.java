package com.ospgames.goh.server.services.lobbyservice;

import com.ospgames.goh.space.StarType;

/**
 * The startypes of the universe
 */
public class StarTypes {

    public static final StarType O = new StarType("O", 1800, 70f, 35f);
    public static final StarType B = new StarType("B", 2000, 65f, 25f );
    public static final StarType A = new StarType("A", 1500, 42f, 18f );
    public static final StarType F = new StarType("F", 1300, 25f, 12f );
    public static final StarType G = new StarType("G", 1700, 20f, 8f  );
    public static final StarType K = new StarType("K", 1200, 15f, 6f  );
    public static final StarType M = new StarType("M", 1000, 12f, 5f  );
    
    
   public static final StarType[] STAR_TYPES = new StarType[] {
            O, B, A, F, G, K, M

    };
}
