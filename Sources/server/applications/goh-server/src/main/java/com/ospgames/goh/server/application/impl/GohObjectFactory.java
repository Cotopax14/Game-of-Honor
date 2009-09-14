package com.ospgames.goh.server.application.impl;

import com.ospgames.goh.space.*;
import com.ospgames.goh.fleet.*;
import Ice.AlreadyRegisteredException;
import Ice.Communicator;
import Ice.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ospgames.goh.server.framework.errorhandling.Emergency;

/**
 * Implements the Object Factory to serialize and deserialize the node hierarchy correctly.
 *
 * @author kempa
 * @version 30.03.2007
 */
public class GohObjectFactory extends Ice.LocalObjectImpl implements Ice.ObjectFactory {
    public static final Log sLog = LogFactory.getLog(GohObjectFactory.class);

    public static class Item {
        Class mItemClass;
        String mName;
        ObjectFactory mObjectFactory;

        public Item(Class itemClass, String name) {
            this.mItemClass = itemClass;
            this.mName = name;
        }

        public Item(Class itemClass, String name, Ice.ObjectFactory objectFactory) {
            this.mItemClass = itemClass;
            this.mName = name;
            mObjectFactory = objectFactory;
        }

        public Class getItemClass() {
            return mItemClass;
        }

        public String getName() {
            return mName;
        }
    }


    public static final Item[] Mappings = new Item[] {
        new Item(StarType.class, StarType.ice_staticId()),
        new Item(Star.class, Star.ice_staticId()),
        new Item(ShipType.class, ShipType.ice_staticId()),
        new Item(Ship.class, Ship.ice_staticId()),
        new Item(ShipGroup.class, ShipGroup.ice_staticId())
    };


    public void registerMappings(Communicator ic) {
        for (int i = 0; i < Mappings.length; i++) {
            Item mapping = Mappings[i];
            ObjectFactory factory;
            if (mapping.mObjectFactory != null) {
                factory = mapping.mObjectFactory;
            }
            else {
                factory = this;
            }
            try {
                ic.addObjectFactory(factory, mapping.getName() );
            }
            catch (AlreadyRegisteredException e) {
                sLog.error("Factory is already registered '"+mapping.getName()+"'", e);
            }
        }
    }

    public Ice.Object
    create(String type)
    {
        for (int i = 0; i < Mappings.length; i++) {
            Item mapping = Mappings[i];
            if (type.equals(mapping.getName())) {
                try {
                    return (Ice.Object) mapping.getItemClass().newInstance();
                }
                catch (InstantiationException e) {
                    Emergency.now("Instanciation failed: ",e);
                }
                catch (IllegalAccessException e) {
                    Emergency.now("Instanciation failed: ",e);            
                }

            }
        }
        Emergency.now("Unknown mapping '"+type+"'");
        return null;
    }

    public void
    destroy()
    {
        // Nothing to do
    }
}
