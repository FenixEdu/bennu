package org.fenixedu.bennu.core.domain;

import java.util.Comparator;
import java.util.Objects;

import jvstm.cps.ConsistencyPredicate;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.joda.time.LocalDate;

/**
 * Represents a period in time in which the associated {@link User} is allowed to log in to the application.
 * 
 * Note that a user may have several active Periods at the same time, but can only have one open period (i.e. a period without a
 * end date).
 * 
 * @author João Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
public class UserLoginPeriod extends UserLoginPeriod_Base implements Comparable<UserLoginPeriod> {
    UserLoginPeriod(User user) {
        setUser(user);
        super.setBeginDate(LocalDate.now());
    }

    /**
     * Creates a {@link UserLoginPeriod} for the given {@link User} with the exact dates.
     * 
     * @param user {@link User} instance
     * @param beginDate {@link LocalDate} when the period started
     * @param endDate {@link LocalDate} when the period ended
     */
    public UserLoginPeriod(User user, LocalDate beginDate, LocalDate endDate) {
        setUser(Objects.requireNonNull(user));
        setBeginDate(Objects.requireNonNull(beginDate, "beginDate cannot be null"));
        setEndDate(Objects.requireNonNull(endDate, "endDate cannot be null"));
    }

    @Override
    public User getUser() {
        // FIXME: remove when the framework supports read-only slots
        return super.getUser();
    }

    @Override
    public LocalDate getBeginDate() {
        // FIXME: remove when the framework supports read-only slots
        return super.getBeginDate();
    }

    @Override
    public LocalDate getEndDate() {
        // FIXME: remove when the framework supports read-only slots
        return super.getEndDate();
    }

    /**
     * Edits this period. Changing the begin date is only allowed if the period is not already started, and changing the end date
     * is only allowed if such date is in the future.
     * 
     * @param beginDate {@link LocalDate} when the period started
     * @param endDate {@link LocalDate} when the period ended
     */
    public void edit(LocalDate beginDate, LocalDate endDate) {
        if (isClosed()) {
            throw BennuCoreDomainException.cannotEditClosedLogin();
        }
        if (!getBeginDate().equals(beginDate) && isStarted()) {
            throw BennuCoreDomainException.cannotEditOpenPeriodStartDate();
        }
        setBeginDate(beginDate);
        setEndDate(endDate);
    }

    /**
     * Returns whether this period is already closed, i.e. its end date is in the past.
     * 
     * @return true if period is closed (ended), false otherwise
     */
    public boolean isClosed() {
        return getEndDate() != null && getEndDate().isBefore(LocalDate.now());
    }

    /**
     * Returns whether this period has already started, i.e. its begin date is not in the future.
     * 
     * @return true if period has started, false otherwise
     */
    public boolean isStarted() {
        return !getBeginDate().isAfter(LocalDate.now());
    }

    /**
     * Returns whether this period matches exactly the given dates.
     * 
     * @param beginDate start {@link LocalDate} to test against
     * @param endDate end {@link LocalDate} to test against
     * @return true if matches, false otherwise
     */
    public boolean matches(LocalDate beginDate, LocalDate endDate) {
        return getBeginDate().equals(beginDate) && Objects.equals(getEndDate(), endDate);
    }

    /**
     * Deletes this period. If the period is already started, it throws an exception.
     * 
     * @see org.fenixedu.bennu.core.domain.UserLoginPeriod#isStarted()
     */
    public void delete() {
        if (isStarted()) {
            throw BennuCoreDomainException.cannotDeleteStartedLoginPeriod();
        } else {
            setUser(null);
            deleteDomainObject();
        }
    }

    @Override
    public int compareTo(UserLoginPeriod o) {
        return Comparator.comparing(UserLoginPeriod::getEndDate,
                Comparator.nullsLast(Comparator.<LocalDate> naturalOrder()).reversed()).compare(this, o);
    }

    @ConsistencyPredicate
    protected boolean checkDateInterval() {
        return getEndDate() == null || !getBeginDate().isAfter(getEndDate());
    }
}
