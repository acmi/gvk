package com.vk.api.wall

import com.vk.api.AccessToken
import com.vk.api.VKEngine
import com.vk.api.VKEngineGPath
import com.vk.api.VKIterator

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
class Wall {
    static class WallIterator extends VKIterator<Post> {
        private int ownerId
        private WallFilter filter = WallFilter.all

        WallIterator(VKEngineGPath engine, int ownerId, int offset = 0, WallFilter filter = WallFilter.all) {
            super(engine, offset)

            this.ownerId = ownerId
            this.filter = filter != null ? filter : WallFilter.all
        }

        @Override
        protected int getCount() {
            engine.executeQuery('wall.get', [
                    owner_id: ownerId,
                    count: 1,
                    filter: filter.name()]).count.text().toInteger()
        }

        @Override
        protected void fillBuffer() {
            def wall = engine.executeQuery('wall.get', [
                    owner_id: ownerId,
                    offset: offset + getReceived(),
                    count: 100,
                    filter: filter.name()
            ])
            wall.post.each {
                Post post = new Post()

                post.id = it.id.text().toInteger()
                post.from = it.from_id.text().toInteger()
                post.to = it.to_id.text().toInteger()
                post.date = new Date(TimeUnit.SECONDS.toMillis(it.date.text().toLong()))
                post.text = it.text.text()

                buffer << post
            }
        }
    }

    static enum WallFilter {
        owner,
        others,
        all
    }

    static class CommentIterator extends VKIterator<Comment> {
        int ownerId
        int postId
        CommentSort sort

        CommentIterator(VKEngine engine, int ownerId, int postId, int offset=0, CommentSort sort=CommentSort.asc) {
            super(engine, offset)

            this.ownerId = ownerId
            this.postId = postId
            this.sort = sort
        }

        @Override
        protected int getCount() {
            engine.executeQuery('wall.getComments', [
                    owner_id: ownerId,
                    post_id: postId,
            ]).count.text().toInteger()
        }

        @Override
        protected void fillBuffer() {
            int step = 100
            def response = engine.executeQuery('wall.getComments', [
                    owner_id: ownerId,
                    post_id: postId,
                    sort: sort.name(),
                    need_likes: 0,
                    offset: offset + getReceived(),
                    count: step,
                    preview_length: 0,
                    v: 4.4 //v=4.4 для того, чтобы получать аттачи в комментариях в виде объектов, а не ссылок.
            ])
            response.comment.each {
                Comment comment = new Comment()

                comment.id = it.cid.text().toInteger()
                comment.user = it.uid.text().toInteger()
                comment.date = new Date(it.date.text().toLong() * 1000)
                comment.text = it.text.text()

                buffer << comment
            }
        }
    }

    static enum CommentSort {
        asc,
        desc
    }
}
