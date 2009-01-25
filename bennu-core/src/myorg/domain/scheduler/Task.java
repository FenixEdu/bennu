package myorg.domain.scheduler;

import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixWebFramework.FenixWebFramework;
import dml.DomainClass;
import dml.DomainModel;

public abstract class Task extends Task_Base {

    public Task() {
        super();
    }

    public static Set<DomainClass> getAllTaskDomainClasses() {
	final Set<DomainClass> tasks = new HashSet<DomainClass>();
	final DomainModel domainModel = FenixWebFramework.getDomainModel();
	for (final DomainClass domainClass : domainModel.getDomainClasses()) {
	    if (isTaskInstance(domainClass)) {
		tasks.add(domainClass);
	    }
	}
	return tasks;
    }

    private static boolean isTask(final DomainClass domainClass) {
	return domainClass != null && domainClass.getFullName().equals(Task.class.getName());
    }

    private static boolean isTaskInstance(final DomainClass domainClass) {
	if (domainClass == null || isTask(domainClass)) {
	    return false;
	}
	final DomainClass superclass = (DomainClass) domainClass.getSuperclass();
	return isTask(superclass) || isTaskInstance(superclass);
    }

}
