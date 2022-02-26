package com.baatybek;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        double val = 428.78;
        double tickSize = 0.05;
        System.out.println(round1(val, tickSize) == round2(val, tickSize));
    }

    public static double round1(double val, double tickSize) {
        double factor = 1/tickSize;
        return Math.round(val * factor)/factor;
    }

    public static double round2(double val, double tickSize) {
        return Math.round(val/tickSize)*tickSize;
    }
}
