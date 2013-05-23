package com.vk.worker.impl

import com.vk.worker.VKException
import com.vk.worker.VKIdentifiedWorker
import com.vk.worker.VKRequest
import groovy.transform.CompileStatic
import org.w3c.dom.Element

import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author acmi
 */
@CompileStatic
final class VKWorkerUser extends AbstractVKWorker implements VKIdentifiedWorker {
    private static final Logger log = Logger.getLogger(VKWorkerUser.class.getName())

    final int userId
    final String token

    private final Timer executionTimer

    VKWorkerUser(int userId, String token) {
        this.userId = userId
        this.token = token

        Task workerTask = new Task(token, requests)
        executionTimer = new Timer()
        executionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                workerTask.run()
            }
        }, 0, WAIT_TIME_MILLIS)
    }

    @Override
    void stop() {
        executionTimer.cancel()
    }

    @Override
    protected String _executeQuery(VKRequest request) throws IOException, InterruptedException {
        if (executionTimer == null)
            log.log(Level.WARNING, "Executor not started")

        super._executeQuery(request)
    }

    @Override
    Element executeQuery(VKRequest request) throws IOException, VKException {
        try {
            return super.executeQuery(request)
        } catch (VKException vke) {
            if (vke.code == VKException.TOO_MANY_REQUESTS_PER_SECOND) {
                executeQuery(request)
            } else {
                throw vke
            }
        }
    }
}
