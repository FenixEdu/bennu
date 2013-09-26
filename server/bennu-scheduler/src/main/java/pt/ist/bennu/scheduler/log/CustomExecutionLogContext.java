package pt.ist.bennu.scheduler.log;


public class CustomExecutionLogContext extends ExecutionLogContext {

    @Override
    protected String getLogFileName() {
        return CustomExecutionLog.LOG_JSON_FILENAME;
    }

}
