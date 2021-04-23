package org.example;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.example.Entity.Order;
import org.example.Processors.FrozenShelf;
import org.example.Processors.GenericShelf;
import org.example.Utilities.Utilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class FrozenShelfTest {
    Order[] orders;

    @BeforeEach
    public void setup(){
        orders = new Order[15];
        String id  = "0"; float decayRate = .8f; int shelfLife = 230;
        for(int i = 0; i < 15; i++){
            orders[i] = new Order(id,null,"frozen",shelfLife,decayRate);
        }
    }

    @Test
    public void TestBufferSize(){
        TestSubscriber<Order> frozenTempSub = new TestSubscriber<>();

        FrozenShelf createFrozenShelf = new FrozenShelf();
        FlowableProcessor<Order> frozenShelf = createFrozenShelf.getFrozenShelf();
        GenericShelf createGenericShelf = new GenericShelf();
        FlowableProcessor<Order> genericShelf = createGenericShelf.getGenericShelf();
        createFrozenShelf.setGenericShelf(genericShelf);

        frozenShelf
                .onBackpressureDrop(s -> createFrozenShelf.onDrop(s))
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> {  Utilities.pause(1); return s; })
                .subscribe(frozenTempSub);

        for(int i = 0; i < orders.length; i++){ frozenShelf.onNext(orders[i]);}

        Utilities.pause(12);
        frozenTempSub.assertNoErrors();
        frozenTempSub.assertValueCount(10);
        frozenTempSub.onComplete();
    }
}
