package com.demo.upimesh;

import com.demo.upimesh.service.VirtualDevice;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class VirtualDeviceTest {
    @Test
    void togglesReportTheNewConnectivityState() {
        VirtualDevice phone = new VirtualDevice("phone", false);
        assertTrue(phone.toggleInternet());
        assertTrue(phone.hasInternet());
        assertFalse(phone.toggleInternet());
        assertFalse(phone.hasInternet());
    }

    @Test
    void concurrentTogglesDoNotLoseUpdates() throws Exception {
        VirtualDevice phone = new VirtualDevice("phone", false);
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<?>> tasks = new ArrayList<>();
            for (int worker = 0; worker < 8; worker++) {
                tasks.add(pool.submit(() -> {
                    start.await();
                    for (int i = 0; i < 1001; i++) phone.toggleInternet();
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> task : tasks) task.get(10, TimeUnit.SECONDS);
            assertFalse(phone.hasInternet(), "An even number of toggles restores the initial state");
        } finally {
            pool.shutdownNow();
        }
    }
}
