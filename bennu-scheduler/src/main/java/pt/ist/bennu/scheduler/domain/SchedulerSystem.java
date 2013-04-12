package pt.ist.bennu.scheduler.domain;

import java.lang.reflect.Modifier;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.Transaction;
import dml.DomainClass;
import dml.DomainModel;

public class SchedulerSystem extends SchedulerSystem_Base {
    private SchedulerSystem() {
        super();
    }

    public static SchedulerSystem getInstance() {
        if (!Bennu.getInstance().hasSchedulerSystem()) {
            Bennu.getInstance().setSchedulerSystem(new SchedulerSystem());
        }
        return Bennu.getInstance().getSchedulerSystem();
    }

    public static void queueTasks() {
        final SchedulerSystem schedulerSystem = getInstance();
        for (final Task task : schedulerSystem.getTaskSet()) {
            if (task.shouldRunNow()) {
                schedulerSystem.queueTasks(task);
            }
        }
    }

    private void queueTasks(final Task task) {
        if (hasPendingTask()) {
            getPendingTask().queue(task);
        } else {
            setPendingTask(task);
        }
    }

    public void initTasks() {
        final DomainModel domainModel = FenixFramework.getDomainModel();
        for (final DomainClass domainClass : domainModel.getDomainClasses()) {
            if (isTaskInstance(domainClass) && !existsTaskInstance(domainClass)) {
                initTask(domainClass);
            }
        }
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

    private boolean existsTaskInstance(final DomainClass domainClass) {
        final String classname = domainClass.getFullName();
        for (final Task task : getTaskSet()) {
            if (task.getClass().getName().equals(classname)) {
                return true;
            }
        }
        return false;
    }

    private void initTask(final DomainClass domainClass) {
        try {
            final Class taskClass = Class.forName(domainClass.getFullName());
            if (!Modifier.isAbstract(taskClass.getModifiers())) {
                taskClass.newInstance();
            }
        } catch (final ClassNotFoundException e) {
            System.out.println("Scheduler: failed initialization of task: " + domainClass.getFullName());
            e.printStackTrace();
        } catch (final InstantiationException e) {
            System.out.println("Scheduler: failed initialization of task: " + domainClass.getFullName());
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            System.out.println("Scheduler: failed initialization of task: " + domainClass.getFullName());
            e.printStackTrace();
        }
    }

    public static void runPendingTask() {
        for (Task task = popPendingTaskService(); task != null; task = popPendingTaskService()) {
            task.runPendingTask();
        }
    }

    private static Task popPendingTaskService() {
        Task result = null;
        boolean committed = false;
        try {
            if (jvstm.Transaction.current() != null) {
                jvstm.Transaction.commit();
            }
            Transaction.begin();

            result = SchedulerSystem.getInstance().popPendingTask();

            jvstm.Transaction.checkpoint();
            committed = true;
        } finally {
            if (!committed) {
                Transaction.abort();
                Transaction.begin();
            }
            Transaction.currentFenixTransaction().setReadOnly();
        }
        return result;
    }

    private Task popPendingTask() {
        final Task task = getPendingTask();
        if (task != null) {
            final Task next = task.getNextTask();
            task.setNextTask(null);
            setPendingTask(next);
            task.setLastRun(new DateTime());
        }
        return task;
    }

    public <T> Task getTask(final Class<? extends Task> clazz) {
        for (final Task task : getTaskSet()) {
            if (task.getClass() == clazz) {
                return task;
            }
        }
        return null;
    }

    public void clearAllScheduledTasks() {
        for (final Task task : getTaskSet()) {
            task.clearAllSchedules();
        }
    }

}
