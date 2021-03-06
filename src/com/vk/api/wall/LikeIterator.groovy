package com.vk.api.wall

import com.vk.api.VKIterator
import com.vk.api.likes.Like
import com.vk.worker.VKAnonymousWorker
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

/**
 * @author acmi
 */
@PackageScope
class LikeIterator extends VKIterator<Like> {
    private final boolean publishedOnly

    LikeIterator(VKAnonymousWorker engine, int ownerId, int postId, int offset, boolean publishedOnly, boolean friendsOnly) {
        super(engine, 'wall.getLikes', [
                owner_id: ownerId,
                post_id: postId,
                published_only: publishedOnly ? 1 : 0,
                friends_only: friendsOnly ? 1 : 0
        ], offset)

        this.publishedOnly = publishedOnly

        bufferSize = friendsOnly ? 100 : 1000
    }

    @Override
    protected void fillBuffer(Element response, Queue<Like> buffer) throws Exception {
        use(DOMCategory) {
            response.users.user.each {
                Like like = new Like(
                        it.uid.text().toInteger(),
                        publishedOnly ? true : it.copied.text() == '1'
                )

                buffer << like
            }
        }
    }
}
