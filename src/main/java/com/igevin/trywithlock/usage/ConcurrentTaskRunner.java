package com.igevin.trywithlock.usage;

import com.igevin.trywithlock.proxy.CglibLockProxy;
import com.igevin.trywithlock.proxy.DynamicLockProxy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentTaskRunner {
    private final ExecutorService executor;
    private VisitCounter visitCounter;
    private final static int core = Runtime.getRuntime().availableProcessors();

    public ConcurrentTaskRunner() {
        this(createExecutor());
    }

    public ConcurrentTaskRunner(ExecutorService executor) {
        this.executor = executor;
        visitCounter = new VisitCounter();
    }

    public ConcurrentTaskRunner resetVisitCounter() {
        this.visitCounter = new VisitCounter();
        return this;
    }

    private static ExecutorService createExecutor() {

        return Executors.newFixedThreadPool(core + 1);
    }

    public void oneThreadVisitCount() throws InterruptedException {
        long total = 20000;
        int current = 1;
        concurrentVisit(total, current, visitCounter::visit);
    }

    public void unsafeVisitCount() throws InterruptedException {
        long total = 20000;
        int current = 10;
        concurrentVisit(total, current, visitCounter::visit);
    }

    public void safeVisitCountWithDynamicProxy() throws InterruptedException {
        long total = 20000;
        int current = 10;
        IVisitCounter visit = (IVisitCounter )new DynamicLockProxy(new ReentrantLock()).createAutoLockProxy2(visitCounter);
        concurrentVisit(total, current, visit::visit);
        System.out.println("actual visits: " + visit.getVisits());
    }

    public void safeVisitCountWithCglib() throws InterruptedException {
        long total = 20000;
        int current = 10;
        Lock lock = new ReentrantLock();
        VisitCounter visitCounterProxy = CglibLockProxy.createAutoLockObject(VisitCounter.class, lock);
        concurrentVisit(total, current, visitCounterProxy::visit);
        System.out.println("actual visits: " + visitCounterProxy.getVisits());
    }

    public void safeVisitCount() throws InterruptedException {
        long total = 20000;
        int current = 10;
        concurrentVisit(total, current, visitCounter::safeVisit);
    }

    public void safeVisitCount2() throws InterruptedException {
        long total = 20000;
        int current = 10;
        concurrentVisit(total, current, visitCounter::safeVisit2);
    }

    public void safeVisitCount3() throws InterruptedException {
        long total = 20000;
        int current = 10;
        concurrentVisit(total, current, visitCounter::safeVisit3);
    }

    public void atomicVisitCount() throws InterruptedException {
        long total = 20000;
        int current = 10;
        concurrentVisit(total, current, visitCounter::atomicVisit);
        System.out.println("actual visits: " + visitCounter.getAtomicVisits().get());
    }


    private void concurrentVisit(long total, int current, Runnable runnable) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(current);
        for (int i = 0; i < current; i++) {
            long batch = total / current;
            executor.execute(createRunnableTask(runnable, batch, latch));
        }
        latch.await();

        System.out.println("total: " + total + " visits: " + visitCounter.getVisits());
    }


    private Runnable createVisitTask(VisitCounter visitCounter, long batch, CountDownLatch latch) {
        return createRunnableTask(visitCounter::visit, batch, latch);
    }

    private Runnable createRunnableTask(Runnable runnable, long batch, CountDownLatch latch) {
        return () -> {
            for (int i = 0; i < batch; i++) {
                runnable.run();
            }
            latch.countDown();
        };
    }
}
