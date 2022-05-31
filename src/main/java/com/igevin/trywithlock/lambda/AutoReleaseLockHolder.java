package com.igevin.trywithlock.lambda;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class AutoReleaseLockHolder {
    private final Lock lock;

    public AutoReleaseLockHolder(Lock lock) {
        this.lock = lock;
    }

    public void runWithLock(Runnable runnable) {
        try {
            lock.lock();
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public boolean runWithTryLock(Runnable runnable) {
        try {
            boolean locked = lock.tryLock();
            if (!locked) {
                return false;
            }
            runnable.run();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean runWithTryLock(Runnable runnable, long time, TimeUnit timeUnit) throws InterruptedException {
        try {
            boolean locked = lock.tryLock(time, timeUnit);
            if (!locked) {
                return false;
            }
            runnable.run();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void runWithLockInterruptibly(Runnable runnable) throws InterruptedException {
        try {
            lock.lockInterruptibly();
            runnable.run();
        } finally {
            lock.unlock();
        }
    }


    public <T> T callWithLock(Callable<T> callable) throws Exception {
        try {
            lock.lock();
            return callable.call();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            lock.unlock();
        }
    }
}
