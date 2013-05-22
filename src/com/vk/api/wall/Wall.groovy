package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.VKWorker
import com.vk.api.VKException
import com.vk.api.likes.Like
import groovy.transform.PackageScope
import groovy.transform.PackageScopeTarget
import groovy.xml.dom.DOMCategory

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
@PackageScope([PackageScopeTarget.METHODS])
class Wall {
    /**
     * Возвращает список записей со стены пользователя или сообщества.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя. Чтобы получить записи со стены группы (публичной страницы, встречи), укажите её идентификатор со знаком "минус": например, ownerId=-1 соответствует группе с идентификатором 1.
     * @param offset смещение, необходимое для выборки определенного подмножества сообщений.
     * @param filter определяет, какие типы сообщений на стене необходимо получить. Если параметр не задан, то считается, что он равен <b>all</b>.
     * @return Итератор постов
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    static Iterator<Post> get(VKWorker engine, int ownerId, int offset = 0, Filter filter = Filter.all) {
        new WallIterator(engine, ownerId, offset, filter)
    }

    static enum Filter {
        owner,
        others,
        all
    }

    /**
     * Возвращает список записей со стен пользователей по их идентификаторам.
     *
     * @param engine VKWorker
     * @param posts перечисленные через запятую идентификаторы, которые представляют собой идущие через знак подчеркивания id владельцев стен и id самих записей на стене. Пример: 93388_21539,93388_20904,2943_4276
     * @return Итератор постов
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static Iterator<Post> getById(VKWorker engine, List<Identifier> posts) throws IOException, VKException {
        use(DOMCategory) {
            Map params = [:]
            if (posts?.size() > 0)
                params['posts'] = posts.collect { it.toString(false) }.join(',')
            def response = engine.executeQuery('wall.getById', params)
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
     * Публикует новую запись на своей или чужой стене.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, у которого должна быть опубликована запись.
     * @param message текст сообщения (является обязательным, если не задан параметр <b>attachments</b>)
     * @param attachments Параметр является обязательным, если не задан параметр <b>message</b>.
     * @param lat географическая широта отметки, заданная в градусах (от -90 до 90).
     * @param lng географическая долгота отметки, заданная в градусах (от -180 до 180).
     * @param placeId идентификатор места, в котором отмечен пользователь
     * @param services Список сервисов или сайтов, на которые необходимо экспортировать статус, в случае если пользователь настроил соответствующую опцию. Например <b>twitter, facebook</b>.
     * @param fromGroup Cтатус будет опубликован от имени группы, иначе от имени пользователя. Данный параметр учитывается, если owner_id < 0 (статус публикуется на стене группы).
     * @param signed У статуса, размещенного от имени группы будет добавлена подпись (имя пользователя, разместившего запись). Параметр учитывается только при публикации на стене группы и указании параметра <b>fromGroup</b>.
     * @param friendsOnly Статус будет доступен только друзьям, иначе всем пользователям.
     * @return Идентификатор созданной записи.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static Integer post(VKWorker engine, int ownerId, String message, List<Identifier> attachments = null, int lat = 0, int lng = 0, int placeId = 0, List services = null, boolean fromGroup = false, boolean signed = false, boolean friendsOnly = false) throws IOException, VKException {
        use(DOMCategory) {
            Map params = [
                    owner_id: ownerId,
                    lat: lat,
                    long: lng,
                    place_id: placeId,
                    from_group: fromGroup ? 1 : 0,
                    signed: signed ? 1 : 0,
                    friends_only: friendsOnly ? 1 : 0
            ]
            if (message?.length() > 0)
                params['message'] = message
            if (attachments?.size() > 0)
                params['attachments'] = attachments.collect { it.toString(true) }.join(',')
            if (services?.size() > 0)
                params['services'] = services.join(',')

            engine.executeQuery('wall.post', params).post_id.text().toInteger()
        }
    }

    /**
     * Редактирует запись на своей или чужой стене.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, которую необходимо отредактировать.
     * @param postId идентификатор записи на стене пользователя.
     * @param message текст сообщения (является обязательным, если не задан параметр <b>attachments</b>)
     * @param attachments список объектов, приложенных к записи и разделённых символом ",". Параметр является обязательным, если не задан параметр <b>message</b>.
     * @param lat географическая широта отметки, заданная в градусах (от -90 до 90).
     * @param lng географическая долгота отметки, заданная в градусах (от -180 до 180).
     * @param placeId идентификатор места, в котором отмечен пользователь
     * @return В случае успешного сохранения записи метод возвратит true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static boolean edit(VKWorker engine, int ownerId, int postId, String message, List<Identifier> attachments = null, int lat = 0, int lng = 0, int placeId = 0) throws IOException, VKException {
        use(DOMCategory) {
            Map params = [
                    owner_id: ownerId,
                    post_id: postId,
                    lat: lat,
                    long: lng,
                    place_id: placeId
            ]
            if (message?.length() > 0)
                params['message'] = message
            if (attachments?.size() > 0)
                params['attachments'] = attachments.collect { it.toString(true) }.join(',')
            engine.executeQuery('wall.edit', params).text() == '1'
        }
    }

    /**
     * Удаляет запись со стены пользователя.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене необходимо удалить запись.
     * @param postId идентификатор записи на стене пользователя.
     * @return В случае успешного удаления записи со стены пользователя возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static boolean delete(VKWorker engine, int ownerId, int postId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('wall.delete', [
                    owner_id: ownerId,
                    post_id: postId
            ]).text() == '1'
        }
    }

    /**
     * Восстанавливает удаленную запись на стене пользователя.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене необходимо удалить запись.
     * @param postId идентификатор записи на стене пользователя.
     * @return В случае успешного восстановления записи на стене пользователя возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static boolean restore(VKWorker engine, int ownerId, int postId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('wall.restore', [
                    owner_id: ownerId,
                    post_id: postId
            ]).text() == '1'
        }
    }

    /**
     * Возвращает список комментариев к записи на стене пользователя.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, к которой необходимо получить комментарии.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, необходимое для выборки определенного подмножества комментариев.
     * @param sort порядок сортировки комментариев
     * @return Итератор комментариев
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    static Iterator<Comment> getComments(VKWorker engine, int ownerId, int postId, int offset = 0, Sort sort = Sort.asc) {
        new CommentIterator(engine, ownerId, postId, offset, sort)
    }

    static enum Sort {
        asc,
        desc
    }

    /**
     * Добавляет комментарий к записи на стене пользователя.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись к которой необходимо добавить комментарий.
     * @param postId идентификатор записи на стене пользователя.
     * @param text текст комментария к записи на стене пользователя.
     * @param replyToCid идентификатор комментария, ответом на который является добавляемый комментарий.
     * @param attachments список объектов, приложенных к комментарию и разделённых символом ",".Параметр является обязательным, если не задан параметр {@code text}.
     * @return В случае успешного добавления комментария к записи возвращает идентификатор добавленного комментария на стене пользователя.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static int addComment(VKWorker engine, int ownerId, int postId, String text, int replyToCid = 0, List<Identifier> attachments = null) throws IOException, VKException {
        use(DOMCategory) {
            Map params = [
                    owner_id: ownerId,
                    post_id: postId,
                    reply_to_cid: replyToCid
            ]
            if (text?.length() > 0)
                params['text'] = text
            if (attachments?.size() > 0)
                params['attachments'] = attachments.collect { it.toString(true) }.join(',')
            engine.executeQuery('wall.restore', params).cid.text().toInteger()
        }
    }

    /**
     * Удаляет комментарий текущего пользователя к записи на своей или чужой стене.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится комментарий к записи.
     * @param cid идентификатор комментария на стене пользователя.
     * @return В случае успеха возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static boolean deleteComment(VKWorker engine, int ownerId, int cid) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('wall.deleteComment', [
                    owner_id: ownerId,
                    cid: cid
            ]).text() == '1'
        }
    }

    /**
     * Восстанавливает комментарий текущего пользователя к записи на своей или чужой стене.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится комментарий к записи.
     * @param cid идентификатор комментария на стене пользователя.
     * @return В случае успеха возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static boolean restoreComment(VKWorker engine, int ownerId, int cid) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('wall.restoreComment', [
                    owner_id: ownerId,
                    cid: cid
            ]).text() == '1'
        }
    }

    /**
     * Получает информацию о пользователях, которые добавили указанную запись в свой список <b>Мне нравится</b>. Список пользователей отсортирован в порядке убывания добавления записи в список <b>Мне нравится</b>.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, относительно начала списка, для выборки определенного подмножества.
     * @param publishedOnly указывает, что необходимо вернуть информацию только пользователях, опубликовавших данную запись у себя на стене.
     * @param friendsOnly указывает, необходимо ли возвращать только пользователей, которые являются друзьями текущего пользователя.
     * @return Итератор лайков
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    static Iterator<Like> getLikes(VKWorker engine, int ownerId, int postId, int offset = 0, boolean publishedOnly = false, boolean friendsOnly = false) {
        new LikeIterator(engine, ownerId, postId, offset, publishedOnly, friendsOnly)
    }

    /**
     * Добавляет запись на стене пользователя в список <b>Мне нравится</b>, а также создает копию понравившейся записи на стене текущего пользователя при необходимости.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, которую необходимо добавить в список <b>Мне нравится</b>.
     * @param postId идентификатор сообщения на стене пользователя, которое необходимо добавить в список <b>Мне нравится</b>.
     * @param repost определяет, необходимо ли опубликовать запись, которая заносится в список <b>Мне нравится</b>, на стене текущего пользователя. Публикация возможна только для записей, находящихся на чужих стенах.
     * @param message комментарий к записи, публикуемой на своей странице (при использовании параметра repost). По умолчанию комментарий к записи не добавляется.
     * @return В случае успешного добавления сообщения в список Мне нравится возвращает Map с ключами likes и reposts, по которым находится текущее количество человек, которые добавили данное сообщение в свой список Мне нравится и количество человек, опубликовавших запись на своих страницах.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static Map<String, Integer> addLike(VKWorker engine, int ownerId, int postId, boolean repost = false, String message = null) throws IOException, VKException {
        use(DOMCategory) {
            def response = engine.executeQuery('wall.restoreComment', [
                    owner_id: ownerId,
                    cid: cid
            ])
            [
                    likes: response.likes.text().toInteger(),
                    reposts: response.reposts.text().toInteger()
            ]
        }
    }

    /**
     * Удаляет запись на стене пользователя из списка Мне нравится.
     *
     * @param engine VKWorker
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, которую необходимо удалить из списка Мне нравится.
     * @param postId идентификатор сообщения на стене пользователя, которое необходимо удалить из списка Мне нравится.
     * @return В случае успешного удаления сообщения из списка Мне нравится возвращает текущее количество человек, которые добавили данное сообщение в свой список Мне нравится.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    static int deleteLike(VKWorker engine, int ownerId, int postId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('wall.deleteLike', [
                    owner_id: ownerId,
                    postId: postId
            ]).likes.text().toInteger()
        }
    }
}
