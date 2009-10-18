package com.ospgames.goh.server.services.namingservice;

import Ice.ObjectPrx;
import Ice.Current;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ospgames.goh.services._NamingServiceEventHandlerDisp;

/**
 * This class defines the methods to
 *
 * @author Wilko Kempa
 * @version 1.0  10.04.2007
 */
public class LoggingNameServiceEventHandler extends _NamingServiceEventHandlerDisp {

    private static Log sLog = LogFactory.getLog(LoggingNameServiceEventHandler.class);

    public void handleRegister(String name, ObjectPrx obj, Current __current) {
        sLog.info("Register Object '"+name+"' for object '"+obj+"'");
    }

    public void handleUnregister(String name, ObjectPrx obj, Current __current) {
        sLog.info("Unregister Object '"+name+"' for object '"+obj+"'");
    }

    public void initialList(String[] names, ObjectPrx[] objects, Current __current) {
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            ObjectPrx prx = objects[i];
            sLog.info("Unregister Object '"+name+"' for object '"+prx+"'");
        }
    }
}
