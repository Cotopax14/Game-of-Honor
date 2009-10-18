package com.ospgames.goh.server.framework.ice;

import com.ospgames.goh.server.framework.errorhandling.Emergency;
import com.ospgames.goh.server.framework.ice.impl.IceAdapter;


/**
 * This ILifecycleAspect interface is used by all application components so
 * that managed nodes can propagate lifecylce events to them.
 *
 * @author kempa
 * @version 02.05.2007
 */
public final class IceAdapterProvider {

    private static IIceAdapter sAdapter;

    /**
     * Return the current IceAdapter used by this application, not null.
     * @return the current IceAdapter used by this application, not null.
     * @precondition there should be an intialized instance of this class, sAdapter != null.
     */
    public static IIceAdapter getIceAdapter() {
        Emergency.checkPrecondition(sAdapter != null, "sAdapter != null");
        return sAdapter;
    }

    /**
     * Create a new IceAdapterProvider for this application, initialize the static member.
     * @precondition sAdapter != null
     * @param adapter the adapter to be exposed, not null.
     */
    public IceAdapterProvider(IceAdapter adapter) {
        synchronized (IceAdapterProvider.class) {
            Emergency.checkPrecondition(sAdapter == null && adapter != null, "sAdapter == null && adapter != null");
            sAdapter = adapter;
        }
    }

    /**
     * reset the sAdapter to null.
     */
    public void release() {
        sAdapter = null;
    }
}
