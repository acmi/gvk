package com.vk.api.wall

import com.vk.api.VKEngine
import com.vk.api.VKIterator
import com.vk.api.likes.Like
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
@PackageScope
class LikeIterator extends VKIterator<Like> {
    private int ownerId
    private int postId
    private boolean publishedOnly
    private boolean friendsOnly

    LikeIterator(VKEngine engine, int ownerId, int postId, int offset, boolean publishedOnly, boolean friendsOnly) {
        super(engine, offset)

        this.ownerId = ownerId
        this.postId = postId
        this.publishedOnly = publishedOnly
        this.friendsOnly = friendsOnly
    }

    @Override
    protected int getCount() throws Exception {
        use(DOMCategory) {
            engine.executeQuery('wall.getLikes', [
                    owner_id: ownerId,
                    post_id: postId,
                    published_only: publishedOnly,
                    friends_only: friendsOnly,
                    count: 1
            ]).count.text().toInteger()
        }
    }

    @Override
    protected void fillBuffer() throws Exception {
        use(DOMCategory) {
            int step = friendsOnly ? 100 : 1000
            def response = engine.executeQuery('wall.getLikes', [
                    owner_id: ownerId,
                    post_id: postId,
                    published_only: publishedOnly ? 1 : 0,
                    friends_only: friendsOnly ? 1 : 0,
                    offset: offset + getProcessed(),
                    count: step
            ])
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
