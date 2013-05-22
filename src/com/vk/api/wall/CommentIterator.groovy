package com.vk.api.wall

import com.vk.api.VKIterator
import com.vk.api.VKWorker
import groovy.transform.PackageScope
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

/**
 * @author acmi
 */
@PackageScope
class CommentIterator extends VKIterator<Comment> {

    CommentIterator(VKWorker engine, int ownerId, int postId, int offset, Sort sort) {
        super(engine, 'wall.getComments', [
                owner_id: ownerId,
                post_id: postId,
                sort: sort.name(),
                need_likes: 0,
                preview_length: 0,
                v: 4.4 //v=4.4 для того, чтобы получать аттачи в комментариях в виде объектов, а не ссылок.
        ], offset)
    }

    @Override
    protected void fillBuffer(Element response) {
        use(DOMCategory) {
            response.comment.each {
                Comment comment = new Comment(
                        it.cid.text().toInteger(),
                        it.uid.text().toInteger(),
                        new Date(it.date.text().toLong() * 1000),
                        it.text.text()
                )

                buffer << comment
            }
        }
    }
}
