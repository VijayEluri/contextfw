package net.contextfw.web.application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultXMLResponseLogger implements XMLResponseLogger {

    private Logger logger = LoggerFactory.getLogger(DefaultXMLResponseLogger.class);
    
    @Override
    public void logXML(String xml) {
        logger.info("Logged xml-response:\n{}", xml);
    }

}
