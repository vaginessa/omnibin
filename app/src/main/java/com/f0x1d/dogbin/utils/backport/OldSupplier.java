package com.f0x1d.dogbin.utils.backport;

@FunctionalInterface
public interface OldSupplier<T> {
    T get();
}
