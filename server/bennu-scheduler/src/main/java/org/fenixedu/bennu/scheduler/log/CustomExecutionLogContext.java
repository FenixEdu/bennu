package org.fenixedu.bennu.scheduler.log;


public class CustomExecutionLogContext extends ExecutionLogContext {

    @Override
    protected String getLogFileName() {
        return CustomExecutionLog.CUSTOMLOG_JSON_FILENAME;
    }

}
