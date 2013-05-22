package com.vk.api

import org.w3c.dom.Element

/**
 * @author acmi
 */
interface VKWorker {
    Element executeQuery(VKRequest request) throws Exception
}
