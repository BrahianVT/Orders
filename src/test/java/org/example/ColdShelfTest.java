package org.example;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.example.Entity.Order;
import org.example.Processors.ColdShelf;
import org.example.Processors.GenericShelf;
import org.example.Utilities.Utilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class ColdShelfTest {
    Order[] orders;
    @BeforeEach
    public void setup(){
        orders = new Order[15];
        String id  = "0"; float decayRate = .8f; int shelfLife = 230;
        for(int i = 0; i < 15; i++){
            orders[i] = new Order(id,null,"cold",shelfLife,decayRate);
        }
    }

    @Test
    public void TestBufferSize(){
        TestSubscriber<Order> coldTempSub = new TestSubscriber<>();

        ColdShelf createColdShelf = new ColdShelf();
        FlowableProcessor<Order> coldShelf = createColdShelf.getColdShelf();
        GenericShelf createGenericShelf = new GenericShelf();
        FlowableProcessor<Order> genericShelf = createGenericShelf.getGenericShelf();
        createColdShelf.setGenericShelf(genericShelf);

        coldShelf
                .onBackpressureDrop(s -> createColdShelf.onDrop(s))
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> {  Utilities.pause(1); return s; })
                .subscribe(coldTempSub);

        for(int i = 0; i < orders.length; i++){ coldShelf.onNext(orders[i]);}

        Utilities.pause(12);
        coldTempSub.assertNoErrors();
        coldTempSub.assertValueCount(10);
        coldTempSub.onComplete();
    }
}
