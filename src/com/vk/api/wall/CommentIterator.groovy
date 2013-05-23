package com.vk.api.wall

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
class CommentIterator extends VKIterator<Comment> {
    private final int ownerId

    CommentIterator(VKAnonymousWorker engine, int ownerId, int postId, int offset, Sort sort) {
        super(engine, 'wall.getComments', [
                owner_id: ownerId,
                post_id: postId,
                sort: sort.name(),
                need_likes: 0,
                preview_length: 0,
                v: 4.4 //v=4.4 для того, чтобы получать аттачи в комментариях в виде объектов, а не ссылок.
        ], offset)

        this.ownerId = ownerId
    }

    @Override
    protected int getCount() throws Exception {
        try {
            return super.getCount()
        } catch (VKException vke) {
            if (vke.code == 212) {//Access to post comments denied
                return 0
            } else {
                throw vke
            }
        }
    }

    @Override
    protected void fillBuffer(Element response, Queue<Comment> buffer) {
        use(DOMCategory) {
            response.comment.each {
                Comment comment = new Comment(
                        ownerId,
                        it.cid.text().toInteger(),
                        it.uid.text().toInteger(),
                        new Date(TimeUnit.SECONDS.toMillis(it.date.text().toLong())),
                        it.text.text()
                )

                buffer << comment
            }
        }
    }
}
