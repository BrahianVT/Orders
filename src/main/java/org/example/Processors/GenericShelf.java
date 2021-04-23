package org.example.Processors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class GenericShelf {
    private static Logger logger = LogManager.getLogger(ColdShelf.class);
    private FlowableProcessor<Order> genericShelf;
    public GenericShelf(){
        genericShelf = PublishProcessor.<Order>create().toSerialized();
    }
    public FlowableProcessor<Order> getGenericShelf(){ return genericShelf;  }
    public void setGenericShelf(FlowableProcessor<Order> gs){ genericShelf = gs; }

    public void onDrop(Order o){
        o.finishTime();
        System.out.println("dropping from  generic shelf ");
    }

    public Subscriber<Order> subscribeGenericShelf(){
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
                System.out.println(" Order in Generic shelf delivered! ");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Delivered all orders G.");
            }
        };
    }
}
