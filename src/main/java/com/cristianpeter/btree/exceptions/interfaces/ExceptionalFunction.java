package com.cristianpeter.btree.exceptions.interfaces;

@FunctionalInterface
public interface ExceptionalFunction<T, R> {
    R apply(T t) throws Exception;
}