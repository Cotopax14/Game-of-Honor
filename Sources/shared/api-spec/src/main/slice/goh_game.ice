/*! \file goh_generic.slice
 *  \brief Space GOH types.
 *
 * This file contains GOH type definitions for space elements,
 * such as the namespace, the module hierarchy and some basic
 * datatypes.
 */

#ifndef _GOH_GAME_IDL_
#define _GOH_GAME_IDL_

#include "goh_generic.ice"
#include "goh_space.ice"
#include "goh_fleet.ice"


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
    module game {
      /**
       * Game types.
       */

       /**
        * Represents the data available for a player.
        * And the interface to send commands to own ships.
        */
       class GameSession {

            string playerName; /** unique name of the player */

            /**
             * Surrender and leave game.
             */
            void surrender();

            /**
             * Return the stars of the universe.
             */
            space::StarSeq getStars();
       };

       /** Sequence of players */
   	  ["java:type:java.util.ArrayList<GameSession>:java.util.List<GameSession>"]
  	  sequence<GameSession>   GameSessionSeq;


       /**
        * A player enters the lobby by call to joinLobby identifing itself with his account name and password.
        * He receives a player proxy that can be used to join game.
        *
        * The game needs a set of players and parameters for the world to be created.
        * Initialy there is only one fix set of
        */
       interface LobbyService {

            /**
             * Return the stars of the universe.
             */
            space::StarSeq getStars(); 

            /** Commented out to test Lobby with simple getStars method
            Player* joinLobby(string accountName, string password);


            createGame(Player* player, int playerNumber, string gameName);

            Game* joinGame(String gameName);

            */
       };



    }; // game module
  }; // goh module
}; // ospgames module

#endif // _GOH_GAME_IDL_