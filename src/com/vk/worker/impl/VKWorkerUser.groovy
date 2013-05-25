package com.vk.worker.impl

import com.vk.worker.VKIdentifiedWorker
import groovy.transform.CompileStatic

/**
 * @author acmi
 */
@CompileStatic
final class VKWorkerUser extends AbstractVKWorker implements VKIdentifiedWorker {
    final int userId

    VKWorkerUser(int userId, String token) {
        super(1)

        this.userId = userId

        addWorker(token)
    }
}
