package com.vk.api

import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

abstract class VKIterator<T> implements Iterator<T> {
    protected final VKWorker engine
    protected final int offset

    protected final String method
    protected final Map params = [:]

    protected int count = -1
    private int processed
    protected int bufferSize = 100
    protected final Queue<T> buffer = new LinkedList<T>()

    public VKIterator(VKWorker engine, String method, Map params, int offset) {
        this.engine = engine
        this.method = method
        this.params = params.collectEntries { k, v ->
            if (v != null)
                [(k): v]
        }
        this.offset = offset
    }

    protected int getProcessed() {
        return processed;
    }

    @Override
    public boolean hasNext() {
        if (count == -1) {
            try {
                use(DOMCategory) {
                    Map params = new HashMap(this.params)
                    params['count'] = 1
                    count = engine.executeQuery(new VKRequest(method, params)).count.text().toInteger()
                }
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
                Map params = new HashMap(this.params)
                params['count'] = bufferSize
                params['offset'] = offset + getProcessed()
                fillBuffer(engine.executeQuery(new VKRequest(method, params)))
            } catch (Exception e) {
                throw new RuntimeException(e)
            }
        processed++
        return buffer.poll()
    }

    protected abstract void fillBuffer(Element response) throws Exception

    @Override
    public void remove() {
        throw new UnsupportedOperationException()
    }
}
