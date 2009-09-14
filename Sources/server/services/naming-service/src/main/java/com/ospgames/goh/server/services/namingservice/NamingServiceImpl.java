package com.ospgames.goh.server.services.namingservice;

import Ice.Current;
import Ice.ObjectPrx;
import Ice.Identity;
import com.ospgames.goh.services._NamingServiceDisp;
import com.ospgames.goh.services.NamingServiceEventHandlerPrx;
import com.ospgames.goh.services.NameAlreadyInUseException;
import com.ospgames.goh.services.NamingServicePrxHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a central lookup service for objects proxies and managed node proxies.
 * You should use ManagedNodeProxies to register the proxies for ManagedNodes,
 * this avoids checkedCasts and by this remote calls at use time.
 *
 * @author Wilko Kempa
 * @version 1.0     10.04.2007
 */
public class NamingServiceImpl extends _NamingServiceDisp {

    public static final Log sLog = LogFactory.getLog(NamingServiceImpl.class);



    private HashMap<String,ObjectPrx> mObjects = new HashMap<String, ObjectPrx>();

    private List<NamingServiceEventHandlerPrx> mEventHandler = new ArrayList<NamingServiceEventHandlerPrx>();


    /**
     * Registers the given proxy to the name.
     * If another proxy is registered a NameAlreadyInUseException is throwed.
     * Calls the handleRegister method of all registered NamingServiceEventHandler.
     * @param name       unique identification if the proxy
     * @param obj        the proxy to be bound to obj.
     * @param __current  ice context.
     * @throws NameAlreadyInUseException if there is already a proxy bound to the given name.
     */
    public synchronized void registerObject(String name, ObjectPrx obj, Current __current) throws NameAlreadyInUseException {

        assert name != null && name.length()>0;

        ObjectPrx exist = mObjects.get(name);

        if (exist != null && Ice.Util.proxyIdentityCompare(exist, obj)!=0) {
            throw new NameAlreadyInUseException(name, exist);
        }
        else {
            mObjects.put(name, obj);
        }

        for (NamingServiceEventHandlerPrx namingServiceEventHandler : mEventHandler) {
            namingServiceEventHandler.handleRegister(name, obj);
        }
    }

    /**
     * Unregister the object proxy bound to the specified name.
     * After a call to this method the name is free for use by registerObject
     * @param name         the unique identifier used to register the proxy.
     * @param __current    ice context.
     */
    public synchronized void unregisterObject(String name, Current __current) {

        assert name != null && name.length()>0: "name != null && name.length()>0";

        // obj is maybe null so we need to test for it
        if (mObjects.containsKey(name)) {

            ObjectPrx obj = mObjects.remove(name);

            for (NamingServiceEventHandlerPrx namingServiceEventHandler : mEventHandler) {
                namingServiceEventHandler.handleUnregister(name, obj);
            }
        }
    }

    /**
     * Returns the object proxy bound to the given name.
     * @param name         the unique identifier used to register object proxy.
     * @param __current    ice context.
     * @return   the object proxy or null if not found.
     */
    public ObjectPrx findByName(String name, Current __current) {
        return mObjects.get(name);
    }


    /**
     * Adds an event listener that is notified for all registration and unregistrations of all object proxies
     * and managed node proxies at this naming service. The handler is successfully added to the list
     * if it receives a call to initialList otherwise the registration failed.
     * @param handler    the event handler to be registered.
     * @param __current  the ice context.
     */
    public synchronized void registerEventHandler(NamingServiceEventHandlerPrx handler, Current __current) {
        int size = mObjects.size();
        String[] names = new String[size];
        ObjectPrx[] proxies = new ObjectPrx[size];

        int i=0;
        for (Map.Entry<String, ObjectPrx> entry : mObjects.entrySet()) {
            assert entry != null;
            names[i] = entry.getKey();
            proxies[i] = entry.getValue();
            i++;
        }

        handler.initialList(names, proxies);
        mEventHandler.add(handler);

    }

    /**
     * Unregister the event handler no further events will be send to it.
     * @param handler   the registered handler handler registered at the
     * @param __current ice context
     */
    public synchronized void unregisterEventHandler(NamingServiceEventHandlerPrx handler, Current __current) {
        mEventHandler.remove(handler);
    }


    // Stuff only required for new ice activation procedure

    public void activate(Ice.ObjectAdapter a) {

         NamingServicePrxHelper.uncheckedCast(a.add(this, _id)); 
    }

    public final Identity _id = Ice.Util.stringToIdentity("NamingService");
}
