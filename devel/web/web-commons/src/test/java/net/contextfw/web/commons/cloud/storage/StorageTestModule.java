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

package net.contextfw.web.commons.cloud.storage;

import net.contextfw.web.application.WebApplicationModule;
import net.contextfw.web.application.configuration.Configuration;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class StorageTestModule extends AbstractModule {

    public static final String TEST_DB = "testDb";
    public static final String TEST_COLLECTION = "testSession";
    public static final String SINGLETON_SCOPED_MSG="I'm singleton scoped";
    
    
    @Override
    protected void configure() {
        install(new WebApplicationModule(Configuration.getDefaults()));
    }
    
    @Test
    public void nullTest() {}
    
    @Provides
    @Singleton
    public SingletonScoped provideSingletonScoped() {
        return new SingletonScoped(SINGLETON_SCOPED_MSG);
    }

}
