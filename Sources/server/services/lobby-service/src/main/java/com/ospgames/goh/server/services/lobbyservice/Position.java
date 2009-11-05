package com.ospgames.goh.server.services.lobbyservice;

/**
* Created by IntelliJ IDEA.
* User: kempa
* Date: 07.10.2009
* Time: 21:55:12
* To change this template use File | Settings | File Templates.
*/
class Position {
    final float x;
    final float y;
    final float z;

    public Position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float distanceTo(Position p) {
        double dx = ((double)p.x)-(double)x;
        double dy = ((double)p.y)-(double)y;
        double dz = ((double)p.z)-(double)z;

        double distance = Math.sqrt(dx*dx+dy*dy+dz*dz);

        if (distance > Float.MAX_VALUE && distance < Float.MIN_VALUE ) {
            throw new IllegalStateException("Math overflow");
        }

        return (float)distance;
    }
}
