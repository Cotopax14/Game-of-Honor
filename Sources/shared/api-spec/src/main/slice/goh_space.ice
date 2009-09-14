/*! \file goh_generic.slice
 *  \brief Space GOH types.
 *
 * This file contains GOH type definitions for space elements,
 * such as the namespace, the module hierarchy and some basic
 * datatypes.
 */

#ifndef _GOH_SPACE_IDL_
#define _GOH_SPACE_IDL_

#include "goh_generic.ice"

/**
 * Module root.
 */
[["java:package:com"]]
module ospgames {
  /**
   * Top level module for goh.
   */
  module goh {
    /**
     * Generic data types
     */
    module space {
      /**
       * Space types.
       */

      /** Space Type */
      enum SpaceType {
        	NORMAL, /** Normal space */
        	HYPER   /** Hyper space */
      };

      class StarType {
            string name;                /** unique name of this type  */
            long   resources;           /** resources to aquire per unit of time */
            long   hyperlimitRadius;    /** radius of the hyperlimit in m */
            long   outerHabitableRadius;/** outer radius of the habitable zone in m */
      };

      class Star {
        long     id;                /** unique id of the star     */
        string   name;              /** unique name of the star   */
        generic::Vector3D position; /** fix position of this star */
        StarType type;              /** type of this star         */
      };


      /** Sequence of stars */
	  ["java:type:java.util.ArrayList<com.ospgames.goh.space.Star>:java.util.List<com.ospgames.goh.space.Star>"]
	  sequence<Star>   StarSeq;


      /** TO BE DISCUSSED: might be structure of the specific domain
          e.g. owner and building colony management */
      // struct StarVariableData {
      //  long           starId;     /** id of the star */
      //  Player          owner;     /** owner of this star system, null if none */
      //  BuildingSeq buildings;     /** buildings improving this star system, empty list if none */
      //  long   LastBattleTime;     /** last time of a fight at this system, 0 if none  */
      // };


      


    }; // space module
  }; // goh module
}; // ospgames module

#endif // _GOH_SPACE_IDL_