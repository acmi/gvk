package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.VKException
import com.vk.api.VKWorkerUser

/**
 * @author acmi
 */
class WallFull extends WallCommon {

    WallFull(VKWorkerUser worker) {
        super(worker)
    }

    @Override
    protected VKWorkerUser getWorker() {
        super.getWorker()
    }

    /**
     * Публикует новую запись на своей или чужой стене.
     *
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
    Integer post(int ownerId, String message, List<Identifier> attachments = null, int lat = 0, int lng = 0, int placeId = 0, List services = null, boolean fromGroup = false, boolean signed = false, boolean friendsOnly = false) throws IOException, VKException {
        Wall.post(worker, ownerId, message, attachments, lat, lng, placeId, services, fromGroup, signed, friendsOnly)
    }

    /**
     * Редактирует запись на своей или чужой стене.
     *
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
    boolean edit(int ownerId, int postId, String message, List<Identifier> attachments = null, int lat = 0, int lng = 0, int placeId = 0) throws IOException, VKException {
        Wall.edit(worker, postId, message, attachments, lat, lng, placeId)
    }

    /**
     * Удаляет запись со стены пользователя.
     *
     * @param ownerId идентификатор пользователя, на чьей стене необходимо удалить запись.
     * @param postId идентификатор записи на стене пользователя.
     * @return В случае успешного удаления записи со стены пользователя возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    boolean delete(int ownerId, int postId) throws IOException, VKException {
        Wall.delete(worker, ownerId, postId)
    }

    /**
     * Восстанавливает удаленную запись на стене пользователя.
     *
     * @param ownerId идентификатор пользователя, на чьей стене необходимо удалить запись.
     * @param postId идентификатор записи на стене пользователя.
     * @return В случае успешного восстановления записи на стене пользователя возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    boolean restore(int ownerId, int postId) throws IOException, VKException {
        Wall.restore(worker, ownerId, postId)
    }

    /**
     * Добавляет комментарий к записи на стене пользователя.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится запись к которой необходимо добавить комментарий.
     * @param postId идентификатор записи на стене пользователя.
     * @param text текст комментария к записи на стене пользователя.
     * @param replyToCid идентификатор комментария, ответом на который является добавляемый комментарий.
     * @param attachments список объектов, приложенных к комментарию и разделённых символом ",".Параметр является обязательным, если не задан параметр {@code text}.
     * @return В случае успешного добавления комментария к записи возвращает идентификатор добавленного комментария на стене пользователя.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    int addComment(int ownerId, int postId, String text, int replyToCid = 0, List<Identifier> attachments = null) throws IOException, VKException {
        Wall.addComment(worker, ownerId, postId, text, replyToCid, attachments)
    }

    /**
     * Удаляет комментарий текущего пользователя к записи на своей или чужой стене.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится комментарий к записи.
     * @param cid идентификатор комментария на стене пользователя.
     * @return В случае успеха возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    boolean deleteComment(int ownerId, int cid) throws IOException, VKException {
        Wall.deleteComment(worker, ownerId, cid)
    }

    /**
     * Восстанавливает комментарий текущего пользователя к записи на своей или чужой стене.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится комментарий к записи.
     * @param cid идентификатор комментария на стене пользователя.
     * @return В случае успеха возвращает true.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    boolean restoreComment(int ownerId, int cid) throws IOException, VKException {
        Wall.restoreComment(worker, ownerId, cid)
    }

    /**
     * Добавляет запись на стене пользователя в список <b>Мне нравится</b>, а также создает копию понравившейся записи на стене текущего пользователя при необходимости.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, которую необходимо добавить в список <b>Мне нравится</b>.
     * @param postId идентификатор сообщения на стене пользователя, которое необходимо добавить в список <b>Мне нравится</b>.
     * @param repost определяет, необходимо ли опубликовать запись, которая заносится в список <b>Мне нравится</b>, на стене текущего пользователя. Публикация возможна только для записей, находящихся на чужих стенах.
     * @param message комментарий к записи, публикуемой на своей странице (при использовании параметра repost). По умолчанию комментарий к записи не добавляется.
     * @return В случае успешного добавления сообщения в список Мне нравится возвращает Map с ключами likes и reposts, по которым находится текущее количество человек, которые добавили данное сообщение в свой список Мне нравится и количество человек, опубликовавших запись на своих страницах.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Map<String, Integer> addLike(int ownerId, int postId, boolean repost = false, String message = null) throws IOException, VKException {
        Wall.addLike(worker, ownerId, postId, repost, message)
    }

    /**
     * Удаляет запись на стене пользователя из списка Мне нравится.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, которую необходимо удалить из списка Мне нравится.
     * @param postId идентификатор сообщения на стене пользователя, которое необходимо удалить из списка Мне нравится.
     * @return В случае успешного удаления сообщения из списка Мне нравится возвращает текущее количество человек, которые добавили данное сообщение в свой список Мне нравится.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    int deleteLike(int ownerId, int postId) throws IOException, VKException {
        Wall.deleteLike(worker, ownerId, postId)
    }
}