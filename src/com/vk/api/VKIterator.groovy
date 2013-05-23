package com.vk.api

import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

abstract class VKIterator<T> implements Iterator<T> {
    protected final VKAnonymousWorker engine
    protected final int offset

    protected final String method
    protected final Map params = [:]

    protected Integer count
    private int processed
    private int bufferSize = 100
    private final Queue<T> buffer = new LinkedList<T>()

    public VKIterator(VKAnonymousWorker engine, String method, Map params, int offset) {
        this.engine = engine
        this.method = method
        this.params = params.collectEntries { k, v ->
            if (v != null)
                [(k): v]
        }
        this.offset = offset
    }

    protected void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize
    }

    protected int getProcessed() {
        return processed;
    }

    @Override
    public boolean hasNext() {
        if (count == null) {
            try {
                count = getCount()
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
                fillBuffer(engine.executeQuery(new VKRequest(method, params)), buffer)
            } catch (Exception e) {
                throw new RuntimeException(e)
            }
        processed++
        return buffer.poll()
    }

    protected int getCount() throws Exception {
        use(DOMCategory) {
            Map params = new HashMap(this.params)
            params['count'] = 1
            count = engine.executeQuery(new VKRequest(method, params)).count.text().toInteger()
        }
    }

    protected abstract void fillBuffer(Element response, Queue<T> buffer) throws Exception

    @Override
    public void remove() {
        throw new UnsupportedOperationException()
    }
}
