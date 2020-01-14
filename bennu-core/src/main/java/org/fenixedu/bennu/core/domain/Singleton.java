package org.fenixedu.bennu.core.domain;

import pt.ist.fenixframework.Atomic;

import java.util.function.Supplier;

public class Singleton {

    public static <S> S getInstance(final Supplier<S> getter, final Supplier<S> creator) {
        return getSingleton(getter, () -> createSingleton(getter, creator));
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private static <S> S createSingleton(final Supplier<S> getter, final Supplier<S> creator) {
        return getSingleton(getter, creator);
    }

    private static <S> S getSingleton(final Supplier<S> getter1, final Supplier<S> getter2) {
        final S singleton = getter1.get();
        return singleton == null ? getter2.get() : singleton;
    }

}
