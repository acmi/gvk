package com.vk.api

import groovy.transform.CompileStatic

@CompileStatic
abstract class VKIterator<T> implements Iterator<T>{
    protected final VKEngine engine
    protected final int offset

    private int count = -1
    private int processed
    protected final Queue<T> buffer = new LinkedList<T>()

    public VKIterator(VKEngine engine, int offset) {
        this.engine = engine
        this.offset = offset
    }

    protected int getProcessed() {
        return processed;
    }

    @Override
    public boolean hasNext() {
        if (count == -1) {
            try {
                count = getCount();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return offset + processed < count
    }

    @Override
    public T next() {
        if (buffer.isEmpty())
            try {
                fillBuffer()
            } catch (Exception e) {
                throw new RuntimeException(e)
            }
        processed++
        return buffer.poll()
    }

    protected abstract int getCount() throws Exception

    protected abstract void fillBuffer() throws Exception

    @Override
    public void remove() {
        throw new UnsupportedOperationException()
    }
}
