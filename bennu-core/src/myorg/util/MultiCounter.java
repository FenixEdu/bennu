package myorg.util;

import java.util.HashMap;
import java.util.Map;

public class MultiCounter<T> {
    T countableObject;
    Map<String, Counter<T>> counters;

    public MultiCounter(T countableObject, String... counterNames) {
	this.countableObject = countableObject;
	counters = new HashMap<String, Counter<T>>();
	for (String name : counterNames) {
	    counters.put(name, new Counter<T>(countableObject));
	}
    }

    public T getCountableObject() {
	return this.countableObject;
    }

    public Counter<T> getCounter(String counterName) {
	return counters.get(counterName);
    }

    public void increment(String counterName) {
	getCounter(counterName).increment();
    }
}
