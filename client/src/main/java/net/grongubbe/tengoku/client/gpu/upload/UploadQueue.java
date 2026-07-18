package net.grongubbe.tengoku.client.gpu.upload;

import net.grongubbe.tengoku.client.render.RenderThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class UploadQueue {
    private static final Logger LOGGER = LogManager.getLogger(UploadQueue.class);

    private final Queue<UploadTask> tasks = new ConcurrentLinkedQueue<>();

    public void submit(UploadTask task) {
        tasks.add(task);
    }

    public void process() {
        RenderThread.assertCurrent();
        LOGGER.trace("Processing upload tasks");

        UploadTask task;

        while ((task = tasks.poll()) != null) {
            task.execute();
        }
    }
}