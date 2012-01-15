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
package net.contextfw.web.application.development;

/**
 * <p>Logs the XML-response created during each request.</p>
 * 
 * <p>
 *  Logger is enabled only when 
 *    <code>Configuration.DEVELOPMENT_MODE</code>
 *  is <code>true</code>. 
 *  Logging can also be configured via 
 *    <code>Configuration.XML_RESPONSE_LOGGER</code> 
 *  and 
 *    <code>Configurtion.LOG_XML</code>
 * </p>
 * 
 * @author marko.lavikainen@netkoti.fi>
 *
 */
public interface XMLResponseLogger {
    void logXML(String xml);
}
