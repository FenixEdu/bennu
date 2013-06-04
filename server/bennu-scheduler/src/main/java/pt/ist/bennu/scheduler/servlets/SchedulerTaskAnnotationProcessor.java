package pt.ist.bennu.scheduler.servlets;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import pt.ist.bennu.scheduler.annotation.Task;
import pt.ist.bennu.scheduler.domain.SchedulerSystem;

@HandlesTypes({ Task.class })
public class SchedulerTaskAnnotationProcessor implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        if (classes != null) {
            for (Class<?> type : classes) {
                final Task annon = type.getAnnotation(Task.class);
                if (annon != null) {
                    SchedulerSystem.addTask(type.getName(), annon);
                }
            }
        }
    }
}
