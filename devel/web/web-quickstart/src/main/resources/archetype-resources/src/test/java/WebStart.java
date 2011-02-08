#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebStart {

    private static final int HTTP_PORT = 8080;
    private static final String WAR_PATH = "src/main/webapp";
    private static final String CONTEXT_PATH = "/";

    public static void main(String[] args) throws Exception {
        
        Server server = new Server();
        
        WebAppContext wac = new WebAppContext();
        wac.setContextPath(CONTEXT_PATH);
        wac.setWar(WAR_PATH);
        
        server.addHandler(wac);
        server.addHandler(new DefaultHandler());
        
        SelectChannelConnector scc = new SelectChannelConnector();
        scc.setPort(HTTP_PORT);
        server.addConnector(scc);
        server.start();
    }
}