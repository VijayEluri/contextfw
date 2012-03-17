/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.commons.cloud.session;

import net.contextfw.web.application.WebApplicationModule;
import net.contextfw.web.application.configuration.Configuration;

import org.junit.Test;

import com.google.inject.AbstractModule;

public class SessionTestModule extends AbstractModule {

    public static final String TEST_DB = "testDb";
    public static final String TEST_COLLECTION = "testSession";

    @Override
    protected void configure() {
        Configuration conf = Configuration.getDefaults()
                .set(MongoCloudSession.COLLECTION_NAME, TEST_COLLECTION);
        install(new WebApplicationModule(conf));
    }
    
    @Test
    public void nullTest() {}

}
