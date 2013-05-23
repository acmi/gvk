package com.vk.worker

/**
 * @author acmi
 */
interface VKIdentifiedWorker extends VKAnonymousWorker {
    int getUserId()
}