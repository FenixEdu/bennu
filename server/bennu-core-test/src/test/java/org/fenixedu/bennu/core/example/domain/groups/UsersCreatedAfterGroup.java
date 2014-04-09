package org.fenixedu.bennu.core.example.domain.groups;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class UsersCreatedAfterGroup extends UsersCreatedAfterGroup_Base {
    @GroupOperator("after")
    public static class UsersCreatedAfter extends CustomGroup {
        private static final long serialVersionUID = -5322710492297718102L;

        @GroupArgument("")
        private DateTime date;

        @GroupArgument
        private Boolean inclusive;

        @GroupArgument("things")
        private Set<String> stuff;

        @GroupArgument
        private List<String> x1;

        @GroupArgument
        private String[] x2;

        protected UsersCreatedAfter() {
        }

        public UsersCreatedAfter(DateTime date) {
            this.date = date;
        }

        @Override
        public String getPresentationName() {
            return "Users Created After " + date.toString();
        }

        @Override
        public PersistentGroup toPersistentGroup() {
            return UsersCreatedAfterGroup.getInstance(date);
        }

        @Override
        public Set<User> getMembers() {
            Set<User> users = new HashSet<>();
            for (User user : Bennu.getInstance().getUserSet()) {
                if (user.getCreated().isAfter(date)) {
                    users.add(user);
                }
            }
            return users;
        }

        @Override
        public Set<User> getMembers(DateTime when) {
            return getMembers();
        }

        @Override
        public boolean isMember(User user) {
            return user.getCreated().isAfter(date);
        }

        @Override
        public boolean isMember(User user, DateTime when) {
            return isMember(user);
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof UsersCreatedAfter) {
                return date.equals(((UsersCreatedAfter) object).date);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return date.hashCode();
        }
    }

    protected UsersCreatedAfterGroup(DateTime date) {
        super();
        setDate(date);
    }

    @Override
    public Group toGroup() {
        return new UsersCreatedAfter(getDate());
    }

    @Atomic(mode = TxMode.WRITE)
    public static PersistentGroup getInstance(DateTime date) {
        return new UsersCreatedAfterGroup(date);
    }
}
