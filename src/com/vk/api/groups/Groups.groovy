package com.vk.api.groups

import com.vk.api.VKException
import com.vk.api.VKRequest
import com.vk.api.VKWorker
import groovy.transform.PackageScope
import groovy.transform.PackageScopeTarget
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
@PackageScope([PackageScopeTarget.METHODS])
class Groups {
    /**
     * Возвращает информацию о заданной группе или о нескольких группах.
     *
     * @param worker VKWorker
     * @param gids ID групп, перечисленные через запятую, информацию о которых необходимо получить. В качестве ID могут быть использованы короткие имена групп. Максимум 500 групп.
     * @return Итератор групп
     * @throws IOException
     * @throws VKException
     */
    static Iterator<Group> getById(VKWorker worker, List gids) throws IOException, VKException{
        use(DOMCategory) {
            def response = worker.executeQuery(new VKRequest('groups.getById', [
                    gids: gids.join(','),
//                    fields: fields.join(',')
            ]))
            response.group.collect {
                new Group(
                        it.gid.text().toInteger(),
                        it.name.text(),
                        it.screen_name.text()

                )
            }.iterator()
        }
    }
}
