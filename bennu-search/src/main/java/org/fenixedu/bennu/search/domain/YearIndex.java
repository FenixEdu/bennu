package org.fenixedu.bennu.search.domain;

import pt.ist.fenixframework.Atomic;

public class YearIndex extends YearIndex_Base implements IntIndex {

    public YearIndex(final int year) {
        super();
        setYear(year);
        setIndexSystem(DomainIndexSystem.getInstance());
    }

    @Override
    public boolean matches(final int value) {
        return getYear() == value;
    }

    public MonthIndex monthIndexFor(final int month) {
        return DomainIndexSystem.search(getMonthIndexSet().stream(), month, () -> createMonthIndex(month));
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private MonthIndex createMonthIndex(final int month) {
        return DomainIndexSystem.search(getMonthIndexSet().stream(), month, () -> new MonthIndex(this, month));
    }

}
