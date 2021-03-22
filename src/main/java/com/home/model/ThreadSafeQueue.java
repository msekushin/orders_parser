package com.home.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadSafeQueue<T> {

    private final long timeout;
    private final TimeUnit timeUnit;
    private final BlockingQueue<T> queue;
    private final AtomicBoolean continueWork = new AtomicBoolean(Boolean.TRUE);

    public ThreadSafeQueue(int capacity, long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        queue = new ArrayBlockingQueue<>(capacity);
    }

    public void put(T data) throws InterruptedException {
        this.queue.put(data);
    }

    public T get() throws InterruptedException {
        return this.queue.poll(timeout, timeUnit);
    }

    public int size() {
        return this.queue.size();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public boolean isContinue() {
        return continueWork.get() || !queue.isEmpty();
    }

    public boolean getContinueWork() {
        return continueWork.get();
    }

    public void complete() {
        continueWork.set(Boolean.FALSE);
    }

    public void start() {
        continueWork.set(Boolean.TRUE);
    }
}
