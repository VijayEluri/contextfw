package net.contextfw.web.commons.cloud.mongo;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public abstract class MongoBase {

    private static final Logger LOG = LoggerFactory.getLogger(MongoBase.class);

    private final ThreadPoolExecutor cleaner = new ScheduledThreadPoolExecutor(1);
    
    private static final int TRY_OUTS = 100;

    private static final int SLEEP_PERIOD = 100;

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
    }

    protected <T> T executeSynchronized(DBCollection collection, 
                                   String handle,
                                   DBObject fields,
                                   String remoteAddr,
                                   boolean execOnNull,
                                   MongoExecution<T> execution) {
        
        BasicDBObjectBuilder queryBuilder = b().add(KEY_HANDLE, handle);

        if (remoteAddr != null) {
            queryBuilder.add(KEY_REMOTE_ADDR, remoteAddr);
        }
        
        queryBuilder
            .push(KEY_VALID_THROUGH)
            .add("$gte", System.currentTimeMillis())
            .pop();

        DBObject query = queryBuilder.get();
        
        boolean exists = collection.count(query) == 1;
        
        if (exists || execOnNull) {
            DBObject opened = exists ? openExclusive(collection, query, fields) : null;
            try {
                return execution.execute(opened);
            } finally {
                closeExclusive(collection, query);
            }
        } else {
            return null;
        }
    }
    
    private void closeExclusive(DBCollection collection, DBObject query) {
        query.removeField(KEY_LOCKED);
        collection.update(query, o("$set", o(KEY_LOCKED, false)));
    }
    
    private DBObject openExclusive(DBCollection collection, DBObject query, DBObject fields) {
        query.put(KEY_LOCKED, false);
        DBObject update = o("$set", o(KEY_LOCKED, true));
        for (int i = 0; i < TRY_OUTS; i++) {
            DBObject rv = collection.findAndModify(
                    query, 
                    fields, 
                    null,
                    false,
                    update,
                    true,
                    false);
            
            if (rv == null) {
                try {
                    Thread.sleep(SLEEP_PERIOD);
                } catch (InterruptedException e) {
                }
            } else {
                return rv;
            }
        }
        return collection.findOne(query, fields);
    }
    
    protected DBObject o(String key, Object value) {
        return new BasicDBObject(key, value);
    }
    
    protected BasicDBObjectBuilder b() {
        return BasicDBObjectBuilder.start();
    }

    protected DB getDb() {
        return db;
    }
    
    protected void removeExpiredObjects(final DBCollection collection) {
        cleaner.execute(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now > nextCleanup) {
                    DBObject query = o(KEY_VALID_THROUGH, o("$lt", now));
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Cleaning {} objects from {}", collection.count(query), 
                            collection.getName());
                    }
                    collection.remove(query);
                    nextCleanup = now + new Random().nextInt((int) removalSchedulePeriod*2);
                }                
            }
        });
    }
}
