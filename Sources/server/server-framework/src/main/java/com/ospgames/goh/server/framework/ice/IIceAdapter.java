package com.ospgames.goh.server.framework.ice;

import Ice.*;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicPrx;
import com.ospgames.goh.services.NamingServicePrx;


/**
 * Defines the methods required to use ice within a application.
 *
 * @author kempa
 * @version 27.04.2007
 */
public interface IIceAdapter {

    /**
     * Returns the current communicator of the applicationm, not null.
     *
     * @return the current communicator of the applicationm, not null.
     * @throws IllegalStateException if no communicator is available.
     */
    public Communicator getCommunicator();


    /**
     * Returns the naming service proxy used by the application, not null.
     * @return the naming service proxy used by the application, not null.
     * @throws IllegalStateException if no naming service proxy is available.
     */
    public NamingServicePrx getNamingServicePrx();


    /**
     * Returns the object adapter used by this application, not null.
     * @return the object adapter used by this application, not null.
     * @throws IllegalStateException if no object adapter is available.
     */
    public ObjectAdapter getObjectAdapter();


    /**
     * Returns the topic manager proxy used by this application, not null.
     * @return the topic manager proxy used by this application, not null.
     */
    public TopicManagerPrx getTopicManagerPrx();


    /**
     * Destroys the client site objects, release all references to proxies.
     */
    public void destroy();


    public void subscribeToTopic(String topicName, String id);

    /**
     * Creates or delivers the existing topic of the given name.
     * @param name  the name of the topic, not null, length >0
     * @return the created or returned Topic.
     */
    public ObjectPrx getOrCreateTopic( String name);

    /**
     * Try to get an existing topic or try until on can be found.
     * @param name the name of the topic
     * @return the existing topic.
     */
    public TopicPrx getTopicOrWait( String name, long waitTimeIfNotExisting);

    /**
     * Creates a proxy from a given property and wait until the servant (server site) is accessible.
     * @param targetClass  The interface of the proxy need to be compatible
     * @param helper       The proxy helper class that is created by ice, it is used as proxy implementation.
     * @param property     The property name of the proxy.
     * @param waitTimeMs   Time between retries (>=0), in ms
     * @return The created proxy.
     */
    public <T extends ObjectPrx, P extends ObjectPrxHelperBase & ObjectPrx> T getPrxOrWait(Class targetClass,
                                                                                           Class<P> helper, String property, long waitTimeMs);



    /**
     * Returns the proxy of the given targetClass, object need to be registered already.
     * @param targetClass  The interface of the proxy need to be compatible
     * @param helper       The proxy helper class that is created by ice, it is used as proxy implementation.
     * @param name         The property name of the proxy.
     * @return The created proxy.
     */
    public <T extends ObjectPrx, P extends ObjectPrxHelperBase & ObjectPrx> T getPrx(Class targetClass,
                                                                     Class<P> helper, String name);


    /**
     * Makes the object accessible by ice and return the proxy that can be used for remote or local access.
     * @param id    an unique id, not null, not empty.
     * @param obj   the ice object to be made accessible, not null.
     * @return  the proxy that can be used for remote or local access, not null.
     */
    ObjectPrx makeObjectAccessible(String id, Ice.Object obj);

    /**
     * Removes the accessible object with the given id and returns it.
     * @param id   the id.
     * @return the removed object might be null if not registered.
     */
    public Ice.Object removeAccessibleObject(String id);
}
