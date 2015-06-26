package org.fenixedu.bennu.core.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserLoginPeriod;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class LoginPeriodTest {

    @Test
    public void testComparator() {
        User user1 = newTestUser();

        LocalDate longTimeAgo = LocalDate.now().minusDays(30);
        LocalDate fifteenDaysAgo = LocalDate.now().minusDays(15);
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);

        user1.openLoginPeriod();
        user1.createLoginPeriod(longTimeAgo, fiveDaysAgo);
        user1.createLoginPeriod(longTimeAgo, tenDaysAgo);
        user1.createLoginPeriod(longTimeAgo, fifteenDaysAgo);
        List<UserLoginPeriod> periods = new ArrayList<UserLoginPeriod>(user1.getLoginValiditySet());
        periods.sort(Comparator.naturalOrder());

        assertEquals(null, periods.get(0).getEndDate());
        assertEquals(fiveDaysAgo, periods.get(1).getEndDate());
        assertEquals(tenDaysAgo, periods.get(2).getEndDate());
        assertEquals(fifteenDaysAgo, periods.get(3).getEndDate());
    }

    @Test
    public void testUsersBornActive() {
        User user1 = newTestUser();
        assertTrue(!user1.isLoginExpired());
    }

    @Test
    public void testClosePeriodOnNewUser() {
        User user1 = newTestUser();

        user1.closeLoginPeriod();
        assertTrue(user1.isLoginExpired());
        assertEquals(1, user1.getLoginValiditySet().size());
    }

    @Test
    public void testOpenPeriodOnNewUser() {
        User user1 = newTestUser();

        user1.openLoginPeriod();
        assertEquals(null, user1.getExpiration().orElse(null));
        assertEquals(1, user1.getLoginValiditySet().size());
    }

    @Test
    public void testOpenOnClosed() {
        User user1 = newTestUser();

        user1.closeLoginPeriod();
        user1.openLoginPeriod();
        assertEquals(null, user1.getExpiration().orElse(null));
        assertEquals(2, user1.getLoginValiditySet().size());
    }

    @Test
    public void testOpenOnOpen() {
        User user1 = newTestUser();

        user1.openLoginPeriod();
        user1.openLoginPeriod();
        assertEquals(null, user1.getExpiration().orElse(null));
        assertEquals(1, user1.getLoginValiditySet().size());
    }

    @Test
    public void testCloseOnOpen() {
        User user1 = newTestUser();

        user1.openLoginPeriod();
        user1.closeLoginPeriod();
        assertTrue(user1.isLoginExpired());
        assertEquals(1, user1.getLoginValiditySet().size());
    }

    @Test
    public void testCloseOnClose() {
        User user1 = newTestUser();

        user1.closeLoginPeriod();
        user1.closeLoginPeriod();
        assertTrue(user1.isLoginExpired());
        assertEquals(1, user1.getLoginValiditySet().size());
    }

    @Test
    public void testCreatePeriod() {
        User user1 = newTestUser();

        LocalDate in30Days = LocalDate.now().plusDays(30);
        user1.createLoginPeriod(LocalDate.now(), in30Days);
        assertEquals(in30Days, user1.getExpiration().orElse(null));
    }

    @Test
    public void testClosePeriodInFuture() {
        User user1 = newTestUser();

        LocalDate in30Days = LocalDate.now().plusDays(30);
        user1.closeLoginPeriod(in30Days);
        assertEquals(in30Days, user1.getExpiration().orElse(null));
    }

    @Test
    public void testClosePeriodInPast() {
        User user1 = newTestUser();

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        user1.closeLoginPeriod(thirtyDaysAgo);
        assertEquals(thirtyDaysAgo, user1.getExpiration().orElse(null));
    }

    private User newTestUser() {
        return new User(new UserProfile("x", "y", null, "x@gmail.com", null));
    }
}
