package com.igevin.trywithlock.usage;

import com.igevin.trywithlock.closeable.AutoCloseableLock;
import com.igevin.trywithlock.lambda.AutoReleaseLockHolder;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class VisitCounter {
    private long visits = 0;
    private final AtomicLong atomicVisits = new AtomicLong(0);
    private final Lock lock = new ReentrantLock();
    private final AutoCloseableLock autoCloseableLock = new AutoCloseableLock(lock);

    private final AutoReleaseLockHolder lockHolder = new AutoReleaseLockHolder(lock);

    public void visit() {
        visits++;
    }

    public void atomicVisit() {
        atomicVisits.getAndIncrement();
    }

    public void safeVisit() {
        try {
            lock.lock();
            visits++;
        } finally {
            lock.unlock();
        }
    }

    public void safeVisit2() {
        try (AutoCloseableLock autoCloseableLock = new AutoCloseableLock(lock)) {
            autoCloseableLock.lock();
            visits++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void safeVisit2() {
//        // works after jdk9
//        try (autoCloseableLock) {
//            autoCloseableLock.lock();
//            visits++;
//        }
//    }

    public void safeVisit3() {
        lockHolder.runWithLock(() -> visits++);
    }

}
