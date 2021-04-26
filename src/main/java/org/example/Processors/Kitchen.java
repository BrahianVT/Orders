package org.example.Processors;

import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;


/**
 *  Class to represents a kitchen the key part is the Subject it
 *  will emit orders and also will send orders.
 * @author BrahianVT
 * */
public  class  Kitchen {
    private static Logger logger = LogManager.getLogger(Kitchen.class);
    Subject<Order> kitchen;
    FlowableProcessor<Order> hotShelf;
    FlowableProcessor<Order> coldShelf;
    FlowableProcessor<Order> frozenShelf;
    public Kitchen(){
        kitchen = PublishSubject.<Order>create().toSerialized();
    }

    public Subject<Order> getKitchen(){ return kitchen; }
    public boolean hotTemp(Order temp){ return temp.getTemp().equals("hot")? true:false; }
    public boolean coldTemp(Order temp){
        return temp.getTemp().equals("cold")? true:false;
    }
    public boolean frozenTemp(Order temp){ return temp.getTemp().equals("frozen")? true:false; }


    /**
     * This method return the DisposableObserver's implementation to subscribe
     * on the kitchen to filter and emit elements to the hot shelf
     * @return DisposableObserver element
     * */
    public DisposableObserver<Order> subscribeHotTemp(){
        DisposableObserver<Order> o =  new DisposableObserver<Order>() {

            @Override
            public void onNext(Order order) {
                order.initTime();
                logger.debug("Sent to hot shelf " );
                hotShelf.onNext(order);
            }
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                logger.debug("--- Processed all Hot Temperature Orders");
            }
        };
        return o;
    }

    /**
     * This method return the DisposableObserver's implementation to subscribe
     * on the kitchen to filter and emit elements to the cold shelf
     * @return DisposableObserver element
     * */

    public DisposableObserver<Order> subscribeColdTemp(){
        return new DisposableObserver<Order>() {
            @Override
            public void onNext(Order order) {
                order.initTime();
                logger.debug("Sent to cold shelf " );
                coldShelf.onNext(order);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                logger.debug("--- Processed all Cold Temperature Orders");
            }
        };
    }

    /**
     * This method return the DisposableObserver's implementation to subscribe
     * on the kitchen to filter and emit elements to the frozen shelf
     * @return DisposableObserver element
     * */
    public DisposableObserver<Order> subscribeFrozenTemp(){
        return new DisposableObserver<Order>() {
            @Override
            public void onNext(Order order) {
                logger.debug("Sent to frozen shelf " );
                order.initTime();
                frozenShelf.onNext(order);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                logger.debug("--- Processed all Frozen Temperature Orders");
            }
        };
    }

    public FlowableProcessor<Order> getHotShelf(){ return hotShelf;  }
    public void setHotShelf(FlowableProcessor<Order> hs){ hotShelf = hs; }

    public FlowableProcessor<Order> getColdShelf(){ return coldShelf;  }
    public void setColdShelf(FlowableProcessor<Order> cs){ coldShelf = cs; }

    public FlowableProcessor<Order> getFrozenShelf(){ return frozenShelf;  }
    public void setFrozenShelf(FlowableProcessor<Order> fs){ frozenShelf = fs; }
}
