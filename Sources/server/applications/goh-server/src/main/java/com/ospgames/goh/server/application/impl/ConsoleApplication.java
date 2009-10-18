package com.ospgames.goh.server.application.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import com.ospgames.goh.server.framework.ice.IceAdapterProvider;
import com.ospgames.goh.server.framework.ice.impl.IceAdapter;
import com.ospgames.goh.server.framework.errorhandling.Emergency;

/**
 * Provides a container for running applications from command line.
 * Supports creation of all application types.
 *
 * @author kempa
 * @version 13.05.2007
 */
public class ConsoleApplication {


    private static final Log sLog = LogFactory.getLog(ConsoleApplication.class);

    public static final String ICE_CONFIG_FILE_DEFAULT_VALUE = "ice-application-config.properties";
    public static final String ICE_ADAPTER_APPLICATION_CONTEXT_BEAN_NAME = "ice";

    public static final String PROPERTY_NAMING_SERVICE_PROXY_NAME = "NamingService.Proxy";
    public static final String PROPERTY_OBJECT_ADAPTER_NAME = "ObjectAdapter";


    // This is only set to hold a reference to
    // avoid dispose of the IceAdapter
    private IceAdapterProvider mProvider;

    private final String mIceConfig;


    private IceApplication mIceApplication;
    private Thread mShutdownHook;

    protected ConsoleApplication(String iceConfig) {
        mIceConfig = iceConfig;

        mIceApplication = new IceApplication();
    }


    /**
     * Initialize the Ice environment.
     * This method should be overwritten to configure this environment.
     * @param args the command line parameters.
     * @throws IOException if there was a problem with one of the configuration files.
     */
    protected void init(String[] args) throws IOException {

        // load ice config to initialize
        InputStream configAsStream = getClass().getClassLoader().getResourceAsStream(mIceConfig);
        if (configAsStream == null) throw new FileNotFoundException(mIceConfig);
        IceAdapter mIceAdapter = mIceApplication.initializeIce(configAsStream, PROPERTY_OBJECT_ADAPTER_NAME, args);

        Emergency.checkPostcondition(mIceAdapter != null, "adapter != null");
        mProvider = new IceAdapterProvider(mIceAdapter);
        try {

            mIceApplication.loadIceServices(PROPERTY_NAMING_SERVICE_PROXY_NAME, true);

        }
        catch (Throwable t) {
            mIceApplication.cleanUpIce();
            Emergency.now("Error within loadIceServices", t);
        }
    }

    int mReturnCode =0;

    public int main(String applicationName, String[] args) {

        try {
            try {
                init(args);
            }
            catch (IOException e) {
                sLog.error("Error reading alna config file", e);
                return -1;
            }

            addShutdownHook();



            if (mReturnCode == 0) {
                sLog.info("Application successfuly started.");
                mIceApplication.waitForShutdown();
            }
            else {
                sLog.info("Application start failed shutdown.");
            }

            sLog.debug("Start regular shutdown");
            System.exit(mReturnCode);
            sLog.info("Shutdown done successful.");
            return mReturnCode;

        }
        catch (Throwable t) {
            sLog.error("Unexpected error", t);
            return -4;
        }
    }


    private void addShutdownHook() {

        final ConsoleApplication app = this;

        mShutdownHook = new Thread() {
            public void run() {
                super.run();
                app.release();

            }
        };

        Runtime.getRuntime().addShutdownHook(mShutdownHook);
    }

    private void waitForFileShutdownSignal(String appName) {

        // TODO configurable place for the pid files.
        String fileName = "c:\\temp\\runningApps\\"+appName+".pid";

        File f = new File(fileName);

        if (!f.exists()) {
            sLog.debug("File '"+fileName+"' was not found stop application ");
            try {
                f.createNewFile();
            }
            catch (IOException e) {
                sLog.error("Could not create file '"+fileName+"'");
                return;
            }
        }

        try {
            while (f.exists()) {
                synchronized (f) {
                    f.wait(5000);
                }
            }
        }
        catch (InterruptedException e) {
            // go on.
        }
    }

    /**
     * Release all ressources.
     *
     * This is called from within a shutdown hook so 
     * no call to System.exit is allowed here!
     */
    protected synchronized void release() {

        if (mIceApplication != null) {
            mIceApplication.release();
        }
    }



    protected IceAdapter getIceAdapter() {
        IceAdapter adapter = mIceApplication.getIceAdapter();
        Emergency.checkPostcondition(adapter != null, "adapter != null");
        return adapter;
    }





}
