package com.cristianpeter.btree.exceptions.interfaces;

import com.cristianpeter.btree.exceptions.KeyNotFoundException;

public interface ExceptionalBiPredicate<T, U> {

    boolean test(T t, U u) throws KeyNotFoundException;
}
