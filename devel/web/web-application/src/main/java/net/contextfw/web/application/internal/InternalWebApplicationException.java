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

package net.contextfw.web.application.internal;

public class InternalWebApplicationException extends RuntimeException {

    private static final String PREFIX = "** THIS IS AN INTERNAL EXCEPTION. PLEASE REPORT IT AS A BUG **\n";
    
    private static final long serialVersionUID = 1L;

    public InternalWebApplicationException() {
        super(PREFIX);
    }

    public InternalWebApplicationException(String msg, Throwable throwable) {
        super(PREFIX + msg, throwable);
    }

    public InternalWebApplicationException(String msg) {
        super(PREFIX + msg);
    }

    public InternalWebApplicationException(Throwable throwable) {
        super(PREFIX, throwable);
    }
}
