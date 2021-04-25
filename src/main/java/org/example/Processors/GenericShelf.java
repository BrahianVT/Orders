package org.example.Processors;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 *  Class to represents a Generic shelf the key part is the FlowableProcessor it
 *  will delivery an order or discard it.
 * @author BrahianVT
 * */
public class GenericShelf {
    private static Logger logger = LogManager.getLogger(GenericShelf.class);
    private FlowableProcessor<Order> genericShelf;
    public GenericShelf(){
        genericShelf = PublishProcessor.<Order>create().toSerialized();
    }
    public FlowableProcessor<Order> getGenericShelf(){ return genericShelf;  }
    public void setGenericShelf(FlowableProcessor<Order> gs){ genericShelf = gs; }

    /**
     * onDrop discard a element from the generic shelf
     * */
    public void onDrop(Order o){
        o.finishTime();
        logger.debug("dropping from  generic shelf ");
    }

    /**
     * This method return the subscriber's implementation to subscribe
     * on the generic shelf and delivery the messages to the courier or discard them
     * @return Subscriber element
     * */
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
                logger.debug(" Order in Generic shelf delivered! ");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                logger.debug("Delivered all orders G.");
            }
        };
    }
}
