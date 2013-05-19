package com.vk.api.wall

import com.vk.api.VKEngine
import com.vk.api.VKIterator
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
@PackageScope
class WallIterator extends VKIterator<Post> {
    private int ownerId
    private Wall.Filter filter

    WallIterator(VKEngine engine, int ownerId, int offset, Wall.Filter filter) {
        super(engine, offset)

        this.ownerId = ownerId
        this.filter = filter != null ? filter : WallFilter.all
    }

    @Override
    protected int getCount() {
        use(DOMCategory) {
            engine.executeQuery('wall.get', [
                    owner_id: ownerId,
                    count: 1,
                    filter: filter.name()]).count.text().toInteger()
        }
    }

    @Override
    protected void fillBuffer() {
        use(DOMCategory) {
            def wall = engine.executeQuery('wall.get', [
                    owner_id: ownerId,
                    offset: offset + getProcessed(),
                    count: 100,
                    filter: filter.name()
            ])
            wall.post.each {
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