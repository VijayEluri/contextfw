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

package net.contextfw.web.application;

import org.dom4j.Document;

/**
 * This interface defines a generic document processor.
 * 
 * <p>
 *  The document processor is mainly used to post process XSL-file after it has been
 *  constructed from resources. During post processing it is possible to modify the
 *  original XSL-file or read meta data that was inserted into templates.
 * </p>
 * 
 * <p>
 *  An XSL-post-processor can be set through 
 *  {@link net.contextfw.web.application.configuration.Configuration}.
 * </p>
 * 
 * @see net.contextfw.web.application.configuration.Configuration#XSL_POST_PROCESSOR
 */
public interface DocumentProcessor {
	
	void process(Document document);
}
