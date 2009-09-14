package com.ospgames.goh.clientjava;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import com.ospgames.goh.services.NamingServicePrx;
import com.ospgames.goh.services.NamingServicePrxHelper;
import com.ospgames.goh.game.LobbyServicePrx;
import com.ospgames.goh.game.LobbyServicePrxHelper;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.clientjava.GohApiObjectFactory;
import Ice.Communicator;
import Ice.ObjectPrx;

/**
 * Example to test ice access to Naming- and LobbyService
 */
public class IceClient extends Ice.Application {

    private static final Log sLog = LogFactory.getLog(IceClient.class);
    private static final GohApiObjectFactory sObjectFactory = new GohApiObjectFactory();

    class ShutdownHook extends Thread
    {
        public void
        run()
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

    public static final void main(String[] args) {
        IceClient client = new IceClient();
        System.exit(client.main("GohIceClient",args));
    }
}
