package org.fenixedu.bennu.core.domain;

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
public class UserLoginPeriod extends UserLoginPeriod_Base {

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
        return getEndDate() != null && getEndDate().isBefore(new LocalDate());
    }

    /**
     * Returns whether this period has already started, i.e. its begin date is not in the future.
     * 
     * @return true if period has started, false otherwise
     */
    public boolean isStarted() {
        return !getBeginDate().isAfter(new LocalDate());
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

    /**
     * Returns an open (i.e. without end date) period for the given {@link User}, or creates one with today's start date if
     * necessary.
     * 
     * @param user the {@link User} to add the period to
     * @return a {@link UserLoginPeriod} instance
     */
    public static UserLoginPeriod createOpenPeriod(User user) {
        UserLoginPeriod period = getOpenPeriod(user);
        return period == null ? new UserLoginPeriod(user) : period;
    }

    /**
     * Closes the open (i.e. without end date) period for the given {@link User} if it exists.
     * 
     * @param user the {@link User} to close the period
     */
    public static void closeOpenPeriod(User user) {
        UserLoginPeriod period = getOpenPeriod(user);
        if (period != null) {
            period.edit(period.getBeginDate(), new LocalDate());
        }
    }

    // Private API

    private UserLoginPeriod(User user) {
        setUser(user);
        super.setBeginDate(new LocalDate());

        // Open periods means the user has no expiration
        user.setExpiration(null);
    }

    @ConsistencyPredicate
    protected boolean checkDateInterval() {
        return getEndDate() == null || !getBeginDate().isAfter(getEndDate());
    }

    /*
     * Note that each user can only have at most one open period.
     */
    private static UserLoginPeriod getOpenPeriod(User user) {
        for (UserLoginPeriod loginPeriod : user.getLoginValiditySet()) {
            if (loginPeriod.getEndDate() == null) {
                return loginPeriod;
            }
        }
        return null;
    }
}
