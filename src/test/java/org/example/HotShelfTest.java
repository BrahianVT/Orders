package org.example;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.example.Entity.Order;
import org.example.Processors.GenericShelf;
import org.example.Processors.HotShelf;
import org.example.Utilities.Utilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class HotShelfTest {

    Order[] orders;
    @BeforeEach
    public void setup(){
        orders = new Order[15];
        String id  = "0"; float decayRate = .8f; int shelfLife = 230;
        for(int i = 0; i < 15; i++){
            orders[i] = new Order(id,null,"hot",shelfLife,decayRate);
        }
    }

    @Test
    public void TestBufferSize(){
        TestSubscriber<Order> hotTempSub = new TestSubscriber<>();

        HotShelf createHotShelf = new HotShelf();
        FlowableProcessor<Order> hotShelf = createHotShelf.getHotShelf();
        GenericShelf createGenericShelf = new GenericShelf();
        FlowableProcessor<Order> genericShelf = createGenericShelf.getGenericShelf();
        createHotShelf.setGenericShelf(genericShelf);

        hotShelf
                .onBackpressureDrop(s -> createHotShelf.onDrop(s))
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> {  Utilities.pause(1); return s; })
                .subscribe(hotTempSub);

        for(int i = 0; i < orders.length; i++){ hotShelf.onNext(orders[i]);}

        Utilities.pause(12);
        hotTempSub.assertNoErrors();
        hotTempSub.assertValueCount(10);
        hotTempSub.onComplete();



    }

}
