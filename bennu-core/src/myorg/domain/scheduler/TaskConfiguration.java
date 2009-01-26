package myorg.domain.scheduler;

import myorg.domain.MyOrg;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public class TaskConfiguration extends TaskConfiguration_Base {

    public TaskConfiguration(final Task task) {
        super();
        setMyOrg(MyOrg.getInstance());
        setTask(task);
    }

    public TaskConfiguration(final TaskConfigurationBean taskConfigurationBean) {
        this(taskConfigurationBean.getTask());
        setMinute(taskConfigurationBean.getMinute());
        setHour(taskConfigurationBean.getHour());
        setDay(taskConfigurationBean.getDay());
        setMonth(taskConfigurationBean.getMonth());
        setDayofweek(taskConfigurationBean.getDayofweek());
    }

    @Service
    public void delete() {
	removeTask();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    public boolean shouldRunNow() {
	final DateTime now = new DateTime();
	final int min = now.getMinuteOfHour();
	final int hour = now.getHourOfDay();
	final int day = now.getDayOfMonth();
	final int month = now.getMonthOfYear();
	final int dayofweek = now.getDayOfWeek();

	if (matchField(getMinute(), min)
		&& matchField(getHour(), hour)
		&& matchField(getDay(), day)
		&& matchField(getMonth(), month)
		&& matchField(getDayofweek(), dayofweek)) {

	    final DateTime lastRun = getLastRun();
	    return lastRun == null || nowIsAfterLastRun(now, lastRun);
	}

	return false;
    }

    private boolean nowIsAfterLastRun(final DateTime now, final DateTime lastRun) {
	if (now.getYear() > lastRun.getYear()) {
	    return true;
	}
	if (now.getMonthOfYear() > lastRun.getMonthOfYear()) {
	    return true;
	}
	if (now.getDayOfMonth() > lastRun.getDayOfMonth()) {
	    return true;
	}
	if (now.getHourOfDay() > lastRun.getHourOfDay()) {
	    return true;
	}
	if (now.getMinuteOfHour() > lastRun.getMinuteOfHour()) {
	    return true;
	}
	return false;
    }

    private DateTime getLastRun() {
	return getTask().getLastRun();
    }

    private boolean matchField(final Integer field, final int value) {
	return field == null || field.intValue() == value;
    }

}
