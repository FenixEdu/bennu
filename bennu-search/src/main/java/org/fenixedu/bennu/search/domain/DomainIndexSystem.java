package org.fenixedu.bennu.search.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DomainIndexSystem extends DomainIndexSystem_Base {

    private DomainIndexSystem() {
        super();
        setRoot(FenixFramework.getDomainRoot());
    }

    public static DomainIndexSystem getInstance() {
        return getSystem(() -> createSystem());
    }

    @Atomic(mode = TxMode.WRITE)
    private static DomainIndexSystem createSystem() {
        return getSystem(() -> new DomainIndexSystem());
    }

    private static DomainIndexSystem getSystem(final Supplier<DomainIndexSystem> supplier) {
        final DomainIndexSystem system = FenixFramework.getDomainRoot().getIndexSystem();
        return system == null ? supplier.get() : system;
    }

    static <Index extends IntIndex> Index search(final Stream<Index> stream, final int value, final Supplier<Index> supplier) {
        return stream.filter(index -> index.matches(value)).findAny().orElseGet(supplier);
    }

    static KeyIndex search(final Map<String, KeyIndex> keyIndexMap, final Stream<KeyIndex> stream, final String key, final Supplier<KeyIndex> supplier) {
        final KeyIndex keyIndex = keyIndexMap.get(key);
        if (keyIndex != null) {
            if (FenixFramework.isDomainObjectValid(keyIndex) && keyIndex.getKeyPart().equals(key)) {
                return keyIndex;
            } else {
                keyIndexMap.remove(key, keyIndex);
            }
        }
        return stream.peek(index -> keyIndexMap.putIfAbsent(index.getKeyPart(), index))
                .filter(index -> index.getKeyPart().equals(key))
                .findAny().orElseGet(supplier);
    }

    private KeyIndex search(final KeyIndex keyIndex, final Map<String, KeyIndex> keyIndexMap, final Stream<KeyIndex> stream, final String[] hash) {
        final String key = hash[0];
        final KeyIndex keyIndex0 = search(keyIndexMap, stream, key, () -> createKeyIndex(keyIndex, key));
        return hash.length == 1 ? keyIndex0 : search(keyIndex0, keyIndex0.getKeyIndexMap(), keyIndex0.getKeyIndexSet().stream(), Arrays.copyOfRange(hash, 1, hash.length));
    }

    private YearIndex yearIndexFor(final int year) {
        return search(getYearIndexSet().stream(), year, () -> createYearIndex(year));
    }

    private MonthIndex monthIndexFor(final int year, final int month) {
        final YearIndex yearIndex = yearIndexFor(year);
        return yearIndex.monthIndexFor(month);
    }

    private KeyIndex keyIndexFor(final String key) {
        final String[] hash = KeyIndex.hash(key);
        return search(null, getKeyIndexMap(), getKeyIndexSet().stream(), hash);
    }

    @Atomic(mode = TxMode.WRITE)
    private YearIndex createYearIndex(final int year) {
        return search(getYearIndexSet().stream(), year, () -> new YearIndex(year));
    }

    @Atomic(mode = TxMode.WRITE)
    private KeyIndex createKeyIndex(final KeyIndex keyIndex, final String key) {
        final Set<KeyIndex> set = keyIndex == null ? getKeyIndexSet() : keyIndex.getKeyIndexSet();
        final Map<String, KeyIndex> keyIndexMap = keyIndex == null ? getKeyIndexMap() : keyIndex.getKeyIndexMap();
        return search(keyIndexMap, set.stream(), key, () -> new KeyIndex(keyIndex, key));
    }

    public <T> void index(final int year, final Function<YearIndex, Set<T>> function, final T value) {
        function.apply(yearIndexFor(year)).add(value);
    }

    public <T> void index(final int year, int month, final Function<MonthIndex, Set<T>> function, final T value) {
        function.apply(monthIndexFor(year, month)).add(value);
    }

    public <T> void index(final String key, final Function<KeyIndex, Set<T>> function, final T value) {
        function.apply(keyIndexFor(key)).add(value);
    }

    public <T> Stream<T> search(final int year, final Function<YearIndex, Stream<T>> function) {
        return function.apply(yearIndexFor(year));
    }

    public <T> Stream<T> search(final int year, final int month, final Function<MonthIndex, Stream<T>> function) {
        return function.apply(monthIndexFor(year, month));
    }

    public <T> Stream<T> search(final String key, final Function<KeyIndex, Stream<T>> function) {
        return function.apply(keyIndexFor(key));
    }

    private Map<String, KeyIndex> keyIndexMap = new ConcurrentHashMap<>();

    private Map<String, KeyIndex> getKeyIndexMap() {
        if (keyIndexMap == null) {
            synchronized (DomainIndexSystem.getInstance()) {
                if (keyIndexMap == null) {
                    keyIndexMap = new ConcurrentHashMap<>();
                }
            }
        }
        return keyIndexMap;
    }

}
