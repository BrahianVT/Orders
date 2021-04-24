package org.example.Processors;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 *  Class to represents a hot shelf the key part is the FlowableProcessor it
 *  will either emit orders or send them to the generic shelf.
 * @author BrahianVT
 * */
public class HotShelf {
    private static Logger logger = LogManager.getLogger(HotShelf.class);
    private FlowableProcessor<Order> genericShelf;
    FlowableProcessor<Order> hotShelf;

    public  HotShelf(){
        hotShelf = PublishProcessor.<Order>create().toSerialized();
    }

    public FlowableProcessor<Order> getGenericShelf(){ return genericShelf;  }

    public void setGenericShelf(FlowableProcessor<Order> gs){ genericShelf = gs; }

    public FlowableProcessor<Order> getHotShelf(){ return hotShelf;  }

    /**
     *
     * onDrop discard a element from the hot shelf and delivers to the generic shelf
     * */
    public  void onDrop(Order o){
        System.out.println("HotShelf full, sent to  generic Shelf");
        genericShelf.onNext(o);
    }

    /**
     * This method return the subscriber's implementation to subscribe
     * on the hot shelf and delivery the messages to the courier or discard them
     * @return Subscriber element
     * */
    public Subscriber<Order> subscribeHotShelf(){
        return new Subscriber<Order>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Order order) {
                order.finishTime();
                System.out.println("Hot Order delivered! ");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Delivered all orders H.");
            }
        };
    }




}
