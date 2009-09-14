package com.ospgames.goh.server.services.lobbyservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ospgames.goh.game._LobbyServiceDisp;
import com.ospgames.goh.game.GameSessionPrx;
import com.ospgames.goh.game.LobbyServicePrxHelper;
import com.ospgames.goh.game.LobbyServicePrx;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.space.StarType;
import com.ospgames.goh.generic.Vector3D;
import com.ospgames.goh.services.NamingServicePrxHelper;
import Ice.Current;
import Ice.Identity;

import java.util.List;
import java.util.ArrayList;

/**
 * Implementation of the Lobby Service
 */
public class LobbyServiceImpl extends _LobbyServiceDisp {

    public static final Log sLog = LogFactory.getLog(LobbyServiceImpl.class);

    public static final long LM = 300000L*1000*60;
    public static final long LY = LM*60*24*365;

    public static final StarType G = new StarType("G",1000, 23L*LM, 12L*LM);
    public final List<Star> mStars = new ArrayList<Star>();

    public LobbyServiceImpl() {
        mStars.add(new Star(1,"Terra", new Vector3D(0,0,0),G));
        mStars.add(new Star(2,"Alpha Centauri", new Vector3D(42L*LY/10, 0, 0), G));
        mStars.add(new Star(3,"Beteigeuze", new Vector3D(0, 32L*LY, -4*LY ), G ));
    }


    public List<Star> getStars(Current __current) {
        sLog.trace(mStars);
        return mStars;
    }

    // Stuff only required for new ice activation procedure

    public LobbyServicePrx activate(Ice.ObjectAdapter a) {
         return LobbyServicePrxHelper.uncheckedCast(a.addWithUUID(this));
    }

    //public final Identity _id = Ice.Util.stringToIdentity("LobbyService");
}
