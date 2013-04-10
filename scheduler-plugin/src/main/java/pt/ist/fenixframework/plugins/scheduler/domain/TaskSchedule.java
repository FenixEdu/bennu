package pt.ist.fenixframework.plugins.scheduler.domain;

import org.joda.time.DateTime;

public class TaskSchedule extends TaskSchedule_Base {

    public TaskSchedule(final Task task, final Integer minute, final Integer hour, final Integer day, final Integer month,
            final Integer dayofweek) {
        super();
        setTask(task);
        setMinute(minute);
        setHour(hour);
        setDay(day);
        setMonth(month);
        setDayofweek(dayofweek);
    }

    public boolean shouldRunNow() {
        return shouldRunNow(new DateTime());
    }

    public boolean shouldRunNow(final DateTime now) {
        final int min = now.getMinuteOfHour();
        final int hour = now.getHourOfDay();
        final int day = now.getDayOfMonth();
        final int month = now.getMonthOfYear();
        final int dayofweek = now.getDayOfWeek();

        if (matchField(getMinute(), min) && matchField(getHour(), hour) && matchField(getDay(), day)
                && matchField(getMonth(), month) && matchField(getDayofweek(), dayofweek)) {

            final DateTime lastRun = getLastRun();
            return lastRun == null || nowIsAfterLastRun(now, lastRun);
        }

        return false;
    }

    private boolean matchField(final Integer field, final int value) {
        return field == null || field.intValue() == value;
    }

    private DateTime getLastRun() {
        return getTask().getLastRun();
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

    public void delete() {
        removeTask();
        super.deleteDomainObject();
    }

}
