package myorg.presentationTier;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import myorg.domain.MyOrg;
import myorg.domain.scheduler.CustomTaskLog;

import org.joda.time.DateTime;

public class CustomTaskLogAggregate implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Comparator<CustomTaskLogAggregate> COMPARATOR_BY_LAST_UPLOAD_DATE_AND_CLASS_NAME = new Comparator<CustomTaskLogAggregate>() {

	@Override
	public int compare(final CustomTaskLogAggregate o1, final CustomTaskLogAggregate o2) {
	    final int d = o2.getLastUploadDate().compareTo(o1.getLastUploadDate());
	    return d == 0 ? compareByClassName(o1, o2) : d;
	}

	public int compareByClassName(final CustomTaskLogAggregate o1, final CustomTaskLogAggregate o2) {
	    final int d = o1.getClassName().compareTo(o2.getClassName());
	    return d == 0 ? o2.hashCode() - o1.hashCode() : d;
	}
    };

    private SortedSet<CustomTaskLog> customTaskLogs;

    private String className;

    private DateTime lastUploadDate;

    public CustomTaskLogAggregate(String className) {
	setClassName(className);
	setCustomTaskLogs(new TreeSet<CustomTaskLog>(CustomTaskLog.COMPARATOR_BY_UPLOAD_TIME_AND_CLASSNAME));
	searchCustomTaskLogs(className);
    }

    public void setCustomTaskLogs(SortedSet<CustomTaskLog> customTaskLogs) {
	this.customTaskLogs = customTaskLogs;
    }

    public SortedSet<CustomTaskLog> getCustomTaskLogs() {
	return customTaskLogs;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public String getClassName() {
	return className;
    }

    public void setLastUploadDate(DateTime lastUploadDate) {
	this.lastUploadDate = lastUploadDate;
    }

    public DateTime getLastUploadDate() {
	return lastUploadDate;
    }

    public int getSize() {
	return customTaskLogs.size();
    }

    public void searchCustomTaskLogs(String searchClassName) {
	for (CustomTaskLog taskLog : MyOrg.getInstance().getCustomTaskLogSet()) {
	    String taskClassName = taskLog.getClassName();
	    if (taskClassName == null) {
		taskClassName = "";
	    }

	    if (taskClassName.equals(searchClassName)) {
		getCustomTaskLogs().add(taskLog);
		if ((getLastUploadDate() == null) || (getLastUploadDate().isBefore(taskLog.getUploaded()))) {
		    setLastUploadDate(taskLog.getUploaded());
		}
	    }
	}
    }

    public void deleteCustomTaskLogs() {
	for (CustomTaskLog taskLog : getCustomTaskLogs()) {
	    taskLog.delete();
	}
	getCustomTaskLogs().clear();
    }
}
