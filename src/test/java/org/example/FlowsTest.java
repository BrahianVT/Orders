package org.example;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.Subject;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.example.Entity.Order;
import org.example.Processors.*;
import org.example.Utilities.ReadJson;
import org.example.Utilities.Utilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;


public class FlowsTest {
    Kitchen kitchen;
    List<Order> orders;
    ReadJson read = new ReadJson();
    Kitchen createKitchen;
    HotShelf createHotShelf;
    ColdShelf createColdShelf;
    GenericShelf createGenericShelf;
    FrozenShelf createFrozenShelf;


    @BeforeEach
    void getOrders(){
        createKitchen = new Kitchen();
        createHotShelf = new HotShelf();
        createColdShelf = new ColdShelf();
        createFrozenShelf = new FrozenShelf();
        createGenericShelf = new GenericShelf();
        orders = read.loadJsonFile("src/test/resources/test.json");
    }

    @Test
    void basicflow(){
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

        // creating flow
        kitchen.filter(s -> createKitchen.hotTemp(s)).subscribe(createKitchen.subscribeHotTemp());
        kitchen.filter(s -> createKitchen.frozenTemp(s)).subscribe(createKitchen.subscribeFrozenTemp());
        kitchen.filter(s -> createKitchen.coldTemp(s)).subscribe(createKitchen.subscribeColdTemp());

        TestSubscriber<Order> hotSubscriber = new TestSubscriber<>();
        TestSubscriber<Order> coldSubscriber = new TestSubscriber<>();
        TestSubscriber<Order> frozenSubscriber = new TestSubscriber<>();

        hotShelf
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(hotSubscriber);


        coldShelf
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(coldSubscriber);

        frozenShelf
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(frozenSubscriber);


        for(int i = 1; i <= orders.size(); i++){
            kitchen.onNext(orders.get(i-1));
        }
        Order[] hotOrders = orders.stream().filter(s -> s.getTemp().equals("hot")).toArray(Order[]::new);
        Order[] coldOrders = orders.stream().filter(s -> s.getTemp().equals("cold")).toArray(Order[]::new);
        Order[] frozenOrders = orders.stream().filter(s -> s.getTemp().equals("frozen")).toArray(Order[]::new);

        hotSubscriber.assertValues(hotOrders);
        hotSubscriber.assertNoErrors();
        hotSubscriber.assertValueCount(2);

        coldSubscriber.assertValues(coldOrders);
        coldSubscriber.assertNoErrors();
        coldSubscriber.assertValueCount(6);

        frozenSubscriber.assertValues(frozenOrders);
        frozenSubscriber.assertNoErrors();
        frozenSubscriber.assertValueCount(5);

        hotSubscriber.onComplete();
        coldSubscriber.onComplete();
        frozenSubscriber.onComplete();

    }

    @Test
    public void complexFLow(){
        orders = read.loadJsonFile("src/test/resources/test2.json");
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

        // creating flow
        kitchen
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.hotTemp(s))
                .subscribe(createKitchen.subscribeHotTemp());

        kitchen
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.frozenTemp(s))
                .subscribe(createKitchen.subscribeFrozenTemp());

        kitchen
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.coldTemp(s))
                .subscribe(createKitchen.subscribeColdTemp());

        TestSubscriber<Order> hotSubscriber = new TestSubscriber<>();
        TestSubscriber<Order> coldSubscriber = new TestSubscriber<>();
        TestSubscriber<Order> frozenSubscriber = new TestSubscriber<>();
        TestSubscriber<Order> genericSubscriber = new TestSubscriber<>();

        hotShelf
                .onBackpressureDrop(s -> createHotShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> { return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(hotSubscriber);


        coldShelf
                .onBackpressureDrop(s -> createColdShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> {  return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(coldSubscriber);

        frozenShelf
                .onBackpressureDrop(s -> createFrozenShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> {  return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(frozenSubscriber);

        genericShelf
                .onBackpressureDrop(s -> createGenericShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 20)
                .map(s -> {  return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 2))
                .subscribe(genericSubscriber);



        for(int i = 1; i <= orders.size(); i++){ kitchen.onNext(orders.get(i-1));  }

        Utilities.pause(orders.size() * 5);

        hotSubscriber.assertNoErrors();
        hotSubscriber.assertValueCount(10);

        coldSubscriber.assertNoErrors();
        coldSubscriber.assertValueCount(6);

        frozenSubscriber.assertNoErrors();
        frozenSubscriber.assertValueCount(10);

        hotSubscriber.onComplete();
        coldSubscriber.onComplete();
        frozenSubscriber.onComplete();

        hotShelf.onComplete();
        coldShelf.onComplete();
        frozenShelf.onComplete();
    }
}
