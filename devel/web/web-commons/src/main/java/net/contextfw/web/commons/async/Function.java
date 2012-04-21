package net.contextfw.web.commons.async;

public interface Function<IN, OUT> {
    OUT apply(IN in);
}
