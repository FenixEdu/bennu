package org.fenixedu.bennu.search.domain;

public class MonthIndex extends MonthIndex_Base implements IntIndex {
    
    public MonthIndex(final YearIndex yearIndex, final int month) {
        super();
        setYearIndex(yearIndex);
        setMonth(month);
    }

    @Override
    public boolean matches(final int value) {
        return getMonth() == value;
    }

}
