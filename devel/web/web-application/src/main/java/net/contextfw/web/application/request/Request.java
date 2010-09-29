package net.contextfw.web.application.request;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides functionality to access and set GET/POST-parameters through
 * consistent abstraction.
 * 
 * <p>
 * Request is an abstraction that provides a set of functionalities to extend
 * the usage of GET/POST-parameters. It tries to make handling parameters more
 * convinient.
 * </p>
 * 
 * <h3>Features and benefits</h3>
 * <ol>
 * <li>
 * Helps testing the application, because <code>Request</code> is an interface
 * and can be replaced with test-requests.</li>
 * <li>
 * Provides an easy way to store parameters for further processing.</li>
 * </ol>
 * 
 * @author Marko Lavikainen
 * 
 */
public class Request {

    private Map<String, RequestParameter> params = null;

    private Map<String, Request> subRequests = null;

    private HttpServletRequest httpRequest;

    private String name = "";

    private Long defaultIndex = null;

    public static final String REQUEST_SEPARATOR = ".";

    /**
     * Sole constructor.
     * 
     */
    public Request(HttpServletRequest httpRequest) {
        params = new HashMap<String, RequestParameter>();
        subRequests = new HashMap<String, Request>();
        this.httpRequest = httpRequest;
    }

    public void reinitialize(HttpServletRequest httpRequest) {
        params.clear();
        subRequests.clear();
        this.httpRequest = httpRequest;
    }

    private Request(String parentName, String name, HttpServletRequest httpRequest) {

        this(httpRequest);

        if (parentName == null)
            this.name = name + REQUEST_SEPARATOR;
        else
            this.name = parentName + name + REQUEST_SEPARATOR;
    }

    private Request(String parentName, String name, Long defaultIndex, HttpServletRequest httpRequest) {
        this(parentName, name, httpRequest);
        this.defaultIndex = defaultIndex;
    }

    public Request subRequest(String name) {

        if (!subRequests.containsKey(name)) {
            subRequests.put(name, new Request(this.name, name, httpRequest));
        }

        return subRequests.get(name);
    }

    public Request subRequest(String name, long defaultIndex) {

        String key = name + Long.toHexString(defaultIndex);

        if (!subRequests.containsKey(key)) {
            subRequests.put(key, new Request(this.name, name, defaultIndex, httpRequest));
        }

        return subRequests.get(key);
    }

    /**
     * Fetches a parameter from page data with given name
     * 
     * <p>
     * This method is the only way to create new parameters. If parameter does
     * not yet exist, it is automatically created an returned. Otherwise already
     * existing parameter is returned
     * </p>
     * 
     * <p>
     * If a parameter without a name is tried to be fetched, then exception is
     * thrown.
     * </p>
     * 
     * @param name
     *            Name of the parameter
     * 
     * @return Newly created paremeter or existing on
     * 
     * @throws NullPointerException
     *             Thrown is name is <code>null</code>.
     */
    public RequestParameter param(String name) throws NullPointerException {

        String formattedName = formatName(name);

        if (!params.containsKey(formattedName)) {
            params.put(formattedName, new RequestParameter(name, formattedName, httpRequest));
        }

        return params.get(formattedName);
    }

    /**
     * Fetches a parameter from page data with given name and index
     * 
     * <p>
     * This is a convience method to fetch parameters with numeral index.
     * </p>
     * 
     * <p>
     * The index is simply appended to the end of the name for instance
     * <code>name2</code>. Otherwise behaviour is same with the single name.
     * </p>
     * 
     * @param name
     *            Name of the parameter
     * @param index
     *            Index of the parameter
     * 
     * @return Newly created paremeter or existing on
     * 
     * @throws NullPointerException
     *             Thrown is name is <code>null</code>.
     */
    public RequestParameter param(String name, int index) throws NullPointerException {

        String formattedName = formatName(name, index);

        if (name == null) {
            throw new NullPointerException();
        }

        if (!params.containsKey(formattedName)) {
            params.put(formattedName, new RequestParameter(name, formattedName, httpRequest));
        }

        return params.get(formattedName);
    }

    private String formatName(String name) {
        if (defaultIndex != null)
            return this.name + name + defaultIndex;
        else
            return this.name + name;
    }

    private String formatName(String name, long index) {
        return this.name + name + REQUEST_SEPARATOR + index;
    }
}