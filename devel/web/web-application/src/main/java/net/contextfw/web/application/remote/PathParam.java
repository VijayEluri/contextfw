package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * <p>
 *  Defines that the field contains encoded value from URL-path
 * </p>
 * 
 * <p>
 *  Path parameter are encoded to URL-definitions with special syntax and it
 *  works on both path-based and regex-based syntaxes. Path parameters are encoded as
 *  <code>&lt;<i>paramName</i>&gt;</code>.
 * </p>
 * 
 * <h2>General usage</h2>
 * 
 * <p>
 *  This annotation can be applied to view component class properties or methods that take the expected
 *  type as singular parameter. When view components are initialized such methods and parameters
 *  are scanned and values are inserted. By default the class property name or method name is used
 *  to resolve parameter name. This can be overridden by using <code>name</code>.
 * </p>
 * 
 * <h3>Mapping in path-style URLs</h3>
 * 
 * <p>
 *  In path-style URLs path parameter is replaced with <code>*</code> and does not match 
 *  with character <code>/</code>. 
 *  For example, following definition:
 * </p>
 * <blockquote>
 *  <code>/customers/&lt;id&gt;/invoice/&lt;invoiceId&gt;</code>
 * </blockquote>
 * <p>
 *  is translated into following path
 * </p>
 * <blockquote>
 *  <code>/customers&#47;*&#47;invoice&#47;*</code>
 * </blockquote>
 * 
 * <h3>Mapping in regex-style URLs</h3>
 * 
 * <p>
 *  In regex-style URLs path parameter is replaced as default with <code>([^/]+)</code>, so it basically
 *  works the same way as path-based variant. With regex-based path it is also possible to 
 *  create different replacement for path-variable, which happens as 
 *  <code>&lt;<i>paramName:regex</i>&gt;</code>.  For example following definition:
 * </p>
 * <blockquote>
 *  <code>/engine/&lt;id&gt;/mode/<mode:started|stopped></code>
 * </blockquote>
 * <p>
 *  is translated into following path
 * </p>
 * <blockquote>
 *  <code>/engine&#47;([^/]+)&#47;mode/(started|stopped)</code>
 * </blockquote>
 * 
 * <h3>Important caveat on initialization</h3>
 * 
 * <p>
 *  It is important to notice that parameters are not initialized during injection. That is 
 *  parameters are not initialized at @PostConstruct-time. To access them, view component
 *  needs to implement ViewComponent-interface thus initialization is ready when method
 *  initialize() is called. 
 * </p>
 * 
 * <h2>Exceptional handling</h2>
 * 
 * <p>
 *  Because path parameters are about URLs it is very likely that there will be malformed URLs
 *  and those cases must be handled somehow. To tackle those cases there following methods are
 *  used at the moment
 * </p>
 * 
 * <ol>
 *  <li>
 *   <b>Setting to null</b>: This is the default. It should be remembered that if target type is primitive
 *   then an exception will be thrown.
 *  </li>
 *  <li>
 *  </li>
 * </ol>
 * 
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface PathParam {
    /**
     * The name of the path parameter
     */
    String name() default "";
    /**
     * The resolution when mapping parameter to required type fails. 
     */
    ErrorResolution onError() default ErrorResolution.SEND_NOT_FOUND_ERROR;
    /**
     * The resolution when parameter is null
     */
    ErrorResolution onNull() default ErrorResolution.SET_TO_NULL;
}