package pt.ist.bennu.core.domain.groups.mutable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class IntersectionGroup extends IntersectionGroup_Base {
	protected IntersectionGroup() {
		super();
	}

	public IntersectionGroup(final PersistentGroup... persistentGroups) {
		this(Arrays.asList(persistentGroups));
	}

	public IntersectionGroup(final Collection<PersistentGroup> persistentGroups) {
		this();
		getPersistentGroupsSet().addAll(persistentGroups);
	}

	@Override
	public Set<User> getMembers() {
		final Set<User> users = new HashSet<>();
		Iterator<PersistentGroup> iterator = getPersistentGroupsSet().iterator();
		if (iterator.hasNext()) {
			users.addAll(iterator.next().getMembers());
			while (iterator.hasNext()) {
				users.retainAll(iterator.next().getMembers());
			}
		}
		return users;
	}

	@Override
	public String getName() {
		if (getName() != null) {
			return getName();
		}
		Iterable<String> names = Iterables.transform(getPersistentGroupsSet(), new Function<PersistentGroup, String>() {
			@Override
			public String apply(PersistentGroup group) {
				return "(" + group.getName() + ")";
			}
		});

		return BundleUtil.getString("BennuResources", "label.persistent.group." + getClass().getSimpleName(), Joiner.on(" AND ")
				.join(names));
	}

	@Override
	public boolean isMember(final User user) {
		if (getPersistentGroupsCount() == 0) {
			return false;
		}
		for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
			if (!persistentGroup.isMember(user)) {
				return false;
			}
		}
		return true;
	}

	@Service
	public static IntersectionGroup getOrCreateGroup(final PersistentGroup... persistentGroups) {
		for (PersistentGroup group : Bennu.getInstance().getGroupsSet()) {
			if (group instanceof IntersectionGroup) {
				IntersectionGroup intersectionGroup = (IntersectionGroup) group;
				if (Iterables.elementsEqual(intersectionGroup.getPersistentGroups(), Arrays.asList(persistentGroups))) {
					return intersectionGroup;
				}
			}
		}
		return new IntersectionGroup(persistentGroups);
	}
}
