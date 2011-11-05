package net.contextfw.web.commons.cloud.mongo;

import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public abstract class MongoBase {

    private static final int TRY_OUTS = 100;

    private static final int SLEEP_PERIOD = 100;

    private final DB db;

    private static final String KEY_HANDLE = "handle";
    private static final String KEY_VALID_THROUGH = "validThrough";
    private static final String KEY_LOCKED = "locked";
    private static final String KEY_REMOTE_ADDR = "remoteAddr";
    
    private long nextCleanup = 0;
    
    private final long removalSchedulePeriod;

    protected MongoBase(DB db, long removalSchedulePeriod) {
        this.db = db;
        this.removalSchedulePeriod = removalSchedulePeriod;
    }

    protected <T> T executeExclusive(DBCollection collection, 
                                   String handle,
                                   String remoteAddr,
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
        
        DBObject obj = collection.findOne(query);
        
        if (obj != null) {
            DBObject opened = openExclusive(collection, query);
            if (opened == null) {
                opened = obj;
            }
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
    
    private DBObject openExclusive(DBCollection collection, DBObject query) {
        query.put(KEY_LOCKED, false);
        DBObject update = o("$set", o(KEY_LOCKED, true));
        for (int i = 0; i < TRY_OUTS; i++) {
            DBObject rv = collection.findAndModify(query, update);
            if (rv == null) {
                try {
                    Thread.sleep(SLEEP_PERIOD);
                } catch (InterruptedException e) {
                }
            } else {
                return rv;
            }
        }
        return null;
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
    
    protected void removeExpiredObjects(DBCollection collection) {
        long now = System.currentTimeMillis();
        if (now > nextCleanup) {
            collection.remove(o(KEY_VALID_THROUGH, o("$lt", now)));
            nextCleanup = now + new Random().nextInt((int) removalSchedulePeriod*2);
        }
    }
}
