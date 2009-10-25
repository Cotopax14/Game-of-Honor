package com.ospgames.goh.clientjava;

import Glacier2.RouterPrx;
import Glacier2.SessionPrx;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import com.ospgames.goh.services.NamingServicePrx;
import com.ospgames.goh.services.NamingServicePrxHelper;
import com.ospgames.goh.game.LobbyServicePrx;
import com.ospgames.goh.game.LobbyServicePrxHelper;
import com.ospgames.goh.space.Star;
import Ice.Communicator;
import Ice.ObjectPrx;

import java.io.IOException;

/**
 * Example to test ice access to Naming- and LobbyService
 */
public class IceClient extends Ice.Application {

    private static final Log sLog = LogFactory.getLog(IceClient.class);
    private static final GohApiObjectFactory sObjectFactory = new GohApiObjectFactory();

    class ShutdownHook extends Thread
    {
        public void run()
        {
            try
            {
                Ice.Communicator ic = communicator();
                if (ic != null) {
                    ic.destroy();
                }
            }
            catch(Ice.LocalException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public int run(String[] strings) {

        //
        // Since this is an interactive demo we want to clear the
        // Application installed interrupt callback and install our
        // own shutdown hook.
        //
        setInterruptHook(new ShutdownHook());

        Communicator ic = communicator();

        // Register Mappings for generated and directly usable classes.
        sObjectFactory.registerMappings(ic);

        try {
            initializeRouterIfPresent(ic);
        }
        catch(Glacier2.PermissionDeniedException ex) {
                sLog.fatal("Permission denied cause ", ex);
                return -1;
        }
        // TODO handle session timeout and recreation
        // TODO handle callback recreation after session timeout


        if (ic == null) throw new IllegalStateException("No ice communicator");

        // Create a proxy for the naming service
        Ice.ObjectPrx prx = ic.propertyToProxy("NamingService.Proxy");

        if (prx == null) throw new RuntimeException("Cannot create proxy");

        // Down-cast the proxy to a NamingService proxy
        NamingServicePrx namingServicePrx =
                NamingServicePrxHelper.checkedCast(prx);

        if (namingServicePrx == null)
            throw new RuntimeException("Invalid naming service proxy");

        // do something with the naming service
        // ObjectPrx lobbyBase = ic.propertyToProxy("LobbyService.Proxy");
        ObjectPrx lobbyBase = namingServicePrx.findByName("Lobby");

        if (lobbyBase == null) throw new IllegalStateException("Lobby unknown");

        LobbyServicePrx lobbyServicePrx = LobbyServicePrxHelper.checkedCast(lobbyBase);

        if (lobbyServicePrx == null)
            throw new RuntimeException("Invalid lobby service proxy");

        System.out.println("Stars: ");
        for (Star star : lobbyServicePrx.getStars()) {
            System.out.println("Name: "+star.name+
                    " Position: ["+star.position.x+","+star.position.y+","+star.position.z+"]"+
                    " Type: "+star.type.name);
        }

        return 0;
    }

    private SessionPrx initializeRouterIfPresent(Communicator ic)
            throws Glacier2.PermissionDeniedException {

        Ice.RouterPrx defaultRouter = ic.getDefaultRouter();

        if (defaultRouter != null) {
            RouterPrx router = Glacier2.RouterPrxHelper.checkedCast(defaultRouter);

            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

            try {
                String id;
                System.out.print("user id: ");
                System.out.flush();
                id = in.readLine();

                String pw;
                System.out.print("password: ");
                System.out.flush();
                pw = in.readLine();
                return router.createSession(id, pw);
            }

            catch(Glacier2.CannotCreateSessionException ex) {
                // TODO use emergency here 
                throw new RuntimeException("Could not create session" , ex);
            }
            catch(IOException ex) {
                // TODO use emergency here
                sLog.fatal("Could not read user id or password");
                throw new RuntimeException(ex);
            }

        }
        return null;
    }
    public static final void main(String[] args) {
        IceClient client = new IceClient();
        System.exit(client.main("GohIceClient",args));
    }
}
