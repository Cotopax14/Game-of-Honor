package com.ospgames.goh.server.services.lobbyservice;

import Ice.Current;
import com.ospgames.goh.game.LobbyServicePrx;
import com.ospgames.goh.game.LobbyServicePrxHelper;
import com.ospgames.goh.game._LobbyServiceDisp;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.space.StarCluster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Implementation of the Lobby Service
 */
public class LobbyServiceImpl extends _LobbyServiceDisp {

    public static final Log sLog = LogFactory.getLog(LobbyServiceImpl.class);

    private StarCluster mCluster;

    public LobbyServiceImpl() {
        WorldGenerator generator = new WorldGenerator();
        mCluster = generator.getCluster();
    }


    public List<Star> getStars(Current __current) {
        sLog.trace(mCluster.stars);
        return mCluster.stars;
    }

    public StarCluster getCluster(Current __current) {
        sLog.trace(mCluster);
        return mCluster;
    }

    // Stuff only required for new ice activation procedure

    public LobbyServicePrx activate(Ice.ObjectAdapter a) {
         return LobbyServicePrxHelper.uncheckedCast(a.addWithUUID(this));
    }

    
}
