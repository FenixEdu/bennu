package myorg.domain.scheduler;

import java.io.PrintWriter;

public abstract class CustomTask {

    protected PrintWriter out;

    void setOut(final PrintWriter out) {
	this.out = out;
    }

    public abstract void run();

}
