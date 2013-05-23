package com.vk.worker

import org.w3c.dom.Element

/**
 * @author acmi
 */
interface VKAnonymousWorker {
    Element executeQuery(VKRequest request) throws Exception
}
