package com.vk.api.wall

import com.vk.api.VKIterator
import com.vk.worker.VKAnonymousWorker
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
@PackageScope
class WallIterator extends VKIterator<Post> {
    private final int ownerId

    WallIterator(VKAnonymousWorker engine, int ownerId, int offset, Filter filter) {
        super(engine, 'wall.get', [
                owner_id: ownerId,
                filter: filter
        ], offset)

        this.ownerId = ownerId
    }

    @Override
    protected void fillBuffer(Element response, Queue<Post> buffer) throws Exception {
        use(DOMCategory) {
            response.post.each {
                Post post = new Post(
                        new Date(TimeUnit.SECONDS.toMillis(it.date.text().toLong())),
                        it.text.text(),
                        ownerId,
                        it.id.text().toInteger(),
                        it.from_id.text().toInteger(),
                        it.to_id.text().toInteger(),
                )

                buffer << post
            }
        }
    }
}