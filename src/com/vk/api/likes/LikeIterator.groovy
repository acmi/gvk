package com.vk.api.likes

import com.vk.api.VKEngine
import com.vk.api.VKIterator
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
@PackageScope
class LikeIterator extends VKIterator<Like> {
    private Likes.Type type
    private int ownerId
    private int itemId
    private Likes.Filter filter
    private boolean friendsOnly

    LikeIterator(VKEngine engine, Likes.Type likeType, int ownerId, int itemId, int offset, Likes.Filter filter, boolean friendsOnly) {
        super(engine, offset)
        this.type = likeType
        this.ownerId = ownerId
        this.itemId = itemId
        this.filter = filter
        this.friendsOnly = friendsOnly
    }

    @Override
    protected int getCount() {
        use(DOMCategory) {
            engine.executeQuery('likes.getList', [
                    type: type.name(),
                    owner_id: ownerId,
                    item_id: itemId,
                    filter: filter.name(),
                    friends_only: friendsOnly ? 1 : 0,
                    count: 1
            ]).count.text().toInteger()
        }
    }

    @Override
    protected void fillBuffer() {
        use(DOMCategory) {
            def response = engine.executeQuery('likes.getList', [
                    type: type.name(),
                    owner_id: ownerId,
                    item_id: itemId,
                    filter: filter.name(),
                    friends_only: friendsOnly ? 1 : 0,
                    offset: offset + getProcessed(),
                    count: friendsOnly ? 100 : 1000
            ])
            response.users.uid.each {
                Like like = new Like(
                        it.text().toInteger(),
                        filter == Likes.Filter.copies
                )

                buffer << like
            }
        }
    }

    @Override
    void remove() {
        throw new UnsupportedOperationException()
    }
}
