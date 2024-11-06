package org.fenixedu.bennu.scheduler.task;

import java.lang.reflect.Method;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.log.ElasticsearchLogRepository;

import com.google.gson.JsonObject;

public class DebugSchedulerLogs extends CustomTask {
  @Override
  public void runTask() throws Exception {
    final ElasticsearchLogRepository repository = (ElasticsearchLogRepository) SchedulerSystem.getLogRepository();
    final Method readIndexJsonMethod = ElasticsearchLogRepository.class.getDeclaredMethod("readIndexJson");
    readIndexJsonMethod.setAccessible(true);
    final JsonObject taskIndex = (JsonObject) readIndexJsonMethod.invoke(repository);
    taskLog(taskIndex.toString());
  } 
}