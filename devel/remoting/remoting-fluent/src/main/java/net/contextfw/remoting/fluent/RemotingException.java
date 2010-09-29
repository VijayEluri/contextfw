package net.contextfw.remoting.fluent;

public class RemotingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RemotingException(InvocationError error) {
        super(error.getMsg());
    }
}