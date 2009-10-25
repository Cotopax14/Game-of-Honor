package com.ospgames.goh.server.application;

import com.ospgames.goh.server.application.impl.ConsoleApplication;
import com.ospgames.goh.server.framework.ice.impl.IceAdapter;
import com.ospgames.goh.server.services.namingservice.NamingServiceImpl;
import com.ospgames.goh.server.services.lobbyservice.LobbyServiceImpl;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.services.NamingService;
import com.ospgames.goh.services.NameAlreadyInUseException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import Ice.Communicator;
import Ice.InitializationData;

/**
 * Game of honor server application.
 */
public class GohServer extends Ice.Application {

    private static final Log sLog = LogFactory.getLog(GohServer.class);

    public int run(String[] strings) {

        // Set Shutdown mode to Terminate cleanly on receipt of a signal
        shutdownOnInterrupt();

        // Create an object adapter (stored in the _adapter
        // static members)
        //
        // Ice.ObjectAdapter adapter = communicator().createObjectAdapterWithEndpoints("ObjectAdapter","default -p 10000");
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("ObjectAdapter");
        NamingServiceImpl namingService = new NamingServiceImpl();
        namingService.activate(adapter);

        LobbyServiceImpl lobbyService = new LobbyServiceImpl();
        String lobbyName = "Lobby";
        try {
            namingService.registerObject("Lobby", lobbyService.activate(adapter));
        } catch (NameAlreadyInUseException e) {
            throw new IllegalStateException("Name '"+lobbyName+"' already in use - is another server running?");
        }

        // All objects are created, allow client requests now
        adapter.activate();

        // Wait until we are done
        communicator().waitForShutdown();
        
        return 0;
    }


    public static final void main(String[] args) {
        GohServer server = new GohServer();
        System.exit(server.main("GohServer",args));
    }
}
