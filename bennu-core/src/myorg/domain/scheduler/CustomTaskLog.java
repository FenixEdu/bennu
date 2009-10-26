package myorg.domain.scheduler;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import myorg.domain.MyOrg;

import org.joda.time.DateTime;

public class CustomTaskLog extends CustomTaskLog_Base {

    public static final Comparator<CustomTaskLog> COMPARATORY_BY_UPLOAD_TIME_AND_CLASSNAME = new Comparator<CustomTaskLog>() {

	@Override
	public int compare(final CustomTaskLog o1, final CustomTaskLog o2) {
	    final int d = o1.getUploaded().compareTo(o2.getUploaded());
	    return d == 0 ? compareByName(o1, o2) : d;
	}

	public int compareByName(final CustomTaskLog o1, final CustomTaskLog o2) {
	    final int s = o1.getClassName().compareTo(o2.getClassName());
	    return s == 0 ? o2.hashCode() - o1.hashCode() : s;
	}

    };

    public CustomTaskLog() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public CustomTaskLog(final String className, final String contents, final DateTime uploadTime,
	    final DateTime taskStart, final DateTime taskEnd, final String out) {
	this();
	setClassName(className);
	setContents(contents);
	setUploaded(uploadTime);
	setTaskStart(taskStart);
	setTaskEnd(taskEnd);
	setOutput(out);
    }

    public static SortedSet<CustomTaskLog> getSortedCustomTaskLogs() {
	final SortedSet<CustomTaskLog> sortedCustomTaskLogs = new TreeSet<CustomTaskLog>(COMPARATORY_BY_UPLOAD_TIME_AND_CLASSNAME);
	sortedCustomTaskLogs.addAll(MyOrg.getInstance().getCustomTaskLogSet());
	return sortedCustomTaskLogs;
    }

}
