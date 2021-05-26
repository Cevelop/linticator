package com.linticator.functional;

public interface Function2<T, U, V> {
	V apply(T t, U u);
}