package net.contextfw.web.commons.cloud.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExceptionSafeExecution implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionSafeExecution.class);
    
    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception e) {
            LOG.info("Error while execution scheduled task", e);
        }
    }

    public abstract void execute() throws Exception;
}
