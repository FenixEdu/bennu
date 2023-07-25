package org.fenixedu.bennu.core.domain.groups;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.runtime.Relation;

import com.google.common.collect.Lists;

class GroupGC {
    private static final Logger logger = LoggerFactory.getLogger(GroupGC.class);

    private static Map<Class<? extends PersistentGroup>, Predicate<PersistentGroup>> testers = new ConcurrentHashMap<>();

    private static Map<Class<? extends PersistentGroup>, Consumer<PersistentGroup>> cleaners = new ConcurrentHashMap<>();

    public static void gc() {
        List<PersistentGroup> candidates = new ArrayList<>();
        AtomicInteger total = new AtomicInteger(-1);
        LongAdder processed = new LongAdder();
        int sweep = 0;
        int totalCleared = 0;

        do {
            total.set(Bennu.getInstance().getGroupSet().size());
            processed.reset();
            totalCleared += candidates.size();
            candidates.clear();

            logger.debug("Starting sweep #{}", ++sweep);

            Timer timer = new Timer();
            try {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        logger.debug("Processed {} of {} (found {} candidates for deletion)", processed.intValue(), total,
                                candidates.size());
                    }
                }, 0, 30 * 1000);
                Lists.partition(new ArrayList<>(Bennu.getInstance().getGroupSet()), 10000).stream().parallel()
                        .forEach(p -> processor(p, candidates, processed));
            } finally {
                timer.cancel();
            }

            logger.debug("Finished sweep #{}, clearing {} candidates", sweep, candidates.size());
            if (!candidates.isEmpty()) {
                List<List<PersistentGroup>> partitions = Lists.partition(candidates, 1000);
                logger.debug("Processing {} clear chunks", partitions.size());
                int i = 0;
                for (List<PersistentGroup> partition : partitions) {
                    FenixFramework.atomic(() -> partition.forEach(PersistentGroup::gc));
                    logger.debug("\tProcessed {} out of {}", ++i, partitions.size());
                }
            }
        } while (!candidates.isEmpty());
        logger.debug("GC completed. {} total objects collected", totalCleared);
    }

    @Atomic(mode = TxMode.READ)
    private static void processor(List<PersistentGroup> groups, List<PersistentGroup> candidates, LongAdder processed) {
        for (PersistentGroup group : groups) {
            if (!(group instanceof PersistentDynamicGroup) && emptyCustomRelations(group)) {
                candidates.add(group);
            }
            processed.increment();
        }
    }

    public static void cleanContext(PersistentGroup group) {
        Consumer<PersistentGroup> cleaner = cleaners.computeIfAbsent(group.getClass(), type -> {
            Consumer<PersistentGroup> result = ignore -> {
            };

            Set<String> relations = group.getContextRelations().stream().map(Relation::getName).collect(Collectors.toSet());
            DomainClass model = FenixFramework.getDomainModel().findClass(type.getName());
            for (Role role : fullRoles(model)) {
                if (relations.contains(role.getRelation().getName())) {
                    if (role.getMultiplicityUpper() == 1) {
                        result = result.andThen(g -> cleanToOneRelation(g, setter(type, role)));
                    } else {
                        result = result.andThen(g -> cleanToManyRelation(g, getter(type, role)));
                    }
                }
            }
            return result;
        });
        cleaner.accept(group);
    }

    static boolean emptyCustomRelations(PersistentGroup group) {
        Predicate<PersistentGroup> predicate =
                testers.computeIfAbsent(
                        group.getClass(),
                        type -> {
                            Predicate<PersistentGroup> result = ignored -> true;
                            Set<String> relations =
                                    group.getContextRelations().stream().map(Relation::getName).collect(Collectors.toSet());

                            List<String> getters = new ArrayList<>();
                            DomainClass model = FenixFramework.getDomainModel().findClass(type.getName());
                            for (Role role : fullRoles(model)) {
                                if (relations.contains(role.getRelation().getName()) || role.getName().equals("root")) {
                                    continue;
                                }
                                Method method = getter(type, role);
                                getters.add(method.getName());
                                result = result.and(g -> testEmptyRole(g, method, role.getMultiplicityUpper() == 1));
                            }
                            logger.debug("Registering getters for type {}, that should be empty {}", group.getClass()
                                    .getSimpleName(), getters.stream().collect(Collectors.joining(", ")));
                            return result;
                        });
        return predicate.test(group);
    }

    private static List<Role> fullRoles(DomainClass model) {
        List<Role> roles = new ArrayList<>();
        roles.addAll(model.getRoleSlotsList());
        if (model.hasSuperclass()) {
            roles.addAll(fullRoles((DomainClass) model.getSuperclass()));
        }
        return roles;
    }

    private static boolean testEmptyRole(PersistentGroup group, Method method, boolean single) {
        try {
            return single ? method.invoke(group) == null : ((Collection<?>) method.invoke(group)).isEmpty();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return false;
        }
    }

    private static void cleanToOneRelation(PersistentGroup group, Method method) {
        try {
            method.invoke(group, (Object) null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.debug("Could not clear relation of group {} with setter {}", group.getExternalId(), method.getName());
        }
    }

    private static void cleanToManyRelation(PersistentGroup group, Method method) {
        try {
            ((Collection<?>) method.invoke(group)).clear();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.debug("Could not clear relation of group {} with setter {}", group.getExternalId(), method.getName());
        }
    }

    private static Method getter(Class<?> type, Role role) {
        String name = "get" + capitalize(role.getName());
        try {
            try {
                Method method = type.getDeclaredMethod(name);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
            }
            if (type.getSuperclass() != null) {
                return getter(type.getSuperclass(), role);
            }
        } catch (SecurityException e) {
        }
        throw new Error(String.format("Unexpected inexistence of method: %s for role %s%n", name, role.getName()));
    }

    private static Method setter(Class<?> type, Role role) {
        String name = "set" + capitalize(role.getName());
        try {
            try {
                Method method = type.getDeclaredMethod(name, Class.forName(role.getType().getFullName())); // nosemgrep
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
            } catch (ClassNotFoundException e) {
            }
            if (type.getSuperclass() != null) {
                return setter(type.getSuperclass(), role);
            }
        } catch (SecurityException e) {
        }
        throw new Error(String.format("Unexpected inexistence of method: %s for role %s%n", name, role.getName()));
    }

    private static String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}
