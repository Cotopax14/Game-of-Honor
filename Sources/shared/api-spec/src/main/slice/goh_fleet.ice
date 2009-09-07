/*! \file goh_generic.slice
 *  \brief General GOH ship related types.
 *
 * This file contains GOH ship type definitions,
 * such as the namespace, the module hierarchy and some basic
 * datatypes.
 */

#ifndef _GOH_SHIP_IDL_
#define _GOH_SHIP_IDL_

#include "goh_generic.ice"
#include "goh_space.ice"

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
    module fleet {
      /**
       * Ship types.
       */


      struct ShipType {
        string name;         /** unique name of this ship type */
        long   sensors;      /** max Sensor Strength     */
        long   attackPower;  /** max Attack power        */
        long   defensePower; /** max Defense power       */
        long   structure;    /** max Structure points    */
        long   hyperA;       /** max Acceleration in hyper space in m/s^2  */
        long   hyperV;       /** max Speed in Hyperspace  in m/s           */
        long   normalA;      /** max Acceleation in normal space    */
        long   normalV;      /** max Speed in normal space in m/s^2 */
        long   mass;         /** in 1.000 t          */
      };

      struct Ship {

        ShipType type;       /** the type of this ship */
        /** How do we detect change in space ? */
        space::SpaceType loc;     /** current space hyper or normal  */
        generic::Vector3D pos;        /** current postion       */
        generic::Vector3D speed;      /** current speed in m/s           */
        generic::Vector3D accel;      /** current accelleration in m/s^2 */
        long   structure;    /** Current Structure              */
        long   minStructure; /** minimum of structure since last repair at dock */
      };

      /** Sequence of ships */
      ["java:type:java.util.ArrayList<Ship>:java.util.List<Ship>"]
      sequence<Ship>      ShipSeq;

      struct ShipGroup {
          string              name;
          ShipSeq            ships;
      };

      /** Sequence of ship groups */
      ["java:type:java.util.ArrayList<ShipGroup>:java.util.List<ShipGroup>"]
      sequence<ShipGroup>      ShipGroupSeq;

    }; // fleet module
  }; // goh module
}; // ospgames module

#endif // _GOH_SHIP_IDL_

