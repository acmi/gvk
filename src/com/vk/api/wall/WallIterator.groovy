package com.vk.api.wall

import com.vk.api.VKIterator
import com.vk.api.VKWorker
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
@PackageScope
class WallIterator extends VKIterator<Post> {

    WallIterator(VKWorker engine, int ownerId, int offset, Wall.Filter filter) {
        super(engine, 'wall.get', [
                owner_id: ownerId,
                filter: filter
        ], offset)
    }

    @Override
    protected void fillBuffer(Element response) throws Exception {
        use(DOMCategory) {
            response.post.each {
                Post post = new Post(
                        new Date(TimeUnit.SECONDS.toMillis(it.date.text().toLong())),
                        it.text.text(),
                        it.id.text().toInteger(),
                        it.from_id.text().toInteger(),
                        it.to_id.text().toInteger(),
                )

                buffer << post
            }
        }
    }
}