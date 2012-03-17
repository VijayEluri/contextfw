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

import static org.easymock.EasyMock.createNiceMock;
import org.easymock.EasyMock;

public abstract class AbstractTest {

    protected <T> T createMock(Class<T> cl) {
        return createNiceMock(cl);
    }
    
    protected <T> T createStrictMock(Class<T> cl) {
        return EasyMock.createStrictMock(cl);
    }
    
    protected void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }
}
