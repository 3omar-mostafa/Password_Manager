package com.hafez.password_manager;

import static org.junit.Assert.assertTrue;

import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataUtils {

    public static <T> T getValueOf(LiveData<T> liveData) {

        ArrayList<T> list = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(1);

        TestObserver<T> observer = new TestObserver<T>() {
            @Override
            public void onChangedBehaviour(T t) {
                list.add(t);
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        assertTrue(observer.isOnChangedCalled());

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return list.get(0);
    }

}
