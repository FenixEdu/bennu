package myorg.domain.scheduler;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import myorg.domain.MyOrg;

public class PendingExecutionTaskQueue extends PendingExecutionTaskQueue_Base implements Queue<Task> {

    public PendingExecutionTaskQueue() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public static PendingExecutionTaskQueue getPendingExecutionTaskQueue() {
	PendingExecutionTaskQueue queue = MyOrg.getInstance().getPendingExecutionTaskQueue();
	if (queue == null) {
	    queue = new PendingExecutionTaskQueue();
	}
	return queue;
    }

    public static List<Task> getPendingExecutionTasks() {
	return getPendingExecutionTaskQueue().getTasks();
    }

    private Task getHead() {
	return getTasks().get(size() - 1);
    }

    private Task removeHead() {
	return getTasks().remove(size() - 1);
    }

    @Override
    public Task element() {
	if (isEmpty()) {
	    throw new NoSuchElementException();
	}
	return getHead();
    }

    @Override
    public boolean offer(Task task) {
	getTasks().add(0, task);
	return true;
    }

    @Override
    public Task peek() {
	if (isEmpty()) {
	    return null;
	}
	return getHead();
    }

    @Override
    public Task poll() {
	if (isEmpty()) {
	    return null;
	}
	Task task = getHead();
	removeHead();
	return task;
    }

    @Override
    public Task remove() {
	if (isEmpty()) {
	    throw new NoSuchElementException();
	}
	Task task = getHead();
	removeHead();
	return task;
    }

    @Override
    public boolean isEmpty() {
	return getTasks().isEmpty();
    }

    @Override
    public int size() {
	return getTasksCount();
    }

    @Override
    public boolean add(Task task) {
	offer(task);
	return true;
    }

    @Override
    public boolean addAll(Collection<? extends Task> c) {
	for (Task task : c) {
	    offer(task);
	}
	return true;
    }

    @Override
    public void clear() {
	getTasks().clear();
    }

    @Override
    public boolean contains(Object o) {
	return getTasks().contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	return getTasks().containsAll(c);
    }

    @Override
    public Iterator<Task> iterator() {
	return getTasks().iterator();
    }

    @Override
    public boolean remove(Object o) {
	return getTasks().remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	return getTasks().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	return getTasks().retainAll(c);
    }

    @Override
    public Object[] toArray() {
	return getTasks().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
	return getTasks().toArray(a);
    }

}
