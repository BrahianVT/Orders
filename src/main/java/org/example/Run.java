package org.example;

import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.Subject;
import org.example.Entity.Order;
import org.example.Processors.*;
import org.example.Utilities.ReadJson;
import org.example.Utilities.Utilities;

import java.util.List;

public class Run {
    public void run(){
        ReadJson read = new ReadJson();
        List<Order> list = read.loadJsonFile("orders.json");

        Kitchen createKitchen = new Kitchen();
        HotShelf createHotShelf = new HotShelf();
        ColdShelf createColdShelf = new ColdShelf();
        FrozenShelf createFrozenShelf = new FrozenShelf();
        GenericShelf createGenericShelf = new GenericShelf();

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

         kitchen.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .filter(s -> createKitchen.coldTemp(s))
                .subscribe(createKitchen.subscribeColdTemp());


        hotShelf
                .onBackpressureDrop(s -> createHotShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> { System.out.println("Waiting for the courier on hot shelf.."); return Utilities.waitingCourier(s);
                })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(createHotShelf.subscribeHotShelf());


        coldShelf
                .onBackpressureDrop(s -> createColdShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> { System.out.println("Waiting for the courier on cold shelf.."); return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(createColdShelf.subscribeColdShelf());

        frozenShelf
                .onBackpressureDrop(s -> createFrozenShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 10)
                .map(s -> { System.out.println("Waiting for the courier on frozen shelf.."); return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 1))
                .subscribe(createFrozenShelf.subscribeFrozenShelf());

        genericShelf
                .onBackpressureDrop(s -> createGenericShelf.onDrop(s))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 20)
                .map(s -> { System.out.println("Waiting for the courier on generic shelf.."); return Utilities.waitingCourier(s); })
                .filter(s -> Utilities.byShelfLife(s, 2))
                .subscribe(createGenericShelf.subscribeGenericShelf());



        int elementsPerSecond = 5;
        System.out.println("-----------------Array size: " + list.size());
        for(int i = 1; i <= list.size(); i++){
            System.out.println("i: " + i + ", "+ list.get(i-1).getId());
            kitchen.onNext(list.get(i-1));
            if(i % elementsPerSecond == 0) {
               Utilities.pause(1);
            }
        }

        kitchen.onComplete();
        boolean a = false;
        while(!genericShelf.hasComplete()){
            a = true;
        }

    }

}