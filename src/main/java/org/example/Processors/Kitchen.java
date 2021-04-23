package org.example.Processors;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


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



    public DisposableObserver<Order> subscribeHotTemp(){
        DisposableObserver<Order> o =  new DisposableObserver<Order>() {

            @Override
            public void onNext(Order order) {
                order.initTime();
                System.out.println("Sent to hot shelf " );
                hotShelf.onNext(order);
            }
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("--- Processed all Hot Temperature Orders");
            }
        };
        return o;
    }

    public DisposableObserver<Order> subscribeColdTemp(){
        return new DisposableObserver<Order>() {
            @Override
            public void onNext(Order order) {
                order.initTime();
                System.out.println("Sent to cold shelf " );
                coldShelf.onNext(order);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("--- Processed all Cold Temperature Orders");
            }
        };
    }

    public DisposableObserver<Order> subscribeFrozenTemp(){
        return new DisposableObserver<Order>() {
            @Override
            public void onNext(Order order) {
                System.out.println("Sent to frozen shelf " );
                order.initTime();
                frozenShelf.onNext(order);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("--- Processed all Cold Temperature Orders");
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
