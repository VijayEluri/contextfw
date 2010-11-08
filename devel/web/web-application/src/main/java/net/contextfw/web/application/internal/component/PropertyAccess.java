package net.contextfw.web.application.internal.component;

interface PropertyAccess<T> {
    T getValue(Object obj);
}