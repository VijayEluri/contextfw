package net.contextfw.web.commons.cloud.mongo;

import com.mongodb.DBObject;

public interface MongoExecution<T> {
    T execute(DBObject object);
}
