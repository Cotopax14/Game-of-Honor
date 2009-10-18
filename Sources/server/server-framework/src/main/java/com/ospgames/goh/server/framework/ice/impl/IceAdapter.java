package com.ospgames.goh.server.framework.ice.impl;

import Ice.*;
import Ice.Object;
import IceStorm.*;
import com.ospgames.goh.server.framework.errorhandling.Emergency;
import com.ospgames.goh.server.framework.ice.IIceAdapter;
import com.ospgames.goh.services.NamingServicePrx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * This ILifecycleAspect interface is used by all application components so
 * that managed nodes can propagate lifecylce events to them.
 *
 * @author kempa
 * @version 27.04.2007
 */
public class IceAdapter implements IIceAdapter {

    public static final Log sLog = LogFactory.getLog(IceAdapter.class);

    private Communicator mCommunicator=null;
    private NamingServicePrx mNamingServicePrx=null;
    private ObjectAdapter mObjectAdapter=null;
    private TopicManagerPrx mTopicManagerPrx=null;

    public void setCommunicator(Communicator communicator) {
        mCommunicator = communicator;
    }

    public void setTopicManagerPrx(TopicManagerPrx topicManagerPrx) {
        mTopicManagerPrx = topicManagerPrx;
    }

    public TopicManagerPrx getTopicManagerPrx() {
        return mTopicManagerPrx;
    }

    public void destroy() {

        mTopicManagerPrx = null;
        mNamingServicePrx = null;

        if (mObjectAdapter != null) {
            try {
                mObjectAdapter.deactivate();
                mObjectAdapter.destroy();

            } catch (Throwable e) {
                sLog.error("Error during object adapter cleanup",e);
            }
            mObjectAdapter = null;
        }

        if (mCommunicator != null) {
            try {
                mCommunicator.shutdown();
                sLog.trace("Waiting for shutdown");
                // wait until all running requests have beeen processed.
                mCommunicator.waitForShutdown();
                sLog.trace("Shutdown successful");
                mCommunicator.destroy();

            }
            catch (Throwable t) {
                sLog.error("Error during communicator cleanup",t);
            }
            mCommunicator = null;
            System.gc();
            sLog.debug("Destroy successful");
        }
    }

    public void setNamingServicePrx(NamingServicePrx namingServicePrx) {
        mNamingServicePrx = namingServicePrx;
    }

    public NamingServicePrx getNamingServicePrx() {
        if (mNamingServicePrx == null) throw new IllegalStateException("No naming service proxy initialized, is null");
        return mNamingServicePrx;
    }


    public void setObjectAdapter(ObjectAdapter objectAdapter) {
        mObjectAdapter = objectAdapter;
    }

    public Communicator getCommunicator() {
        if (mCommunicator == null) throw new IllegalStateException("No Communicator initialized, is null");
        return mCommunicator;
    }


    public ObjectAdapter getObjectAdapter() {
        if (mObjectAdapter == null) throw new IllegalStateException("No object adapter initialized, is null");
        return mObjectAdapter;
    }

    /**
     * Makes the object accessible by ice and return the proxy that can be used for remote or local access.
     * Proxy is already narrowed so we don't need checkedCast use ..Helper.uncheckedCast to convert result to
     * your typed proxy.
     * 
     * @param id    an unique id, not null, not empty.
     * @param obj   the ice object to be made accessible, not null.
     * @return  the proxy that can be used for remote or local access, not null.
     */
    public ObjectPrx makeObjectAccessible(String id, Object obj) {

        Emergency.checkPrecondition(id != null && id.length()>0, "id != null && id.length()>0");
        Emergency.checkPrecondition(obj != null, "obj != null");

        Identity ident = Ice.Util.stringToIdentity(id);
        // Make Object accessible from outsite
        ObjectAdapter adapter = getObjectAdapter();
        ObjectPrx objPrx = adapter.add(obj, ident);

        // Create a direct proxy which can be used to address the object.
        // objPrx = adapter.createDirectProxy(ident);
        assert objPrx != null;
        return objPrx;
    }


    /**
     * Removes the accessible object with the given id and returns it.
     * @param id   the id.
     * @return the removed object might be null if not registered.
     */
    public Object removeAccessibleObject(String id) {
        ObjectAdapter adapter = getObjectAdapter();
        Identity ident = Ice.Util.stringToIdentity(id);
        return adapter.remove(ident);
    }


    /**
     * Creates or delivers the existing topic of the given name.
     * @param name  the name of the topic, not null, length >0
     * @return the created or returned Topic.
     */
    public ObjectPrx getOrCreateTopic( String name) {

        Emergency.checkPrecondition( mTopicManagerPrx != null , "mTopicManagerPrx != null");
        Emergency.checkPrecondition( name != null && name.length()>0 , "name != null && name.length()>0");

        TopicPrx topicPrx = null;
        do {

            try {
                topicPrx = mTopicManagerPrx.retrieve(name);
            }
            catch (NoSuchTopic noSuchTopic) {
                try {
                    topicPrx = mTopicManagerPrx.create(name);
                }
                catch (TopicExists topicExists) {
                    // repeat retrieve topic
                }
            }
        } while (topicPrx == null);
        return topicPrx.getPublisher();
    }

