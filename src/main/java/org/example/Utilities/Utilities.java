package org.example.Utilities;

import org.example.Entity.Order;

import java.util.Random;

public class Utilities {
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

    public static void pause(int s){
        try {
            Thread.sleep(s  * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
