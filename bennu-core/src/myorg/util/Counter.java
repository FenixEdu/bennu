package myorg.util;

public class Counter<T> {

    T countableObject;
    int counter;

    public Counter(T countableObject) {
	this.countableObject = countableObject;
	this.counter = 0;
    }

    public Counter(T countableObject, int startCounter) {
	this.countableObject = countableObject;
	this.counter = startCounter;
    }

    public void increment() {
	this.counter++;
    }

    public int getValue() {
	return this.counter;
    }

    public T getCountableObject() {
	return this.countableObject;
    }
}
