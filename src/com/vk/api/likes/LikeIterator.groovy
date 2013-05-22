package com.vk.api.likes

import com.vk.api.VKIterator
import com.vk.api.VKWorker
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

/**
 * @author acmi
 */
@PackageScope
class LikeIterator extends VKIterator<Like> {
    private final Filter filter

    LikeIterator(VKWorker engine, Type likeType, int ownerId, int itemId, int offset, Filter filter, boolean friendsOnly) {
        super(engine, 'likes.getList', [
                type: likeType.name(),
                owner_id: ownerId,
                item_id: itemId,
                filter: filter.name(),
                friends_only: friendsOnly ? 1 : 0
        ], offset)

        this.filter = filter

        bufferSize = friendsOnly ? 100 : 1000
    }

    @Override
    protected void fillBuffer(Element response) {
        use(DOMCategory) {
            response.users.uid.each {
                Like like = new Like(
                        it.text().toInteger(),
                        filter == Likes.Filter.copies
                )

                buffer << like
            }
        }
    }
}
