package com.vk.api.wall

import com.vk.api.VKEngine
import com.vk.api.likes.Like
import groovy.xml.dom.DOMCategory

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
class Wall {
    /**
     * Возвращает список записей со стены пользователя или сообщества.
     *
     * @param engine VKEngine
     * @param ownerId идентификатор пользователя. Чтобы получить записи со стены группы (публичной страницы, встречи), укажите её идентификатор со знаком "минус": например, ownerId=-1 соответствует группе с идентификатором 1.
     * @param offset смещение, необходимое для выборки определенного подмножества сообщений.
     * @param filter определяет, какие типы сообщений на стене необходимо получить. Если параметр не задан, то считается, что он равен <b>all</b>.
     * @return Итератор постов
     */
    static Iterator<Post> get(VKEngine engine, int ownerId, int offset = 0, Filter filter = Filter.all) {
        new WallIterator(engine, ownerId, offset, filter)
    }

    static enum Filter {
        owner,
        others,
        all
    }

    /**
     * Возвращает список комментариев к записи на стене пользователя.
     *
     * @param engine VKEngine
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, к которой необходимо получить комментарии.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, необходимое для выборки определенного подмножества комментариев.
     * @param sort порядок сортировки комментариев
     * @return Итератор комментариев
     */
    static Iterator<Comment> getComments(VKEngine engine, int ownerId, int postId, int offset = 0, Sort sort = Sort.asc) {
        new CommentIterator(engine, ownerId, postId, offset, sort)
    }

    static enum Sort {
        asc,
        desc
    }

    /**
     * Возвращает список записей со стен пользователей по их идентификаторам.
     *
     * @param engine VKEngine
     * @param posts перечисленные через запятую идентификаторы, которые представляют собой идущие через знак подчеркивания id владельцев стен и id самих записей на стене. Пример: 93388_21539,93388_20904,2943_4276
     * @return Итератор постов
     */
    static Iterator<Post> getById(VKEngine engine, List<PostIdentifier> posts) {
        use(DOMCategory) {
            Map params = [:]
            if (posts?.size() > 0)
                params['posts'] = posts.join(',')
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
     * @param engine VKEngine
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
     */
    static Integer post(VKEngine engine, int ownerId, String message, List<AttachmentIdentifier> attachments = null, int lat = 0, int lng = 0, Integer placeId = 0, List services = null, Boolean fromGroup = false, Boolean signed = false, Boolean friendsOnly = false) {
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
                params['attachments'] = attachments.join(',')
            if (services?.size() > 0)
                params['services'] = services.join(',')

            engine.executeQuery('wall.post', params).post_id.text().toInteger()
        }
    }

    /**
     * Получает информацию о пользователях, которые добавили указанную запись в свой список <b>Мне нравится</b>. Список пользователей отсортирован в порядке убывания добавления записи в список <b>Мне нравится</b>.
     *
     * @param engine VKEngine
     * @param ownerId идентификатор пользователя, на чьей стене находится запись.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, относительно начала списка, для выборки определенного подмножества.
     * @param publishedOnly указывает, что необходимо вернуть информацию только пользователях, опубликовавших данную запись у себя на стене.
     * @param friendsOnly указывает, необходимо ли возвращать только пользователей, которые являются друзьями текущего пользователя.
     * @return Итератор лайков
     */
    static Iterator<Like> getLikes(VKEngine engine, int ownerId, int postId, int offset = 0, boolean publishedOnly = false, boolean friendsOnly = false) {
        new LikeIterator(engine, ownerId, postId, offset, publishedOnly, friendsOnly)
    }
}
