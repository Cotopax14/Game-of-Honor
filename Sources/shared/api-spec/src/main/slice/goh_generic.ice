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
        long year;     /**< The year with century */
        long month;    /**< The month (1..12) */
        long day;      /**< The day (1..31) */
        long hour;     /**< Hour in 24h format (0..23) */
        long minute;   /**< The minute */
        long second;   /**< The second */
        long milliSeconds; /**< The millisecond */
      };

      struct Vector3D {
        long x;         /** X-part */
        long y;         /** Y-part */
        long z;         /** Z-part */
      };



      

    }; // generic
  }; // goh
}; // ospgames

#endif // _GOH_GENERIC_IDL_