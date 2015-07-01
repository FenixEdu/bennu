package org.fenixedu.bennu.search.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.search.IndexListener;

import pt.ist.fenixframework.FenixFramework;

@WebListener
public class BennuSearchContextListener implements ServletContextListener {
    private IndexListener listener = new IndexListener();

    @Override
    public void contextInitialized(ServletContextEvent event) {
        FenixFramework.getTransactionManager().addCommitListener(listener);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        FenixFramework.getTransactionManager().removeCommitListener(listener);
    }
}
