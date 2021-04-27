package org.example;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.Subject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;
import org.example.Processors.*;
import org.example.Utilities.ReadJson;
import org.example.Utilities.Utilities;

import java.util.List;

/**
 *  Class where the components interacts between each other to delivery orders
 * @author BrahianVT
 * */
public class Run {
    private static Logger logger = LogManager.getLogger(Run.class);
    /**
     * Method where the components interacts between each other to delivery orders
     * */
    public void run(){
        logger.debug("run ...");
        ReadJson read = new ReadJson();
        List<Order> list = read.loadJsonFile("orders.json");

        Kitchen createKitchen = new Kitchen();
        HotShelf createHotShelf = new HotShelf();
        ColdShelf createColdShelf = new ColdShelf();
        FrozenShelf createFrozenShelf = new FrozenShelf();
        GenericShelf createGenericShelf = new GenericShelf();

        // this components are the elements sending elements
        // each can receive and emit elements
        // As every element is in a single thread I implemented as thread safe
        Subject<Order> kitchen = createKitchen.getKitchen();
        FlowableProcessor<Order> hotShelf = createHotShelf.getHotShelf();
        FlowableProcessor<Order> coldShelf = createColdShelf.getColdShelf();
        FlowableProcessor<Order> frozenShelf = createFrozenShelf.getFrozenShelf();
        FlowableProcessor<Order> genericShelf = createGenericShelf.getGenericShelf();

        createKitchen.setHotShelf(hotShelf);
        createKitchen.setColdShelf(coldShelf);
        createKitchen.setFrozenShelf(frozenShelf);

        createHotShelf.setGenericShelf(genericShelf);
        createColdShelf.setGenericShelf(genericShelf);
        createFrozenShelf.setGenericShelf(genericShelf);

        // Implement the subscribe method so process the next elements filtering by hot temp in a different thread.
        kitchen
                .observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.hotTemp(s))
                .subscribe(createKitchen.subscribeHotTemp());

        // Implement the subscribe method so process the next elements filtering by frozen temp in a different thread..
        kitchen
                .observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.frozenTemp(s))
                .subscribe(createKitchen.subscribeFrozenTemp());

        // Implement the subscribe method so process the next elements filtering by cold temp in a different thread..
         kitchen.observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.coldTemp(s))
                .subscribe(createKitchen.subscribeColdTemp());


        /* Hot shelf that receive the order it Implement the subscribe method to simulate the delivery waiting for the courier
         also filtering by shelf life, the size for the buffer is the among specify in the input arguments
         by default 10 when this is full it sends to generic shelf. */
        hotShelf
                .onBackpressureDrop(s -> createHotShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, Main.BUFFER_SIZE_SHELF)
                .map(s -> { logger.debug("Waiting for the courier on hot shelf.."); return Utilities.waitingCourier(s);
                })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(createHotShelf.subscribeHotShelf());


        /* Cold shelf that receive the order it Implement the subscribe method to simulate the delivery waiting for the courier
         also filtering by shelf life, the size for the buffer is the among specify in the input arguments
         by default 10 when this is full it sends to generic shelf. */
        coldShelf
                .onBackpressureDrop(s -> createColdShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, Main.BUFFER_SIZE_SHELF)
                .map(s -> { logger.debug("Waiting for the courier on cold shelf.."); return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(createColdShelf.subscribeColdShelf());

        /* Frozen shelf that receive the order it Implement the subscribe method to simulate the delivery waiting for the courier
         also filtering by shelf life, the size for the buffer is the among specify in the input arguments
         by default 10 when this is full it sends to generic shelf. */
        frozenShelf
                .onBackpressureDrop(s -> createFrozenShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, Main.BUFFER_SIZE_SHELF)
                .map(s -> { logger.debug("Waiting for the courier on frozen shelf.."); return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(createFrozenShelf.subscribeFrozenShelf());

        /* Generic shelf that receive the order it Implement the subscribe method to simulate the delivery waiting for the courier
         also filtering by shelf life, the size for the buffer is the among specify in the input arguments
         by default 15 when this is full it drops the order */
        genericShelf
                .onBackpressureDrop(s -> createGenericShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, Main.BUFFER_SIZE_GENERIC_SHELF)
                .map(s -> { logger.debug("Waiting for the courier on generic shelf.."); return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 2))
                .subscribe(createGenericShelf.subscribeGenericShelf());


        /*
        * Section that simulate elements/requests  per second defined by ELEMENTS_PER_SECOND the first input Argument.
        *
        * Total threads   8 threads
        * */
        for(int i = 1; i <= list.size(); i++){
            logger.debug("i " + i + " "+ list.get(i-1).getId());
            kitchen.onNext(list.get(i-1));
            if(i % Main.ELEMENTS_PER_SECOND == 0) {
               Utilities.pause(1);
            }
        }

        kitchen.onComplete();
        // Block  to finish process the orders
        genericShelf.isEmpty().blockingGet();

    }

}
