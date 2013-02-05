/*
 * @(#)TaskConfiguration.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package pt.ist.bennu.core.domain.scheduler;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

public class TaskConfiguration extends TaskConfiguration_Base {

    public TaskConfiguration(final Task task) {
        super();
        setMyOrg(MyOrg.getInstance());
        setTask(task);
    }

    public TaskConfiguration(final TaskConfigurationBean taskConfigurationBean) {
        this(taskConfigurationBean.getTask());
        setMinute(taskConfigurationBean.getMinute());
        setHour(taskConfigurationBean.getHour());
        setDay(taskConfigurationBean.getDay());
        setMonth(taskConfigurationBean.getMonth());
        setDayofweek(taskConfigurationBean.getDayofweek());
    }

    @Service
    public void delete() {
        removeTask();
        removeMyOrg();
        deleteDomainObject();
    }

    public boolean shouldRunNow() {
        return shouldRunNow(new DateTime());
    }

    public boolean shouldRunNow(DateTime now) {
        final int min = now.getMinuteOfHour();
        final int hour = now.getHourOfDay();
        final int day = now.getDayOfMonth();
        final int month = now.getMonthOfYear();
        final int dayofweek = now.getDayOfWeek();

        if (matchField(getMinute(), min) && matchField(getHour(), hour) && matchField(getDay(), day)
                && matchField(getMonth(), month) && matchField(getDayofweek(), dayofweek)) {

            final DateTime lastRun = getLastRun();
            return lastRun == null || nowIsAfterLastRun(now, lastRun);
        }

        return false;
    }

    private boolean nowIsAfterLastRun(final DateTime now, final DateTime lastRun) {
        if (now.getYear() > lastRun.getYear()) {
            return true;
        }
        if (now.getMonthOfYear() > lastRun.getMonthOfYear()) {
            return true;
        }
        if (now.getDayOfMonth() > lastRun.getDayOfMonth()) {
            return true;
        }
        if (now.getHourOfDay() > lastRun.getHourOfDay()) {
            return true;
        }
        if (now.getMinuteOfHour() > lastRun.getMinuteOfHour()) {
            return true;
        }
        return false;
    }

    private DateTime getLastRun() {
        return getTask().getLastRun();
    }

    private boolean matchField(final Integer field, final int value) {
        return field == null || field.intValue() == value;
    }

}
