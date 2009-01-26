package myorg.domain.scheduler;

import java.util.Comparator;

import myorg.domain.MyOrg;

import org.joda.time.DateTime;

public class TaskLog extends TaskLog_Base {
    
    public static final Comparator<TaskLog> COMPARATOR_BY_START = new Comparator<TaskLog>() {

	@Override
	public int compare(final TaskLog taskLog1, final TaskLog taskLog2) {
	    final int c = taskLog1.getTaskStart().compareTo(taskLog2.getTaskStart());
	    return c == 0 ? taskLog1.getIdInternal().compareTo(taskLog2.getIdInternal()) : c;
	}
	
    };

    public TaskLog(final Task task) {
        super();
        setMyOrg(MyOrg.getInstance());
        setTask(task);
        setSuccessful(Boolean.FALSE);
        setTaskStart(new DateTime());
    }

    public void update(final Boolean successful) {
	setTaskEnd(new DateTime());
	setSuccessful(successful);
    }
    
}
