package pt.ist.bennu.search.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.bennu.search.IndexListener;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

@WebListener
public class BennuSearchContextListener implements ServletContextListener {
    private IndexListener listener = new IndexListener();

    @Override
    public void contextInitialized(ServletContextEvent event) {
        TopLevelTransaction.addCommitListener(listener);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        TopLevelTransaction.removeCommitListener(listener);
    }
}
