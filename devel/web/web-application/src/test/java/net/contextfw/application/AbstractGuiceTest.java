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

package net.contextfw.application;

import net.contextfw.application.GuiceJUnitRunner.GuiceModules;

import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestModule.class })
public abstract class AbstractGuiceTest extends AbstractTest {

    @Inject
    private Injector injector;
    
    protected <T> T getMember(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
    
    protected <T> T injectMembers(T instance) {
        injector.injectMembers(instance);
        return instance;
    }
}
