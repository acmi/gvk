package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.VKIterator
import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
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
    protected int getCount() throws Exception {
        try{
            return super.getCount()
        }catch(VKException vke){
            if (vke.code == VKException.USER_WAS_DELETED_OR_BANNED)
                return 0

            throw vke
        }
    }

    @Override
    protected void fillBuffer(Element response, Queue<Post> buffer) throws Exception {
        use(DOMCategory) {
            response.post.each {
                Post post = new Post(
                        new Identifier(ownerId, it.id.text().toInteger(), Identifier.Type.post),
                        it.from_id.text().toInteger(),
                        it.to_id.text().toInteger(),
                        new Date(TimeUnit.SECONDS.toMillis(it.date.text().toLong())),
                        it.text.text(),
                )

                buffer << post
            }
        }
    }
}