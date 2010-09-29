package net.contextfw.remoting.fluent;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.contextfw.remoting.fluent.InvocationError.Type;

public final class ProxiedInvocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Set<String> nonAllowedMethods;
    
    static {
        nonAllowedMethods = new HashSet<String>();
        
        nonAllowedMethods.add("equals");
        nonAllowedMethods.add("toString");
        nonAllowedMethods.add("getClass");
        nonAllowedMethods.add("hashCode");
        nonAllowedMethods.add("notify");
        nonAllowedMethods.add("notifyAll");
        nonAllowedMethods.add("clone");
        nonAllowedMethods.add("finalize");
        nonAllowedMethods.add("wait");
    }
    
    private final Class<?> remotedInterface;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final Object[] parameters;

    private ProxiedInvocation nextInvocation;

    ProxiedInvocation(Class<?> remotedInterface, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.remotedInterface = remotedInterface;
    }

    ProxiedInvocation setNextInvocation(ProxiedInvocation nextInvocation) {
        this.nextInvocation = nextInvocation;
        return nextInvocation;
    }

    public Object invoke(Object obj) {

        try {
            InvocationError error = getNonAllowedMethodError(methodName);
            
            if (error == null) {
                Method method = obj.getClass().getMethod(methodName, parameterTypes);
    
                Object rv = method.invoke(obj, parameters);
                
                if (rv instanceof InvocationError) {
                    return rv;
                } else {
                    return nextInvocation == null ? rv : nextInvocation.invoke(rv);
                }
            }
            else {
                return error;
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            return new InvocationError(Type.UNKNOWN_ERROR, null);
        }
    }

    static InvocationError getNonAllowedMethodError(String methodName) {
        if (nonAllowedMethods.contains(methodName)) {
            return new InvocationError(Type.NOT_ALLOWED_METHOD, methodName);
        } else {
            return null;
        }
    }
    
    Object invokeRemoted(RemotingConnection connection) {
        return connection.invoke(remotedInterface, this);
    }
}