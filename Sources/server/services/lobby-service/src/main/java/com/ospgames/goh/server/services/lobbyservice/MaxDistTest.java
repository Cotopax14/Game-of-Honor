package com.ospgames.goh.server.services.lobbyservice;

import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: kempa
 * Date: 19.10.2009
 * Time: 22:30:15
 * To change this template use File | Settings | File Templates.
 */
public class MaxDistTest {

    public static final long LM = 300000L*1000*60;
    public static final long LY = LM*60*24*365;

    private static void testNumbers() {

        double d = (double)Long.MAX_VALUE * (double)Long.MAX_VALUE / (double)Long.MAX_VALUE;
        System.out.println("Conversion result: "+d);
    }

    public static void testBigInteger() {

        BigInteger big = BigInteger.valueOf(Long.MAX_VALUE);
        big.multiply(big);
    }


    public static void main(String[] args) {

        



        double d = Long.MAX_VALUE/LY;
        System.out.printf("Float: %+10.50f\n", d );
        double p = Double.MAX_VALUE/LY;
        System.out.printf("Float: %+10.50f\n", p );
        System.out.printf("Float: %+10.50f\n",0.1234567890123456789f);
        System.out.printf("Float: %+10.50f\n",0.1213141516171819212324f);
        System.out.println("Max Radius Long LY: "+Long.MAX_VALUE / LY);
        System.out.println("Max Radius Double LY: "+Double.MAX_VALUE / LY);
        testNumbers();
    }
}