    public void subscribeToTopic(String topicName, String id) {
        // subscribe to parent channel to receive lifecycle events
        ObjectPrx objPrx = getNamingServicePrx().findByName(id);
        Emergency.checkPrecondition(objPrx != null, "objPrx != null for id: "+id);

        TopicPrx parentTopic = getTopicOrWait(topicName, 2000);
        Map<String,String> params = new HashMap<String,String>();

        boolean tryAgain=false;
        do
        try {
            tryAgain = false;
            parentTopic.subscribeAndGetPublisher(params, objPrx );
            sLog.debug("Subscribe to channel: "+topicName);
        }
        catch (AlreadySubscribed alreadySubscribed) {

            sLog.warn("Resubscribe with id '"+id+"'");
            parentTopic.unsubscribe(objPrx);
            tryAgain = true;
        }
        catch (BadQoS badQoS) {
            Emergency.now("Bad QoS on subscription of '"+id+"'",badQoS);
        }
        while (tryAgain);
    }


    /**
     * Try to get an existing topic or try until on can be found.
     * @param name the name of the topic
     * @return the existing topic.
     */
    public TopicPrx getTopicOrWait( String name, long waitTimeIfNotExisting) {
        Emergency.checkPrecondition(mTopicManagerPrx != null , "mTopicManagerPrx != null");
        Emergency.checkPrecondition( name != null && name.length()>0 , "name != null && name.length()>0");

        TopicPrx topicPrx = null;
        do {

            try {
                topicPrx = mTopicManagerPrx.retrieve(name);
            }
            catch (NoSuchTopic noSuchTopic) {
                synchronized (this) {
                    try {
                        if (sLog.isDebugEnabled()) {
                            sLog.debug("Wait for Topic '"+name+"'");
                        }

                        wait(waitTimeIfNotExisting);
                    }
                    catch (InterruptedException e) {
                        Emergency.unexpectedInterrupt();
                    }
                }
            }
        } while (topicPrx == null);
        return topicPrx;
    }


    protected  <T extends ObjectPrx, P extends ObjectPrxHelperBase & ObjectPrx> T checkedCast(ObjectPrx prx, Class resultType, Class<P> helperClass ) {

        T __d = null;
        if(prx != null)
        {
            try
            {
                // T is "ObjectPrx" so any cast is true
                // this seems a limitation of the java generics implementation
                // thats why we need explicit checks here
                if (resultType.isAssignableFrom(prx.getClass())) {
                    __d = (T)prx;
                }
                else {
                    throw new ClassCastException();
                }
            }
            catch(ClassCastException ex)
            {
                // This is needed to narrow the Proxie and to check if there is really an object
                // bound behind the proxy!
                prx.ice_isA(prx.ice_getAdapterId());

                P helper = null;
                try {
                    helper = helperClass.newInstance();
                }
                catch (InstantiationException e) {
                    Emergency.now("Could not instanciate Helper class: "+e);
                }
                catch (IllegalAccessException e) {
                    Emergency.now("Could not instanciate Helper class: "+e);
                }
                helper.__copyFrom(prx);
                try {
                    __d = (T) helper;
                }
                catch (ClassCastException e) {
                    // this is a configuration error
                    Emergency.now("Input parameter error, ProxyHelper class is not compatible to requested ProxyClass: "+e);
                }
            }
        }
        return __d;
    }

    /**
     * Creates a proxy from a given property and wait until the servant (server site) is accessible.
     * @param targetClass  The interface of the proxy need to be compatible
     * @param helper       The proxy helper class that is created by ice, it is used as proxy implementation.
     * @param property     The property name of the proxy.
     * @param waitTimeMs   Time between retries (>=0), in ms
     * @return The created proxy.
     */
    public <T extends ObjectPrx, P extends ObjectPrxHelperBase & ObjectPrx> T getPrxOrWait(Class targetClass, Class<P> helper, String property,
                                                                                           long waitTimeMs) {
        sLog.debug("Get or wait proxy: "+property);
        Emergency.checkPrecondition(waitTimeMs >=0, "waitTimeMs >=0");

        // Create untyped proxy from properties
        ObjectPrx objPrx=getCommunicator().propertyToProxy(property);

        if (objPrx == null) {
            Emergency.now("Property '"+property+"' is not configured, propertyToProxy returned null!");
        }

        // Convert type to correct proxy type, wrap within the ProxyHelper
        do {
            try {
                // exclicit cast is necessary to define the return type to be used.
                return (T)checkedCast(objPrx, targetClass, helper);
            }
            catch (LocalException e) {
                // try again
                sLog.debug("Retrieve proxy for property '"+property+"' failed - error:"+e);
            }
            if (sLog.isDebugEnabled()) sLog.debug("Wait for Proxy: "+objPrx);
            synchronized (this) {
                try {
                    wait(waitTimeMs);
                }
                catch (InterruptedException ie) {
                    Emergency.unexpectedInterrupt();
                }
            }
        }
        while (true);
    }

   /**
     * Returns the proxy of the given targetClass.
     * @param targetClass  The interface of the proxy need to be compatible
     * @param helper       The proxy helper class that is created by ice, it is used as proxy implementation.
     * @param name         The property name of the proxy.
     * @return The created proxy.
     */
    public <T extends ObjectPrx, P extends ObjectPrxHelperBase & ObjectPrx> T getPrx(Class targetClass,
                                                                     Class<P> helper, String name) {
        sLog.debug("Get proxy: "+name);

        Identity id = Ice.Util.stringToIdentity(name);

        // Create untyped proxy from properties
        ObjectPrx objPrx=getObjectAdapter().createProxy(id);

        if (objPrx == null) {
            return null;
        }
        else {
                // exclicit cast is necessary to define the return type to be used.
            return (T)checkedCast(objPrx, targetClass, helper);
        }
   }
}
