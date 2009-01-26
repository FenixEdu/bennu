package myorg.domain.scheduler;

import java.io.Serializable;

import pt.ist.fenixWebFramework.util.DomainReference;

public class TaskConfigurationBean implements Serializable {

    private DomainReference<Task> task;
    private Integer minute;
    private Integer hour;
    private Integer day;
    private Integer month;
    private Integer dayofweek;

    public TaskConfigurationBean(final Task task) {
	setTask(task);
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDayofweek() {
        return dayofweek;
    }

    public void setDayofweek(Integer dayofweek) {
        this.dayofweek = dayofweek;
    }

    public Task getTask() {
        return task == null ? null : task.getObject();
    }

    public void setTask(final Task task) {
        this.task = task == null ? null : new DomainReference<Task>(task);
    }

    public void create() {
	final Task task = getTask();
	task.createTaskConfiguration(this);
    }

}
