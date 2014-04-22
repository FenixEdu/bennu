package org.fenixedu.bennu.scheduler.log;

import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

public class CustomExecutionLog extends ExecutionLog {

    public final static String CUSTOMLOG_JSON_FILENAME = "customExecutionLog.json";
    private String javaCode;
    private String username;

    public CustomExecutionLog(String taskName) {
        super(taskName);
    }

    public void setJavaCode(String javaCode) {
        this.javaCode = javaCode;
    }

    public String getJavaCode() {
        return javaCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected String getLogFilePath() {
        return SchedulerSystem.getLogsPath().concat(CUSTOMLOG_JSON_FILENAME);
    }

    @Override
    protected Object getLock() {
        return CUSTOMLOG_JSON_FILENAME;
    }

    @Override
    public void end() {
        /* There is no need to persist javaCode again since it is already in the file when start runs */
        setJavaCode(null);
        setUsername(null);
        super.end();
    }

}
