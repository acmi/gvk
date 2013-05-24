package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.Info
import com.vk.api.groups.GroupsCommon
import com.vk.api.likes.Like
import com.vk.api.users.UsersCommon
import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
class WallCommon {
/**
 * Возвращает список записей со стены пользователя или сообщества.
 *
 * @param worker VKAnonymousWorker
 * @param ownerId идентификатор пользователя. Чтобы получить записи со стены группы (публичной страницы, встречи), укажите её идентификатор со знаком "минус": например, ownerId=-1 соответствует группе с идентификатором 1.
 * @param offset смещение, необходимое для выборки определенного подмножества сообщений.
 * @param filter определяет, какие типы сообщений на стене необходимо получить. Если параметр не задан, то считается, что он равен <b>all</b>.
 * @return Итератор постов
 * @exception RuntimeException ( wrapped IOException , VKException )
 */
    static Iterator<Post> get(VKAnonymousWorker worker, int ownerId, int offset = 0, Filter filter = Filter.all) {
        new WallIterator(worker, ownerId, offset, filter)
    }

    /**
     * Возвращает список записей со стен пользователей по их идентификаторам.
     *
     * @param worker VKAnonymousWorker
     * @param posts перечисленные через запятую идентификаторы, которые представляют собой идущие через знак подчеркивания id владельцев стен и id самих записей на стене. Пример: 93388_21539,93388_20904,2943_4276
     * @return Итератор постов
     * @throws IOException
     * @throws VKException
     */
    static Iterator<Post> getById(VKAnonymousWorker worker, Collection<Identifier> posts) throws IOException, VKException {
        use(DOMCategory) {
            Map params = [:]
            if (posts?.size() > 0)
                params['posts'] = posts.collect { it.toString(false) }.join(',')
            def response = worker.executeQuery(new VKRequest('wall.getById', params))
            response.post.collect {
                new Post(
                        new Date(TimeUnit.SECONDS.toMillis(it.date.text().toLong())),
                        it.text.text(),
                        it.id.text().toInteger(),
                        it.from_id.text().toInteger(),
                        it.to_id.text().toInteger(),
                )
            }.iterator()
        }
    }

    /**
     * Возвращает список комментариев к записи на стене пользователя.
     *
     * @param worker VKAnonymousWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, к которой необходимо получить комментарии.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, необходимое для выборки определенного подмножества комментариев.
     * @param sort порядок сортировки комментариев
     * @return Итератор комментариев
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    static Iterator<Comment> getComments(VKAnonymousWorker worker, int ownerId, int postId, int offset = 0, Sort sort = Sort.asc) {
        new CommentIterator(worker, ownerId, postId, offset, sort)
    }

    /**
     * Получает информацию о пользователях, которые добавили указанную запись в свой список <b>Мне нравится</b>. Список пользователей отсортирован в порядке убывания добавления записи в список <b>Мне нравится</b>.
     *
     * @param worker VKAnonymousWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, относительно начала списка, для выборки определенного подмножества.
     * @param publishedOnly указывает, что необходимо вернуть информацию только пользователях, опубликовавших данную запись у себя на стене.
     * @param friendsOnly указывает, необходимо ли возвращать только пользователей, которые являются друзьями текущего пользователя.
     * @return Итератор лайков
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    static Iterator<Like> getLikes(VKAnonymousWorker worker, int ownerId, int postId, int offset = 0, boolean publishedOnly = false, boolean friendsOnly = false) {
        new LikeIterator(worker, ownerId, postId, offset, publishedOnly, friendsOnly)
    }

    static Info getToInfo(Post post, VKAnonymousWorker worker) throws IOException, VKException {
        int id = post.to
        if (id < 0) {
            return GroupsCommon.getById(worker, -id)
        } else if (id > 0) {
            return UsersCommon.get(worker, id)
        }
        null
    }

    static Info getFromInfo(Post post, VKAnonymousWorker worker) throws IOException, VKException {
        int id = post.from
        if (id < 0) {
            return GroupsCommon.getById(worker, -id)
        } else if (id > 0) {
            return UsersCommon.get(worker, id)
        }
        null
    }

    static Iterator<Comment> getComments(Post post, VKAnonymousWorker worker, int offset = 0, Sort sort = Sort.asc) {
        getComments(worker, post.identifier.ownerId, post.identifier.mediaId, offset, sort)
    }

    static Iterator<Like> getLikes(Post post, VKAnonymousWorker worker, int offset = 0, boolean publishedOnly = false, boolean friendsOnly = false) {
        getLikes(worker, post.identifier.ownerId, post.identifier.mediaId, offset, publishedOnly, friendsOnly)
    }

    static Info getFromInfo(Comment comment, VKAnonymousWorker worker) throws IOException, VKException {
        int id = comment.user
        if (id < 0) {
            return GroupsCommon.getById(worker, -id)
        } else if (id > 0) {
            return UsersCommon.get(worker, id)
        }
        null
    }
}
