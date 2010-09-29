package net.contextfw.remoting.fluent;

import java.io.Serializable;

public final class InvocationError implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String msg;
    
    public enum Type {
        NO_BINDING("No implementation bound to '*'"),

        NOT_ALLOWED_METHOD("Invoking method '*' is not allowed."), UNKNOWN_ERROR("An unknown error happened");

        private String msg;

        private Type(String msg) {
            this.msg = msg;
        }

        public String getMsg(String custom) {
            return msg.replaceAll("\\*", custom);
        }
    }
    
    public InvocationError(Type type, String customMsg) {
        this.msg = type.getMsg(customMsg);
    }
    
    public InvocationError(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}