package com.vk.api.users

import com.vk.api.VKWorkerUser

/**
 * @author acmi
 */
class UsersFull extends UsersCommon{
    UsersFull(VKWorkerUser worker) {
        super(worker)
    }

    @Override
    protected VKWorkerUser getWorker() {
        super.getWorker()
    }
}
