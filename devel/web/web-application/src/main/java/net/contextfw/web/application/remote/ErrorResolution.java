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

package net.contextfw.web.application.remote;

/**
 * Denotes the resolutions what should be done on exceptional case.
 */
public enum ErrorResolution {
    /**
     * Sets the variable to null.
     * 
     * <p>
     *  Note, if underlaying parameter is primitive, this may fail and throw another
     *  exception. That exception is not caught.
     * </p>
     */
    SET_TO_NULL,
    /**
     * Rethrows the exception causing the failure forward.
     */
    RETHROW_CAUSE,
    /**
     * Stop the initialization from being executed and send Not Found (404) error
     * to client.
     */
    SEND_NOT_FOUND_ERROR,
    /**
     * Stop the initialization from being executed and send Bad Request (400) error
     * to client.
     */
    SEND_BAD_REQUEST_ERROR
}
