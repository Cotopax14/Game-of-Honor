/*! \file goh_generic.slice
 *  \brief General GOH types.
 *
 * This file contains general GOH type definitions,
 * such as the namespace, the module hierarchy and some basic
 * datatypes.
 */

#ifndef _GOH_GENERIC_IDL_
#define _GOH_GENERIC_IDL_

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
    module generic {
      /**
       * Generic types.
       */

      /** Generic type for date and time */
      struct DateTime {
        int year;     /**< The year with century */
        byte month;    /**< The month (1..12) */
        byte day;      /**< The day (1..31) */
        byte hour;     /**< Hour in 24h format (0..23) */
        byte minute;   /**< The minute */
        byte second;   /**< The second */
        byte milliSeconds; /**< The millisecond */
      };

      struct Vector3D {
        float x;         /** X-part */
        float y;         /** Y-part */
        float z;         /** Z-part */
      };


    }; // generic
  }; // goh
}; // ospgames

#endif // _GOH_GENERIC_IDL_