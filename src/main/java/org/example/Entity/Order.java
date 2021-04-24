package org.example.Entity;
/**
 * Entity class that represents an Order
 * @author BrahianVT
 * */
public class Order {
    private String id;
    private String name;
    private String temp;
    private int shelfLife;
    private float decayRate;
    public  float orderAge;
    private Thread lifeRemainig;
    private boolean run = true;
    public Order(){}
    public Order(String id, String name, String temp, int shelfLife, float decayRate) {
        this.id = id;
        this.name = name;
        this.temp = temp;
        this.shelfLife = shelfLife;
        this.decayRate = decayRate;
    }

    public float calculateRemainingTime(int shelfDecayModifier){
        return  shelfLife - orderAge - decayRate * orderAge * shelfDecayModifier /shelfLife;
    }
    public void initTime(){
         Runnable taskOrderAge  = () -> {
             try {
                 while(run){ orderAge++;  Thread.sleep(1000); }
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         };
        lifeRemainig = new Thread(taskOrderAge);
        lifeRemainig.start();
    }

    public void finishTime(){ this.run = false; }
    public String getId() { return id;  }

    public void setId(String id) {  this.id = id;  }

    public String getName() {  return name;  }

    public void setName(String name) { this.name = name; }

    public String getTemp() { return temp; }

    public void setTemp(String temp) { this.temp = temp; }

    public int getShelfLife() { return shelfLife; }

    public void setShelfLife(int shelfLife) { this.shelfLife = shelfLife; }

    public double getDecayRate() { return decayRate; }

    public void setDecayRate(float decayRate) { this.decayRate = decayRate; }
}
