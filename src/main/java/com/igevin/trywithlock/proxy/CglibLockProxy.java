package com.igevin.trywithlock.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.concurrent.locks.Lock;

public class CglibLockProxy {
    private static <T> T createLockProxyObject(Class<T> objectClass, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(objectClass);
        enhancer.setCallback(interceptor);
        return (T) enhancer.create();
    }

    public static <T> T createAutoLockObject(Class<T> objectClass, Lock lock) {
        MethodInterceptor interceptor = createLockInterceptor(lock);
        return createLockProxyObject(objectClass, interceptor);
    }

    public static <T> T createAInterruptibleLockObject(Class<T> objectClass, Lock lock) {
        MethodInterceptor interceptor = createInterruptibleLockInterceptor(lock);
        return createLockProxyObject(objectClass, interceptor);
    }

    private static MethodInterceptor createLockInterceptor(Lock lock) {
        return (obj, method, args, proxy) -> {
            try {
                lock.lock();
                return proxy.invokeSuper(obj, args);
            } finally {
                lock.unlock();
            }
        };
    }

    private static MethodInterceptor createInterruptibleLockInterceptor(Lock lock) {
        return (obj, method, args, proxy) -> {
            try {
                lock.lockInterruptibly();
                return proxy.invokeSuper(obj, args);
            } finally {
                lock.unlock();
            }
        };
    }

}
