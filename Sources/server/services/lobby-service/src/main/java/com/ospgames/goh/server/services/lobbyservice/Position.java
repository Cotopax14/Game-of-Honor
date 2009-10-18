package com.ospgames.goh.server.services.lobbyservice;

/**
* Created by IntelliJ IDEA.
* User: kempa
* Date: 07.10.2009
* Time: 21:55:12
* To change this template use File | Settings | File Templates.
*/
class Position {
    final double x;
    final double y;
    final double z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distanceTo(Position p) {
        double dx = p.x-x;
        double dy = p.y-y;
        double dz = p.z-z;

        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
}
