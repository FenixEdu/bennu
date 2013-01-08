package pt.ist.bennu.core.domain.groups;

import java.util.Comparator;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.security.UserView;

public abstract class PersistentGroup extends PersistentGroup_Base {

	public static final Comparator<PersistentGroup> COMPARATOR_BY_NAME = new Comparator<PersistentGroup>() {

		@Override
		public int compare(PersistentGroup r1, PersistentGroup r2) {
			final int c = r1.getName().compareTo(r2.getName());
			return c == 0 ? r1.getExternalId().compareTo(r2.getExternalId()) : c;
		}

	};

	public PersistentGroup() {
		super();
		setBennu(Bennu.getInstance());
		setCreated(new DateTime());
		setCreator(UserView.getUser());
	}

	public abstract boolean isMember(final User user);

	public abstract String getName();

	public abstract Set<User> getMembers();

	@SuppressWarnings("unchecked")
	protected static <T extends PersistentGroup> T getSystemGroup(Class<? extends T> clazz) {
		for (final PersistentGroup group : Bennu.getInstance().getSystemGroupsSet()) {
			if (group.getClass().isAssignableFrom(clazz)) {
				return (T) group;
			}
		}
		return null;
	}
}
