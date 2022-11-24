/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Signals.
 *
 * Bennu Signals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Signals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Signals.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.signals;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.CommitListener;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.Transaction;

import javax.transaction.Status;
import javax.transaction.SystemException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * This is the main class for the signaling system. This implements a system wide event bus that allows writing code in one module
 * that observes and operates over another set of code.
 * 
 * @author Artur Ventura
 * 
 */
public class Signal {

    private static final Logger logger = LoggerFactory.getLogger(Signal.class);

    private static final Map<String, Set<Consumer<Object>>> immediate = new ConcurrentHashMap<>();
    private static final Map<String, SignalEventBus> withTransaction = new ConcurrentHashMap<>();
    private static final Map<String, SignalEventBus> withoutTransaction = new ConcurrentHashMap<>();

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
        innerClear();
        FenixFramework.getTransactionManager().removeCommitListener(listener);
    }

    /**
     * Registers a handler for events of that key that runs within the same transaction as the emited events. This means that this
     * handler can still abort the transaction.
     * 
     * @param key
     *            The key in which to register this handler.
     * @param handler
     *            The handler to register.
     * @return
     *         The {@link HandlerRegistration} associated with this handler
     */
    public static HandlerRegistration register(String key, Object handler) {
        return registerInBus(key, handler, withTransaction, true);
    }

    /**
     * Registers the given {@link Consumer} as a handler for events, with the same semantics as {@link #register(String, Object)}.
     * 
     * The advantage of this method is that it is lambda-ready, removing the necessity of creating an {@link EventBus} compatible
     * class.
     * 
     * @param <T>
     *            The type of the object sent with the signal
     * @param key
     *            The key in which to register this handler.
     * @param handler
     *            The handler to register.
     * @return
     *         The {@link HandlerRegistration} associated with this handler.
     * @throws NullPointerException
     *             If either key or handler are null.
     */
    public static <T> HandlerRegistration register(String key, Consumer<T> handler) {
        return register(key, new LambdaHandler<T>(handler));
    }

    /**
     * Registers a handler for events of that key. This handler will run outside the transaction and only after the commit is
     * successful. If you want the chance to abort the transaction, use {@link #register(String, Object)} .
     * 
     * @param key
     *            The key in which to register this handler.
     * @param handler
     *            The handler to register.
     * @return
     *         The {@link HandlerRegistration} associated with this handler.
     */
    public static HandlerRegistration registerWithoutTransaction(String key, Object handler) {
        return registerInBus(key, handler, withoutTransaction, false);
    }

    /**
     * Registers a handler for events of that key. This handler will run immediately inside the current transaction.
     *
     * @param key
     *            The key in which to register this handler.
     * @param handler
     *            The handler to register.
     * @return
     *         The {@link HandlerRegistration} associated with this handler.
     */
    public static <T> void registerImmediate(String key, Consumer<T> handler) {
        final Set handlers = immediate.computeIfAbsent(key, (k) -> Collections.synchronizedSet(new HashSet<>()));
        handlers.add(handler);
    }

    /**
     * Registers the given {@link Consumer} as a handler for events, with the same semantics as
     * {@link #registerWithoutTransaction(String, Object)}.
     * 
     * The advantage of this method is that it is lambda-ready, removing the necessity of creating an {@link EventBus} compatible
     * class.
     * 
     * @param <T>
     *            The type of the object sent with the signal
     * @param key
     *            The key in which to register this handler.
     * @param handler
     *            The handler to register.
     * @return
     *         The {@link HandlerRegistration} associated with this handler.
     * @throws NullPointerException
     *             If either key or handler are null.
     */
    public static <T> HandlerRegistration registerWithoutTransaction(String key, Consumer<T> handler) {
        return registerWithoutTransaction(key, new LambdaHandler<T>(handler));
    }

    private static HandlerRegistration registerInBus(String key, Object handler, Map<String, SignalEventBus> eventBuses,
            boolean throwsException) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(handler, "handler");
        eventBuses.computeIfAbsent(key, (k) -> new SignalEventBus(k, throwsException)).register(handler);
        return new HandlerRegistration(key, handler);
    }

    /**
     * Clears all handlers for a given key
     * 
     * @param key
     *            the key to be cleared
     */
    public static void clear(String key) {
        withTransaction.remove(key);
        withoutTransaction.remove(key);
        immediate.remove(key);
    }

    /**
     * Clears all event handlers. This method emits a warning because the uses cases for this method are mostly restricted to
     * development enviroments.
     * 
     */
    public static void clear() {
        logger.warn("Detaching all handlers. Be sure what you are doing.");
        innerClear();
    }

    private static void innerClear() {
        withTransaction.clear();
        withoutTransaction.clear();
        immediate.clear();
    }

    /**
     * Unregisters a handler for a given key.
     * 
     * @param key
     *            the context key
     * @param handler
     *            the handler to be removed
     */
    public static void unregister(String key, Object handler) {
        SignalEventBus with = withTransaction.get(key);
        SignalEventBus without = withoutTransaction.get(key);
        Set imm = immediate.get(key);
        if (with != null) {
            with.unregister(handler);
        }
        if (without != null) {
            without.unregister(handler);
        }
        if (imm != null) {
            imm.remove(handler);
        }
    }

    /**
     * Unregisters a handler in all keys.
     * 
     * @param handler to be removed
     */
    public static void unregister(Object handler) {
        withTransaction.forEach((key, bus) -> bus.unregister(handler));
        withoutTransaction.forEach((key, bus) -> bus.unregister(handler));
        immediate.forEach((key, bus) -> bus.remove(handler));
    }

    /**
     * Emits a event.
     * 
     * @param key the key for that signal
     * @param event the event object
     */
    public static void emit(String key, Object event) {
        if (withoutTransaction.containsKey(key)) {
            Map<String, List<Object>> cache = FenixFramework.getTransaction().getFromContext("signals");
            if (cache == null) {
                cache = new HashMap<>();
                FenixFramework.getTransaction().putInContext("signals", cache);
            }
            cache.computeIfAbsent(key, (k) -> new ArrayList<Object>()).add(event);
        }

        if (withTransaction.containsKey(key)) {
            Map<String, List<Object>> cache = FenixFramework.getTransaction().getFromContext("signalsWithTransaction");
            if (cache == null) {
                cache = new HashMap<>();
                FenixFramework.getTransaction().putInContext("signalsWithTransaction", cache);
            }
            cache.computeIfAbsent(key, (k) -> new ArrayList<Object>()).add(event);
        }

        final Set<Consumer<Object>> consumers = immediate.get(key);
        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(event));
        }
    }

    private static void fireAllInCacheOutsideTransaction(Transaction transaction) {
        Map<String, ArrayList<Object>> cache = transaction.getFromContext("signals");
        /* allowing signal emiting within a signal */
        while (cache != null) {
            transaction.putInContext("signals", null);
            for (Entry<String, ArrayList<Object>> entry : cache.entrySet()) {
                for (Object event : entry.getValue()) {
                    withoutTransaction.get(entry.getKey()).emit(event);
                }
            }
            cache = transaction.getFromContext("signals");
        }
    }

    private static void fireAllInCacheWithinTransaction(Transaction transaction) {
        Map<String, ArrayList<Object>> cache = transaction.getFromContext("signalsWithTransaction");
        while (cache != null) {
            transaction.putInContext("signalsWithTransaction", null);
            for (Entry<String, ArrayList<Object>> entry : cache.entrySet()) {
                for (Object event : entry.getValue()) {
                    withTransaction.get(entry.getKey()).emit(event);
                }
            }
            cache = transaction.getFromContext("signalsWithTransaction");
        }
    }

    private static final class LambdaHandler<T> {

        private final Consumer<T> consumer;

        public LambdaHandler(Consumer<T> consumer) {
            this.consumer = Objects.requireNonNull(consumer);
        }

        @Subscribe
        public void handleEvent(T event) {
            this.consumer.accept(event);
        }

    }
}
