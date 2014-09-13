package org.fenixedu.bennu.signals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Status;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.CommitListener;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.Transaction;

import com.google.common.eventbus.EventBus;

/**
 * This is the main class for the signaling system. This implements a system wide event bus that allows writing code in one module
 * that observes and operates over another set of code.
 * 
 * @author Artur Ventura
 * 
 */
public class Signal {

    private static final Logger logger = LoggerFactory.getLogger(Signal.class);

    private static final Map<String, EventBus> withTransaction = new ConcurrentHashMap<>();
    private static final Map<String, EventBus> withoutTransaction = new ConcurrentHashMap<>();
    private static final Map<String, HashSet<Object>> handlers = new ConcurrentHashMap<>();

    private static final CommitListener listener = new CommitListener() {

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

    /**
     * Initializes the signaling system. This registers the required FenixFramework commit listeners.
     */
    public static void init() {
        FenixFramework.getTransactionManager().addCommitListener(listener);
    }

    /**
     * Shuts down the signaling system. This removes all registered event handlers,
     * and removes the FenixFramework transactional listener.
     */
    public static void shutdown() {
        clear();
        FenixFramework.getTransactionManager().removeCommitListener(listener);
    }

    /**
     * Registers a handler for events of that key. This handler will run outside the transaction and only after the commit is
     * successful. If you want the chance to abort the transaction, use {@link #registerWithTransaction(String, Object)} .
     * 
     * @param key
     * @param handler
     */
    public static HandlerRegistration register(String key, Object handler) {
        return registerInBus(key, handler, withoutTransaction);
    }

    /**
     * Registers a handler for events of that key that runs within the same transaction as the emited events. This means that this
     * handler can still abort the transaction.
     * 
     * @param key
     * @param handler
     */
    public static HandlerRegistration registerWithTransaction(String key, Object handler) {
        return registerInBus(key, handler, withTransaction);
    }

    private static HandlerRegistration registerInBus(String key, Object handler, Map<String, EventBus> eventBuses) {
        HashSet<Object> handlerSet = handlers.get(key);

        if (handlerSet == null) {
            handlerSet = new HashSet<Object>();
            handlers.put(key, handlerSet);
        } else if (handlerSet.contains(handler)) {
            return new HandlerRegistration(key, handler);
        }

        handlerSet.add(handler);
        EventBus bus;
        if (!eventBuses.containsKey(key)) {
            bus = new EventBus(key);
            withTransaction.put(key, bus);
        } else {
            bus = eventBuses.get(key);
        }
        bus.register(handler);
        return new HandlerRegistration(key, handler);
    }

    /**
     * Clears all handlers for a given key
     * 
     * @param the key to be cleared
     */
    public static void clear(String key) {
        if (handlers.containsKey(key)) {
            HashSet<Object> hlr = handlers.get(key);
            EventBus with = withTransaction.get(key);
            EventBus without = withoutTransaction.get(key);
            for (Object handler : hlr) {
                if (with != null) {
                    with.unregister(handler);
                }
                if (without != null) {
                    without.unregister(handler);
                }
            }
            withTransaction.remove(key);
            withoutTransaction.remove(key);
            handlers.remove(key);
        }
    }

    /**
     * Clears all event handlers. This method emits a warning because the uses cases for this method are mostly restricted to
     * development enviroments.
     * 
     */
    public static void clear() {
        logger.warn("Detaching all handlers. Be sure what you are doing.");
        for (String key : handlers.keySet()) {
            clear(key);
        }
    }

    /**
     * Unregisters a handler for a given key.
     * 
     * @param the context key
     * @param the handler to be removed
     */
    public static void unregister(String key, Object handler) {
        if (handlers.containsKey(key)) {
            HashSet<Object> hlr = handlers.get(key);
            EventBus with = withTransaction.get(key);
            EventBus without = withoutTransaction.get(key);
            if (with != null) {
                with.unregister(handler);
            }
            if (without != null) {
                without.unregister(handler);
            }
            hlr.remove(handler);
        }
    }

    /**
     * Unregisters a handler in all keys.
     * 
     * @param handler to be removed
     */
    public static void unregister(Object handler) {
        for (String key : handlers.keySet()) {
            unregister(key, handler);
        }
    }

    /**
     * Emits a event.
     * 
     * @param key the key for that signal
     * @param event the event
     */
    public static void emit(String key, Object event) {
        if (withoutTransaction.containsKey(key)) {
            HashMap<String, ArrayList<Object>> cache = FenixFramework.getTransaction().getFromContext("signals");

            if (cache == null) {
                cache = new HashMap<>();
            }

            ArrayList<Object> list = cache.get(key);

            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(event);
            cache.put(key, list);

            FenixFramework.getTransaction().putInContext("signals", cache);
        }

        if (withTransaction.containsKey(key)) {
            HashMap<String, ArrayList<Object>> cache = FenixFramework.getTransaction().getFromContext("signalsWithTransaction");

            if (cache == null) {
                cache = new HashMap<>();
            }

            ArrayList<Object> list = cache.get(key);

            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(event);
            cache.put(key, list);

            FenixFramework.getTransaction().putInContext("signalsWithTransaction", cache);
        }
    }

    private static void fireAllInCacheOutsideTransaction(Transaction transaction) {
        HashMap<String, ArrayList<Object>> cache = transaction.getFromContext("signals");
        if (cache != null) {
            for (String key : cache.keySet()) {
                for (Object event : cache.get(key)) {
                    withoutTransaction.get(key).post(event);
                }
            }
            transaction.putInContext("signals", null);
        }
    }

    private static void fireAllInCacheWithinTransaction(Transaction transaction) {
        HashMap<String, ArrayList<Object>> cache = transaction.getFromContext("signalsWithTransaction");
        if (cache != null) {
            for (String key : cache.keySet()) {
                for (Object event : cache.get(key)) {
                    withTransaction.get(key).post(event);
                }
            }
            transaction.putInContext("signalsWithTransaction", null);
        }
    }
}
