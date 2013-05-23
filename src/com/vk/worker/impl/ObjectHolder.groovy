package com.vk.worker.impl

import groovy.transform.PackageScope

import java.util.concurrent.CountDownLatch

/**
 * @author acmi
 */
@PackageScope
class ObjectHolder<V> {
    private final CountDownLatch latch = new CountDownLatch(1)
    private V slot

    V get() throws InterruptedException {
        latch.await()
        slot
    }

    void set(V value) {
        slot = value
        latch.countDown()
    }
}
