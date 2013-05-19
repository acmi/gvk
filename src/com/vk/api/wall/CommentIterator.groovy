package com.vk.api.wall

import com.vk.api.VKEngine
import com.vk.api.VKIterator
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
@PackageScope
class CommentIterator extends VKIterator<Comment> {
    int ownerId
    int postId
    Wall.Sort sort

    CommentIterator(VKEngine engine, int ownerId, int postId, int offset, Wall.Sort sort) {
        super(engine, offset)

        this.ownerId = ownerId
        this.postId = postId
        this.sort = sort
    }

    @Override
    protected int getCount() {
        use(DOMCategory) {
            engine.executeQuery('wall.getComments', [
                    owner_id: ownerId,
                    post_id: postId,
                    count: 1
            ]).count.text().toInteger()
        }
    }

    @Override
    protected void fillBuffer() {
        use(DOMCategory) {
            int step = 100
            def response = engine.executeQuery('wall.getComments', [
                    owner_id: ownerId,
                    post_id: postId,
                    sort: sort.name(),
                    need_likes: 0,
                    offset: offset + getProcessed(),
                    count: step,
                    preview_length: 0,
                    v: 4.4 //v=4.4 для того, чтобы получать аттачи в комментариях в виде объектов, а не ссылок.
            ])
            response.comment.each {
                Comment comment = new Comment(
                        id: it.cid.text().toInteger(),
                        user: it.uid.text().toInteger(),
                        date: new Date(it.date.text().toLong() * 1000),
                        text: it.text.text()
                )

                buffer << comment
            }
        }
    }
}
