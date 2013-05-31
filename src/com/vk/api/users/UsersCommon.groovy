package com.vk.api.users

import com.vk.api.VKIterator
import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

/**
 * @author acmi
 */
class UsersCommon {
    /**
     * Возвращает расширенную информацию о пользователях.
     *
     * @param worker {@link VKAnonymousWorker}
     * @param uids перечисленные через запятую ID пользователей или их короткие имена (screen_name). Максимум 1000 пользователей.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws VKException
     */
    static List<User> get(VKAnonymousWorker worker, Collection uids, NameCase nameCase = null) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('users.get', [
                    uids: uids.join(','),
                    fields: ['uid', 'first_name', 'last_name', 'screen_name'].join(','),
                    name_case: nameCase != null ? nameCase.name() : null
            ])).user.collect {
                new User(
                        it.uid.text().toInteger(),
                        it.first_name.text(),
                        it.last_name.text(),
                        it.screen_name.text()
                )
            }
        }
    }

    /**
     * Возвращает расширенную информацию о пользователе.
     *
     * @param worker VKAnonymousWorker
     * @param uids ID пользователя или их короткое имя (screen_name).
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws VKException
     */
    static User get(VKAnonymousWorker worker, uid, NameCase nameCase = null) throws IOException, VKException {
        try {
            List<User> list = get(worker, [uid], nameCase)
            if (!list.empty)
                return list[0]
        } catch (VKException vke) {
            if (vke.code == VKException.INVALID_USER_ID)
                return null

            throw vke
        }
        null
    }

    /**
     *
     * @param worker {@link VKAnonymousWorker}
     * @param q строка поискового запроса.
     * @param sortDate сортировка результатов: true - по дате регистрации, false - по популярности
     * @param offset смещение относительно первого найденного пользователя для выборки определенного подмножества.
     * @return
     */
    static Iterator<User> search(VKAnonymousWorker worker, String q, boolean sortDate = false, int offset = 0) {
        new VKIterator<User>(worker, 'users.search', [
                q: q,
                sort: sortDate ? 1 : 0,
                fields: ['uid', 'first_name', 'last_name', 'screen_name'].join(','),
        ], offset) {
            {
                setBufferSize(1000)
            }

            @Override
            protected int getCount() throws Exception {
                Math.min(super.getCount(), 500)   //почему-то контакт не отдает больше 500
            }

            @Override
            protected void fillBuffer(Element response, Queue<User> buffer) throws Exception {
                use(DOMCategory) {
                    response.user.each {
                        buffer << new User(
                                it.uid.text().toInteger(),
                                it.first_name.text(),
                                it.last_name.text(),
                                it.screen_name.text(),
                        )
                    }
                }
            }
        }
    }

    /**
     * Данный метод возвращает информацию о том, установил ли текущий пользователь приложение или нет.
     *
     * @param worker {@link VKAnonymousWorker}
     * @param uid ID пользователя.
     * @return Метод isAppUser возвращает true в случае, если пользователь установил у себя данное приложение, иначе false.
     * @throws IOException
     * @throws VKException
     */
    static boolean isAppUser(VKAnonymousWorker worker, int uid) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('users.isAppUser', [uid: uid])).text() == '1'
        }
    }

    /**
     * Возвращает список идентификаторов пользователей и групп, которые входят в список подписок пользователя.
     *
     * @param worker {@link VKAnonymousWorker}
     * @param uid идентификатор пользователя, подписки которого необходимо получить.
     * @return список идентификаторов пользователей и пбличных станиц, на которые подписан пользователь.
     * @throws IOException
     * @throws VKException
     */
    static List<Integer> getSubscriptions(VKAnonymousWorker worker, int uid) throws IOException, VKException {
        use(DOMCategory) {
            def subcriptions = []

            def response = worker.executeQuery(new VKRequest('users.getSubscriptions', [uid: uid]))
            response.users.items.uid.each {
                subcriptions << it.text().toInteger()
            }
            response.groups.items.gid.each {
                subcriptions << -it.text().toInteger()
            }

            subcriptions
        }
    }

    /**
     * Возвращает список идентификаторов пользователей, которые являются подписчиками пользователя. Идентификаторы пользователей в списке отсортированы в порядке убывания времени их добавления.
     * @param worker {@link VKAnonymousWorker}
     * @param uid идентификатор пользователя.
     * @param offset смещение, необходимое для выборки определенного подмножества подписчиков.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return итератор <b>незаблокированных</b> подписчиков.
     */
    static Iterator<User> getFollowers(VKAnonymousWorker worker, int uid, int offset = 0, NameCase nameCase = NameCase.nom) {
        new VKIterator<User>(worker, 'users.getFollowers', [
                uid: uid,
                fields: ['uid', 'first_name', 'last_name', 'screen_name'].join(','),
                name_case: nameCase != null ? nameCase.name() : null
        ], offset) {
            {
                setBufferSize(1000)
            }

            @Override
            protected void fillBuffer(Element response, Queue<User> buffer) throws Exception {
                use(DOMCategory) {
                    response.items.user.each {
                        if (it.deactivated.size() == 0)
                            buffer << new User(
                                    it.uid.text().toInteger(),
                                    it.first_name.text(),
                                    it.last_name.text(),
                                    it.screen_name.text(),
                            )
                    }
                }
            }
        }
    }
}
