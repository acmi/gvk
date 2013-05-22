package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.VKException
import com.vk.api.VKWorker
import com.vk.api.likes.Like

/**
 * @author acmi
 */
class WallCommon {
    private final VKWorker worker

    WallCommon(VKWorker worker) {
        this.worker = worker
    }

    protected VKWorker getWorker() { worker }

/**
 * Возвращает список записей со стены пользователя или сообщества.
 *
 * @param ownerId идентификатор пользователя. Чтобы получить записи со стены группы (публичной страницы, встречи), укажите её идентификатор со знаком "минус": например, ownerId=-1 соответствует группе с идентификатором 1.
 * @param offset смещение, необходимое для выборки определенного подмножества сообщений.
 * @param filter определяет, какие типы сообщений на стене необходимо получить. Если параметр не задан, то считается, что он равен <b>all</b>.
 * @return Итератор постов
 * @exception RuntimeException ( wrapped IOException , VKException )
 */
    Iterator<Post> get(int ownerId, int offset = 0, Wall.Filter filter = Wall.Filter.all) {
        Wall.get(worker, ownerId, offset, filter)
    }

    /**
     * Возвращает список записей со стен пользователей по их идентификаторам.
     *
     * @param posts перечисленные через запятую идентификаторы, которые представляют собой идущие через знак подчеркивания id владельцев стен и id самих записей на стене. Пример: 93388_21539,93388_20904,2943_4276
     * @return Итератор постов
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Iterator<Post> getById(List<Identifier> posts) throws IOException, VKException {
        Wall.getById(worker, posts)
    }

    /**
     * Возвращает список комментариев к записи на стене пользователя.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится запись, к которой необходимо получить комментарии.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, необходимое для выборки определенного подмножества комментариев.
     * @param sort порядок сортировки комментариев
     * @return Итератор комментариев
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    Iterator<Comment> getComments(int ownerId, int postId, int offset = 0, Wall.Sort sort = Wall.Sort.asc) {
        Wall.getComments(worker, ownerId, postId, offset, sort)
    }

    /**
     * Получает информацию о пользователях, которые добавили указанную запись в свой список <b>Мне нравится</b>. Список пользователей отсортирован в порядке убывания добавления записи в список <b>Мне нравится</b>.
     *
     * @param ownerId идентификатор пользователя, на чьей стене находится запись.
     * @param postId идентификатор записи на стене пользователя.
     * @param offset смещение, относительно начала списка, для выборки определенного подмножества.
     * @param publishedOnly указывает, что необходимо вернуть информацию только пользователях, опубликовавших данную запись у себя на стене.
     * @param friendsOnly указывает, необходимо ли возвращать только пользователей, которые являются друзьями текущего пользователя.
     * @return Итератор лайков
     * @exception RuntimeException ( wrapped IOException , VKException )
     */
    Iterator<Like> getLikes(int ownerId, int postId, int offset = 0, boolean publishedOnly = false, boolean friendsOnly = false) {
        Wall.getLikes(worker, ownerId, postId, offset, publishedOnly, friendsOnly)
    }
}
