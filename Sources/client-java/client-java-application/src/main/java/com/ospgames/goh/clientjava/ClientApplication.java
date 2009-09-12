package com.ospgames.goh.clientjava;

import com.sun.opengl.util.GLUT;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.*;

public class ClientApplication
{
    public static void main(String[] args){
        long numberToCheck;
        long primeNumberCheckResult;

        Scanner in = new Scanner(System.in);
        System.out.println("Bitte gib eine Zahl ein, die Du für eine Primzahl hältst: ");
        numberToCheck = in.nextLong();
        primeNumberCheckResult = isPrimeNumber(numberToCheck);

        if (primeNumberCheckResult==0){
            System.out.println("Gewonnen! Die Zahl "+numberToCheck+" ist eine Primzahl!");
        }
        else{
            System.out.println("Sorry! Die Zahl "+numberToCheck+" ist keine Primzahl! Sie lässt sich z.B. durch "+primeNumberCheckResult+" teilen.");
            System.out.println(numberToCheck+" / "+primeNumberCheckResult+" = "+(double)numberToCheck/(double)primeNumberCheckResult);
        }

        System.out.println("\nAlle Primzahlen bis "+numberToCheck+": \n2\n3");
        allPrimeNumbers(numberToCheck);
    }

    public static long isPrimeNumber(long numberToCheck){
        long i;
        for (i=2; i<(int)Math.sqrt(numberToCheck); i++){
            if (Math.floor((double)(numberToCheck/i)) == (double)numberToCheck/i){
                return(i);
            }
        }
        return(0);
    }

    public static long allPrimeNumbers(long maxNumber){
        int j=0, k=0;
        int i=0;
        int primeNumbers[] = new int[100000];
        primeNumbers[0]=2;
        primeNumbers[1]=3;
        long startTime=System.currentTimeMillis();
        long endTime;

        int numberOfPrimeNumbers=2;
        int firstNumberOutsidePrimeNumberRange=0;
        boolean primeNumberRuledOut=false;

        for (i=3; i<=maxNumber; i+=2){

//            System.out.println("Zu prüfende Zahl: "+i);

            primeNumberRuledOut=false;

            for (j=0; j<numberOfPrimeNumbers; j++){

//                System.out.print(primeNumbers[j]+" - ");

                if (Math.floor((double)(i/primeNumbers[j])) == (double)i/primeNumbers[j]){
                    primeNumberRuledOut=true;
                }
            }

//            System.out.println();

            for (k=primeNumbers[j-1]; k<(int)Math.sqrt(i); k+=2){
                if (Math.floor((double)(i/k)) == (double)i/k){
                    primeNumberRuledOut=true;
                }
            }

            if (!primeNumberRuledOut){
//                System.out.println(i);
                primeNumbers[numberOfPrimeNumbers++]=i;
            }
        }

        endTime=System.currentTimeMillis();

        System.out.println("Insgesamt "+numberOfPrimeNumbers+" Primzahlen gefunden in "+(endTime-startTime)+" Millisekunden = "+(endTime-startTime)/1000+" Sekunden");
        return(numberOfPrimeNumbers);
    }
}
