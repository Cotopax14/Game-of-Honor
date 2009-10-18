package com.ospgames.goh.server.application.impl;

import Ice.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.ospgames.goh.server.framework.ice.impl.IceAdapter;
import com.ospgames.goh.server.framework.ice.impl.ClasspathLoaderProperties;
import com.ospgames.goh.server.framework.errorhandling.Emergency;
import com.ospgames.goh.server.services.namingservice.LoggingNameServiceEventHandler;
import com.ospgames.goh.server.services.namingservice.NamingServiceImpl;
import com.ospgames.goh.services.*;


/**
 * This class provides the functions to setup a running ice environment and the
 * functions to shutdown this environment.
 * It is the common base for the console applications and web applications.
 *
 * @author kempa
 * @version 22.05.2007
 */
public class IceApplication {

    private static final Log sLog = LogFactory.getLog(IceApplication.class);

    private IceAdapter mIceAdapter=null;


    public IceApplication() {

    }

    /**
     * Initialize the client/application site of the ice middleware.
     *
     * @param configInputStream        Classpath based path to Config file, not null.
     * @param objectAdapterName The name of the objectAdapter defined within the config file by a set of properties, not null.
     * @param args              Intitial arguments for the ice middleware.
     * @return the ice adapter with initialized communicator, applicationName and ObjectMappings.
     * @throws java.io.IOException if the given input stream could not be read.

     */
    public IceAdapter initializeIce(InputStream configInputStream, String objectAdapterName, String[] args) throws IOException {

        Emergency.checkPrecondition(configInputStream != null, "configInputStream != null");

        InitializationData initData = new InitializationData();
        // pass in parameters read from properties as string array.
        //
        Communicator ic = null;
        IceAdapter adapter = null;
        try {
            ClasspathLoaderProperties properties = new ClasspathLoaderProperties();

            properties.loadFromStream(configInputStream);
            initData.properties = properties;

            StringSeqHolder argHolder = new StringSeqHolder(args);

            if (sLog.isDebugEnabled()) {

                String[] values = argHolder.value;
                Properties p = initData.properties;
                sLog.debug("argHolder: "+ Arrays.toString(values)+ " initData: "+p);
            }
            ic = Util.initialize(argHolder, initData);

            ObjectAdapter objectAdapter = ic.createObjectAdapter(objectAdapterName);
            if (objectAdapter == null) {
                Emergency.now("No ObjectAdapter with name '" + objectAdapterName + "' defined in config-file " + configInputStream + "'!");
            }
            else {
                objectAdapter.activate();
            }

            GohObjectFactory factory = new GohObjectFactory();
            factory.registerMappings(ic);

            adapter = new IceAdapter();
            adapter.setCommunicator(ic);
            adapter.setObjectAdapter(objectAdapter);
            if (sLog.isTraceEnabled()) sLog.trace("IceAdapter initialized.");
            mIceAdapter = adapter;
            return adapter;
        }
        catch (LocalException ex) {
            sLog.error("Could not intialize ice ", ex);


            if (adapter != null) {
                try {
                adapter.destroy();
                }
                catch (Throwable t) {
                    sLog.warn("Error during adapter.destroy: ", t);
                }
            }

            if (ic != null) {
                try {
                    ic.destroy();
                }
                catch (Throwable t) {
                    sLog.warn("Error during ic.destroy: ", t);
                }
            }
            
            Emergency.now("LocalException in initializeIce", ex);
            // never reached by code but required for ide pleasure.
            return null;
        }
    }

    /**
     * Load the naming service.
     * @param namingServiceProxyName property name of the naming service
     * @param hostNamingService      true if this instance should create naming service, else false.
     */
    public void loadIceServices(String namingServiceProxyName, boolean hostNamingService) {

        long waitTimeMs = 2000;

        // TopicManager is a Ice Service implemented in C++ and is started by the icebox.
        // Because it is implemented in C++ we need the IceBox and can not use the Server
        // (Java Server can not start C++ Services)
        //TopicManagerPrx topicManagerPrx = mIceAdapter.getPrxOrWait(TopicManagerPrx.class, TopicManagerPrxHelper.class, iceStormProxyName, waitTimeMs);
        //Emergency.checkPostcondition(topicManagerPrx != null, "topicManagerPrx != null");
        //mIceAdapter.setTopicManagerPrx(topicManagerPrx);

        if (hostNamingService) {
            // Create NamingService amd Listener
            NamingServiceImpl service = new NamingServiceImpl();
            NamingServiceEventHandler eventHandler = new LoggingNameServiceEventHandler();
            ObjectPrx loggerObjPrx = mIceAdapter.makeObjectAccessible("NamingServiceLogger", eventHandler);
            NamingServiceEventHandlerPrx loggerPrx = NamingServiceEventHandlerPrxHelper.checkedCast(loggerObjPrx);
            service.registerEventHandler(loggerPrx);

            ObjectPrx objPrx = mIceAdapter.makeObjectAccessible("NamingService", service);
            NamingServicePrx prx = NamingServicePrxHelper.uncheckedCast(objPrx);
            Emergency.checkPrecondition(prx != null, "prx != null");
            sLog.debug("NamingService created under: '"+prx+"'");
            mIceAdapter.setNamingServicePrx(prx);

        }
        else {
            NamingServicePrx namingServicePrx = mIceAdapter.getPrxOrWait(NamingServicePrx.class, NamingServicePrxHelper.class, namingServiceProxyName, waitTimeMs);
            Emergency.checkPostcondition(namingServicePrx != null, "namingServicePrx != null");
            mIceAdapter.setNamingServicePrx(namingServicePrx);
        }

    }

    public void release() {
        cleanUpIce();

        // ice box is stopped by shutdown hook
    }


    protected synchronized void cleanUpIce() {

        if (mIceAdapter != null) {
            try {
                mIceAdapter.destroy();
            }
            catch (Throwable e) {
                sLog.error("Error during cleanup", e);
            }
            mIceAdapter = null;
        }

    }



    protected IceAdapter getIceAdapter() {
        Emergency.checkPostcondition(mIceAdapter != null, "mIceAdapter != null");
        return mIceAdapter;
    }


    public void waitForShutdown() {
        try {
            mIceAdapter.getCommunicator().waitForShutdown();
        }
        finally {
            sLog.info("Application shutdown signal received!");
        }
    }
}
