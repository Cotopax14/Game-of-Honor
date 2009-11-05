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

    private List<Star> mStars;

    public LobbyServiceImpl() {
        mStars = new WorldGenerator().getStars();    
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
