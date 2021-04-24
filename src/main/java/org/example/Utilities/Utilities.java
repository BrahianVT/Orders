package org.example.Utilities;

import org.example.Entity.Order;

import java.util.Random;
/**
 * Utility class for Json and waiting a time in a thread
 * @author BrahianVT
 * **/
public class Utilities {

    /**
     * Method to generate a random time between 2- 6 seconds
     * @param: Order
     * @return Order
     * */
    public static Order waitingCourier(Order o){
        Random random = new Random();
        try {
            int range = random.nextInt(6 - 2 + 1) + 2;
            Thread.sleep(range  * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * Method to generate a random time in seconds
     * @param: s are seconds to wait
     * */
    public static void pause(int s){
        try {
            Thread.sleep(s  * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to filter by shelf life a order
     * @param: An Order
     * @param: A shelfDecayModifier value
     * @return a boolean variable
     * */
    public static boolean byShelfLife(Order o, int shelfDecayModifier){
        float aux = o.calculateRemainingTime(shelfDecayModifier);
        if( aux < 0){
            o.finishTime();
            System.out.println("Discarting due to shelfLife: " + aux );
            return false;
        }
        return true;
    }


}
