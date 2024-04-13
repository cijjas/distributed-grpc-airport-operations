package ar.edu.itba.pod.tpe1.models;

public class Counter implements Comparable<Counter>{
    private final Integer counterId;
    private Boolean isBusy;

    public Counter(Integer id, Boolean isBusy) {
        this.counterId = id;
        this.isBusy = isBusy;
    }

    @Override
    public int compareTo(Counter other) {
        return Integer.compare(this.counterId, other.counterId);
    }

    public Integer getCounterId() {
        return counterId;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }
}
