package com.igevin.trywithlock.closeable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class AutoCloseableLock implements AutoCloseable{
    private final Lock lock;

    public AutoCloseableLock(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void close() throws Exception {
        lock.unlock();
    }

    public void lock() {
        lock.lock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public boolean tryLock(long time, TimeUnit timeUnit) throws InterruptedException {
        return lock.tryLock(time, timeUnit);
    }

    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }
}
