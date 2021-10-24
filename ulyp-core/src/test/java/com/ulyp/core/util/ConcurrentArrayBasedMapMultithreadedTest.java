package com.ulyp.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ConcurrentArrayBasedMapMultithreadedTest {

    static {
        System.setProperty("ConcurrentArrayBasedMap.BITS", "3");
    }

    @Test
    public void testPutAndGetSingleChunk() throws InterruptedException {
        int threads = 4;
        int puts = 1_000_000;
        ConcurrentArrayBasedMap<Integer> map = new ConcurrentArrayBasedMap<>(
                (threads + 1) * puts / 8
        );
        CountDownLatch countDownLatch = new CountDownLatch(threads + 1);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<long[]>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            int threadOffset = i;
            futures.add(executorService.submit(
                    () -> {
                        long[] keys = new long[puts];

                        countDownLatch.countDown();
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            // NOP
                        }

                        for (int j = 0; j < puts; j++) {
                            int value = j * (threads + 1) + threadOffset;
                            keys[j] = map.put(value);
                        }

                        return keys;
                    }
            ));
        }

        countDownLatch.countDown();

        for (int threadOffset = 0; threadOffset < futures.size(); threadOffset++) {
            try {
                Future<long[]> future = futures.get(threadOffset);
                long[] keys = future.get();

                for (int j = 0; j < keys.length; j++) {
                    int actualValue = map.get(keys[j]);
                    int expectedValue = j * (threads + 1) + threadOffset;
                    Assert.assertEquals(expectedValue, actualValue);
                }
            } catch (Exception e) {
                Assert.fail("Test failed: " + e.getMessage());
            }
        }

        executorService.shutdownNow();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }
}