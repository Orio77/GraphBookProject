package com.graphbook.elements;

public class Edge {
    private Fragment fragment1;
    private Fragment fragment2;
    private double similarityScore;

    public Edge(Fragment fragment1, Fragment fragment2) {
        this.fragment1 = fragment1;
        this.fragment2 = fragment2;
        similarityScore = -1;
    }

    public Fragment getFragment1() {
        return fragment1;
    }
    public Fragment getFragment2() {
        return fragment2;
    }
    public double getSimilarityScore() {
        return similarityScore;
    }
}
