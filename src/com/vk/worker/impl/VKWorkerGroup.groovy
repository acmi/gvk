package com.vk.worker.impl

import groovy.transform.CompileStatic

/**
 * @author acmi
 */
@CompileStatic
final class VKWorkerGroup extends AbstractVKWorker {
    VKWorkerGroup(String... tokens) {
        super(tokens.length)

        tokens.each { String token ->
            addWorker(token)
        }
    }

    VKWorkerGroup(Collection<String> tokens) {
        super(tokens.size())

        tokens.each { String token ->
            addWorker(token)
        }
    }

    @Override
    void addWorker(String token) {
        super.addWorker(token)
    }

    @Override
    void removeWorker(String token) {
        super.removeWorker(token)
    }
}
