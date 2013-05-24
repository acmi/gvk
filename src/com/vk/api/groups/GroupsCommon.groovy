package com.vk.api.groups

import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Element

/**
 * @author acmi
 */
class GroupsCommon {
    /**
     * Возвращает информацию о заданной группе или о нескольких группах.
     *
     * @param gids ID групп, перечисленные через запятую, информацию о которых необходимо получить. В качестве ID могут быть использованы короткие имена групп. Максимум 500 групп.
     * @return Итератор групп
     * @throws IOException
     * @throws VKException
     */
    static Iterator<Group> getById(VKAnonymousWorker worker, Collection gids) throws IOException, VKException {
        use(DOMCategory) {
            Element response = worker.executeQuery(new VKRequest('groups.getById', [
                    gids: gids.join(','),
//                    fields: ['gid', 'name', 'screen_name'].join(',')
            ]))
            response.group.collect { Element group ->
                new Group(
                        group.gid.text().toInteger(),
                        group.get('name').text(),
                        group.screen_name.text()

                )
            }.iterator()
        }
    }

    /**
     * Возвращает информацию о заданной группе
     *
     * @param gid ID группы, информацию о которой необходимо получить. В качестве ID может быть использовано короткое имя группы.
     * @return
     * @throws IOException
     * @throws VKException
     */
    static Group getById(VKAnonymousWorker worker, gid) throws IOException, VKException {
        try {
            Iterator<Group> it = getById(worker, [gid])
            if (it.hasNext())
                return it.next()
        } catch (VKException vke) {
            if (vke.code == VKException.ONE_OF_THE_PARAMETERS_SPECIFIED_WAS_MISSING_OR_INVALID)
                return null

            throw vke
        }
        null
    }
}
