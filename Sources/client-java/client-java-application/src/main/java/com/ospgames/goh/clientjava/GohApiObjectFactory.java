package com.ospgames.goh.clientjava;

import Ice.ObjectFactory;
import Ice.Communicator;
import Ice.AlreadyRegisteredException;
import com.ospgames.goh.space.Star;
import com.ospgames.goh.space.StarType;


/**
 * Provides an object factory for the classes directly usable from generated code.
 */
public class GohApiObjectFactory implements Ice.ObjectFactory {
    //public static final Log sLog = LogFactory.getLog(GohApiObjectFactory.class);

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
        new Item(Star.class, Star.ice_staticId(), Star.ice_factory()),
        new Item(StarType.class, StarType.ice_staticId(), StarType.ice_factory()),
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
               // TODO sLog.error("Factory is already registered '"+mapping.getName()+"'", e);
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
                    // TODO Emergency.now("Instanciation failed: ",e);
                }
                catch (IllegalAccessException e) {
                    // TODO Emergency.now("Instanciation failed: ",e);
                }

            }
        }
        // TODO Emergency.now("Unknown mapping '"+type+"'");
        return null;
    }

    public void
    destroy()
    {
        // Nothing to do
    }
}

