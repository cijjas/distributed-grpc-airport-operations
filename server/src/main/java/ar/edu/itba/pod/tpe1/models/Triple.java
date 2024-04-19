package ar.edu.itba.pod.tpe1.models;

import lombok.Getter;

@Getter
public class Triple<V, Q, K> extends Pair<V, K>{
    private Q middle;

    public Triple(V left, Q middle, K right) {
        super(left, right);
        this.middle = middle;
    }

    public void setMiddle(Q middle) {
        this.middle = middle;
    }

    @Override
    public String toString() {
        return "(" + this.getLeft() + ", " + middle + ", " + this.getRight() + ")";
    }
}
