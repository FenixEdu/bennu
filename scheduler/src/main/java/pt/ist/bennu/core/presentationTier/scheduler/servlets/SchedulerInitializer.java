package pt.ist.bennu.core.presentationTier.scheduler.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import pt.ist.bennu.core.domain.scheduler.Scheduler;

/**
 * Listener that initializes/shuts down the Scheduler module.
 * 
 * @author Joao Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
@WebListener
public class SchedulerInitializer implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(SchedulerInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	if (!Scheduler.isInitialized()) {
	    Scheduler.initialize();
	    logger.info("Scheduler initialized");
	}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	if (Scheduler.isInitialized()) {
	    Scheduler.shutdown();
	    logger.info("Shutting down Scheduler");
	}
    }
}
