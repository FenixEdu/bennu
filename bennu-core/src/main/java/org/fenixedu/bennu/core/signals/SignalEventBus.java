package org.fenixedu.bennu.core.signals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

class SignalEventBus extends EventBus {

    private static final Logger logger = LoggerFactory.getLogger(SignalEventBus.class);

    private final SignalLoggingSubscriberExceptionHandler exceptionHandler;
    private boolean throwsExceptions = false;

    private SignalEventBus(SignalLoggingSubscriberExceptionHandler handler) {
        super(handler);
        exceptionHandler = handler;
    }

    public SignalEventBus(String identifier) {
        this(new SignalLoggingSubscriberExceptionHandler(identifier));
    }

    public SignalEventBus(String identifier, boolean throwsExceptions) {
        this(new SignalLoggingSubscriberExceptionHandler(identifier));
        this.throwsExceptions = throwsExceptions;
    }

    public void emit(Object event) throws RuntimeException {
        super.post(event);
        Throwable e = exceptionHandler.getException();
        if (e != null) {
            exceptionHandler.clear();
            if (throwsExceptions) {
                throw new RuntimeException("Exceptions while emiting signals", e);
            }
        }
    }

    private static final class SignalLoggingSubscriberExceptionHandler implements SubscriberExceptionHandler {

        private final String identifier;

        public Throwable exception;

        public SignalLoggingSubscriberExceptionHandler(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void handleException(Throwable exception, SubscriberExceptionContext context) {
            logger.error("Could not emit signal: " + context.getSubscriber() + " to " + context.getSubscriberMethod() + "("
                    + identifier + ")", exception);
            this.exception = exception;
        }

        public Throwable getException() {
            return exception;
        }

        public void clear() {
            exception = null;
        }
    }

}
