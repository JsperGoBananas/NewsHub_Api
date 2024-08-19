package com.jl.newshubapi.model.entity;

public class Tuple<X,Y> {
    private final X x;
    private final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getFirst() {
        return x;
    }

    public Y getSecond() {
        return y;
    }
}
