package org.example;


import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.example.Entity.Order;
import org.example.Processors.Kitchen;
import org.example.Utilities.ReadJson;
import org.junit.jupiter.api.*;

import java.util.List;

public class KitchenTest {
    Kitchen kitchen;
    List<Order> orders;
    ReadJson read = new ReadJson();
    @BeforeEach
    public void setup(){
        orders = read.loadJsonFile("src/test/resources/test.json");
        kitchen = new Kitchen();
    }

    @Test
    public void testFilterByHotTemp(){
        System.out.println("***********: " + orders == null);
        System.out.println("***********: " + orders);

        Order[] hotOrders = orders.stream().
                filter(s -> s.getTemp().equals("hot")).toArray(Order[]::new);


        TestSubscriber<Order> subscriber = new TestSubscriber<>();
        Flowable.fromArray(hotOrders)
                .filter(s -> kitchen.hotTemp(s))
                .subscribe(subscriber);


        subscriber.assertResult(hotOrders);
        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(2);
    }

    @Test
    public void testFilterByColdTemp(){
        Order[] coldOrders = orders.stream().
                filter(s -> s.getTemp().equals("cold")).toArray(Order[]::new);

        TestSubscriber<Order> subscriber = new TestSubscriber<>();
        Flowable.fromArray(coldOrders)
                .filter(s -> kitchen.coldTemp(s))
                .subscribe(subscriber);

        subscriber.assertResult(coldOrders);
        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(6);
    }

    @Test
    public void testFilterByFrozenTemp(){
        Order[] frozenOrders = orders.stream().
                filter(s -> s.getTemp().equals("frozen")).toArray(Order[]::new);


        TestSubscriber<Order> subscriber = new TestSubscriber<>();
        Flowable.fromArray(frozenOrders)
                .filter(s -> kitchen.frozenTemp(s))
                .subscribe(subscriber);

        subscriber.assertResult(frozenOrders);
        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(5);
    }
}
