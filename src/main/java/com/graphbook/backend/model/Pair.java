package com.graphbook.backend.model;

import java.io.Serializable;

/**
 * The {@code Pair} class represents a generic pair of objects.
 * It is a simple container for two related objects of potentially different types.
 * This class is useful for cases where you need to return or handle two objects as a single entity.
 * It implements {@link Serializable}, allowing objects of this class to be serialized.
 * 
 * @param <T1> the type of the first element in the pair
 * @param <T2> the type of the second element in the pair
 */
public class Pair <T1, T2> implements Serializable {
    /**
     * The first element of the pair.
     */
    private T1 el1;

    /**
     * The second element of the pair.
     */
    private T2 el2;

    /**
     * Constructs an empty {@code Pair} instance.
     */
    public Pair() {};

    /**
     * Constructs a {@code Pair} with the provided elements.
     * 
     * @param el1 the first element, cannot be {@code null}
     * @param el2 the second element, cannot be {@code null}
     * @throws IllegalArgumentException if either {@code el1} or {@code el2} is {@code null}
     */
    public Pair(T1 el1, T2 el2) {
        if (el1 == null || el2 == null) {
            throw new IllegalArgumentException("Provided a null element");
        }
        this.el1 = el1;
        this.el2 = el2;
    }

    /**
     * Returns the first element of this pair.
     * 
     * @return the first element
     */
    public T1 getEl1() {
        return el1;
    }

    /**
     * Returns the second element of this pair.
     * 
     * @return the second element
     */
    public T2 getEl2() {
        return el2;
    }

    /**
     * Sets the first element of this pair.
     * 
     * @param el1 the new first element
     */
    public void setEl1(T1 el1) {
        this.el1 = el1;
    }

    /**
     * Sets the second element of this pair.
     * 
     * @param el2 the new second element
     */
    public void setEl2(T2 el2) {
        this.el2 = el2;
    }

    /**
     * Returns a string representation of this pair.
     * The string representation consists of the string representations of the first and second elements of this pair, separated by a newline.
     * 
     * @return a string representation of this pair
     */
    @Override
    public String toString() {
        return "\nElement1: " + el1 + "\nElement2: " + el2;
    }
}