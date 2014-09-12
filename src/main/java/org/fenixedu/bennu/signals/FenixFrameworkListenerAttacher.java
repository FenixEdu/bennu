package org.fenixedu.bennu.signals;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.Status;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.CommitListener;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.Transaction;

@WebListener
public class FenixFrameworkListenerAttacher implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(FenixFrameworkListenerAttacher.class);

    private final CommitListener listener = new CommitListener() {

        @Override
        public void afterCommit(Transaction transaction) {
            try {
                if (transaction.getStatus() == Status.STATUS_COMMITTED) {
                    Signal.fireAllInCacheOutsideTransaction(transaction);
                }
            } catch (SystemException e) {
                logger.error("Can't fire signals", e);
            }
        }

        @Override
        public void beforeCommit(Transaction transaction) {
            Signal.fireAllInCacheWithinTransaction(transaction);
        }
    };

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        FenixFramework.getTransactionManager().addCommitListener(listener);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        FenixFramework.getTransactionManager().removeCommitListener(listener);
    }
}
