/*! \file naming_service.ice
 *  \brief Interface definition of naming service
 *
 * This file contains some interface
 * definitions for testing and debugging.
 *
 */

#ifndef _NAMING_SERVICE_IDL_
#define _NAMING_SERVICE_IDL_

/**
 * Module root.
 */
[["java:package:com"]]
module ospgames {
  /**
   * Top level module for GOH.
   */
  module goh {
    /**
     * Interface description for the Base module. It includes all
     * generic types and exceptions. All interfaces shall derive
     * from this interface.
     */
    module services {

           sequence<Object*> ObjectPrxSeq;
           sequence<string> NameSeq;

           /** defines the methods needed to handle registration changes */
           interface NamingServiceEventHandler {

                /**
                 * Called if a new name-object mapping was registrated.
                 * @param obj the ObjectProxy registred, not null.
                 */
                void handleRegister(string name, Object* obj);

                /**
                 * Called if an object was unregistrated.
                 * @param obj the ObjectProxy to be unregistrated.
                 */
                void handleUnregister(string name, Object* obj);

                /**
                 * This method is called by the NamingService to
                 * provide the current registered mappings.
                 * the mappings have the same order
                 * so names[0] belongs to objects[0] and so on.
                 * @names the names of the registrated mappings, not null.
                 * @objects the objects that are registrated, not null, length equals to names.
                 */
                void initialList(NameSeq names, ObjectPrxSeq objects);
           };

           /**
            * This exeption indicates a multiple register with the same name.
            * Overwrite Conflict when registered.
            */
           exception NameAlreadyInUseException {
                /**
                 * The name that was used during the registerObject operation.
                 */
                string name;

                /**
                 * The object currently bound.
                 */
                Object* obj;
           };

           /** defines the methods needed to manage a registry of named objects */
           interface NamingService {

                /**
                 * Register the given obj under the given name.
                 * if there is already an obj with this name.
                 * NameAlreadyUsedException
                 * @param name the name of the obj, not null or emptry.
                 * @param obj the ObjectProxy to be stored.
                 * @throws NameAlreadyInUseException if an name-object mapping exisits.
                 */
                void registerObject(string name, Object* obj) throws NameAlreadyInUseException;

                /**
                 * Removes the name-object from registry.
                 * This method removed the first occurance of the object.
                 * If an object was registrated multiple times this operation
                 * is not save.
                 * @param name the name of the object proxy to be removed.
                 */
                void unregisterObject(string name );

                /**
                 * Returns the ObjectProxy stored under the given name,
                 * maybe null if no such name binding exists.
                 * @param the name that was used in registerObject.
                 * @return the object proxy bound to the name.
                 */
                idempotent Object* findByName(string name);

                /**
                 * Register a new EventHandler calls initialList on this handler.
                 */
                void registerEventHandler(NamingServiceEventHandler* handler);

                /**
                 * Unregister the given event handler.
                 */
                void unregisterEventHandler(NamingServiceEventHandler* handler);

          }; // NamingService

    };  // module services

  }; // module goh

}; // module ospgames

#endif // _NAMING_SERVICE_IDL_