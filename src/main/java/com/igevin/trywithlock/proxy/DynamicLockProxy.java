package com.igevin.trywithlock.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.locks.Lock;

public class DynamicLockProxy {
    private final Lock lock;

    public DynamicLockProxy(Lock lock) {
        this.lock = lock;
    }

    private Object createLockProxy(Object target, InvocationHandler handler) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, handler);
    }

    public Object createAutoLockProxy(Object target) {
        return createLockProxy(target, new AutoLockHandler(target));
    }

    public Object createAutoLockProxy2(Object target) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, (proxy, method, args) -> {
            try {
                lock.lock();
                return method.invoke(target, args);
            } finally {
                lock.unlock();
            }
        });
    }

    public Object createAutoLockWithInterruptiblyProxy(Object target) {
        return createLockProxy(target, new AutoLockWithInterruptiblyHandler(target));
    }

    private class AutoLockHandler implements InvocationHandler {
        private final Object target;

        private AutoLockHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                lock.lock();
                return method.invoke(target, args);
            } finally {
                lock.unlock();
            }
        }
    }

    private class AutoLockWithInterruptiblyHandler implements InvocationHandler {
        private final Object target;

        private AutoLockWithInterruptiblyHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                lock.lockInterruptibly();
                return method.invoke(target, args);
            } finally {
                lock.unlock();
            }
        }
    }
}
