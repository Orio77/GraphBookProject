package com.graphbook.backend.model;

public class Pair <T1, T2> {
    private T1 el1;
    private T2 el2;

    public Pair() {};

    public Pair(T1 el1, T2 el2) {
        this.el1 = el1;
        this.el2 = el2;
    }

    public T1 getEl1() {
        return el1;
    }
    public T2 getEl2() {
        return el2;
    }
    public void setEl1(T1 el1) {
        this.el1 = el1;
    }
    public void setEl2(T2 el2) {
        this.el2 = el2;
    }
}
