package com.vk.api.users

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
class Users {
    /**
     * Возвращает расширенную информацию о пользователях.
     *
     * @param worker VKWorker
     * @param uids перечисленные через запятую ID пользователей или их короткие имена (screen_name). Максимум 1000 пользователей.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws VKException
     */
    static Iterator<User> get(VKWorker worker, List uids, NameCase nameCase = NameCase.nom) throws IOException, VKException{
        use(DOMCategory){
              worker.executeQuery(new VKRequest('users.get', [
                      uids: uids.join(','),
                      fields: ['uid', 'first_name', 'last_name', 'screen_name'].join(','),
                      name_case: nameCase.name()
              ])).user.collect{
                  new User(
                          it.uid.text().toInteger(),
                          it.first_name.text(),
                          it.last_name.text(),
                          it.screen_name.text()
                  )
              }.iterator()
        }
    }

    static enum NameCase{
        nom,
        gen,
        dat,
        acc,
        ins,
        abl
    }
}
