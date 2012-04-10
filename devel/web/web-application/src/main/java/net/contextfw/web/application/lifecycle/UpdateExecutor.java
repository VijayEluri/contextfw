package net.contextfw.web.application.lifecycle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;

/**
 * 
 */
public interface UpdateExecutor {

    
    /**
     * USed mainly througn normal response
     * 
     * @param servlet
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    void update(HttpServlet servlet,
                HttpServletRequest request,
                HttpServletResponse response);
    
    /**
     * Calls remote method, used mainly through web sockets
     * @param pageHandle
     * @param componentId
     * @param method
     * @param parameters
     */
    void update(PageHandle handle, 
                String componentId, 
                String method, 
                List<String> parameters,
                String remoteAddr,
                PrintWriter out);
    
    /**
     * Runs runnable in page scope and creates an update
     * 
     * This is used mainly by web socket response
     * 
     * @param handle
     * @param runnable
     * @param writer
     */
    boolean update(PageHandle handle,
                Callable<Boolean> callable,
                String remoteAddr,
                PrintWriter out);
    
    /**
     * Runs runnable in page scope and creates an update
     * 
     * This is used mainly by comet response
     * 
     * @param handle
     * @param runnable
     * @param writer
     */
    boolean update(PageHandle handle,
                Callable<Boolean> callable,
                String remoteAddr,
                HttpServletResponse response);
    
    /**
     * Runs runnable in page scope and creates an update
     * 
     * This is used mainly by comet response
     * 
     * @param handle
     * @param runnable
     * @param writer
     */
    boolean update(PageHandle handle,
                Callable<Boolean> callable,
                HttpServletRequest request,
                HttpServletResponse response);
    
}
