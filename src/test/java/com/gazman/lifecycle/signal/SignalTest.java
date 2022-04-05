package com.gazman.lifecycle.signal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

public class SignalTest {

    @Test
    public void testDispatch() {
        AtomicInteger foods = new AtomicInteger();
        Signal<EatSignal> eatSignal = SignalsBag.inject(EatSignal.class);

        eatSignal.addListener(foods::incrementAndGet);
        eatSignal.addListener(foods::incrementAndGet);

        eatSignal.dispatcher.onEat();

        Assert.assertEquals(2, foods.get());
    }

    @Test
    public void testDispatchMultithreaded() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AtomicInteger foods = new AtomicInteger();
        Signal<EatSignal> eatSignal = SignalsBag.inject(EatSignal.class);

        for (int i = 0; i < 10; i++) {
            eatSignal.addListener(foods::incrementAndGet);
        }

        for (int i = 0; i < 10; i++) {
            eatSignal.dispatcher.onEat();
        }
        Assert.assertEquals(100, foods.get());

        for (int i = 0; i < 10; i++) {
            executorService.execute(eatSignal.dispatcher::onEat);
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(200, foods.get());
    }

    private interface EatSignal {
        void onEat();
    }
}