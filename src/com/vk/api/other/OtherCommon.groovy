package com.vk.api.other

import com.vk.api.Info
import com.vk.api.groups.GroupsCommon
import com.vk.api.users.UsersCommon
import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException

/**
 * @author acmi
 */
class OtherCommon {
    static Info getInfoById(VKAnonymousWorker worker, int id) throws IOException, VKException {
        if (id < 0) {
            return GroupsCommon.getById(worker, -id)
        } else if (id > 0) {
            return UsersCommon.get(worker, id)
        }
        null
    }

    static Info getInfoByScreenName(VKAnonymousWorker worker, String screenName) throws IOException, VKException {
        def group = GroupsCommon.getById(worker, screenName)
        if (group == null) {
            return UsersCommon.get(worker, screenName)
        } else {
            return group
        }
    }
}
