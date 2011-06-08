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
 *  {@link net.contextfw.web.application.properties.Properties}.
 * </p>
 * 
 * @see net.contextfw.web.application.properties.Properties#XSL_POST_PROCESSOR
 */
public interface DocumentProcessor {
	
	void process(Document document);
}
