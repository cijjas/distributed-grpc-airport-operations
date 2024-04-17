package ar.edu.itba.pod.tpe1.models;

public class Pair<V, K> {
    private V left;
    private K right;

    public Pair(V left, K right) {
        this.left = left;
        this.right = right;
    }

    public V getLeft() {
        return left;
    }

    public void setLeft(V left) {
        this.left = left;
    }

    public K getRight() {
        return right;
    }

    public void setRight(K right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
