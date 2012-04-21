package net.contextfw.web.commons.cloud.internal.mongo;

import java.util.Random;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * For internal use only
 */
public abstract class MongoBase {

    private static final Logger LOG = LoggerFactory.getLogger(MongoBase.class);

    private final ThreadPoolExecutor executor;
    
    private final DB db;

    protected static final String KEY_HANDLE = "handle";
    protected static final String KEY_VALID_THROUGH = "validThrough";
    protected static final String KEY_LOCKED = "locked";
    protected static final String KEY_REMOTE_ADDR = "remoteAddr";
    
    private long nextCleanup = 0;
    
    private final long removalSchedulePeriod;

    protected MongoBase(DB db, long removalSchedulePeriod) {
        this.db = db;
        this.removalSchedulePeriod = removalSchedulePeriod;
        
        RejectedExecutionHandler handler = new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r,
                    ThreadPoolExecutor executor) {
                        r.run();
                }
        };
        
        executor = new ScheduledThreadPoolExecutor(10, handler);
    }
    
    protected final void setIndexes(DBCollection collection) {
        collection.ensureIndex(KEY_HANDLE);
        collection.ensureIndex(KEY_REMOTE_ADDR);
    }

    protected static DBObject o(String key, Object value) {
        return new BasicDBObject(key, value);
    }
    
    protected BasicDBObjectBuilder b() {
        return BasicDBObjectBuilder.start();
    }

    protected DB getDb() {
        return db;
    }
    
    protected void removeExpiredObjects() {
        final long now = System.currentTimeMillis();
        if (now > nextCleanup) {
            nextCleanup = now + new Random().nextInt((int) removalSchedulePeriod*2);
            executeAsync(new ExceptionSafeExecution() {
                public void execute() {
                    DBCollection collection = getCollection();
                    DBObject query = o(KEY_VALID_THROUGH, o("$lt", now));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Cleaning {} objects from {}", 
                                  collection.count(query), 
                                  collection.getName());
                    }
                    collection.remove(query);
                }
            });
        }
    }
    
    protected void executeAsync(ExceptionSafeExecution execution) {
        executor.execute(execution);
    }
    
    protected abstract DBCollection getCollection();
}
