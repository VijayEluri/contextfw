package net.contextfw.web.commons.cloud.storage;

import java.net.UnknownHostException;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.commons.AbstractGuiceTest;
import net.contextfw.web.commons.GuiceJUnitRunner.GuiceModules;
import net.contextfw.web.commons.cloud.internal.serializer.Serializer;
import net.contextfw.web.commons.cloud.session.SessionTestModule;

import org.junit.Before;

import com.google.inject.Inject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@GuiceModules({StorageTestModule.class })
public abstract class AbstractStorageTest extends AbstractGuiceTest {

    protected static final String FOOBAR = "foobar";

    protected static final String COOKIE_NAME = "cloudSession";
    
    @Inject
    protected Configuration conf;
    
    @Inject
    protected Serializer serializer;
    
    @Before
    public void setup() throws UnknownHostException, MongoException {
        super.setup();
        Mongo mongo = new Mongo();
        mongo.dropDatabase(SessionTestModule.TEST_DB);
        db = mongo.getDB(SessionTestModule.TEST_DB);
    }
    
    protected DB db;
}
