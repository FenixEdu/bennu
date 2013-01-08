package pt.ist.bennu.core.domain.groups.mutable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class UnionGroup extends UnionGroup_Base {
	public UnionGroup() {
		super();
	}

	public UnionGroup(final PersistentGroup... persistentGroups) {
		this(Arrays.asList(persistentGroups));
	}

	public UnionGroup(final Collection<PersistentGroup> persistentGroups) {
		this();
		getPersistentGroupsSet().addAll(persistentGroups);
	}

	@Override
	public Set<User> getMembers() {
		final Set<User> users = new HashSet<>();
		for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
			users.addAll(persistentGroup.getMembers());
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
		for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
			if (persistentGroup.isMember(user)) {
				return true;
			}
		}
		return false;
	}

	@Service
	public static UnionGroup getOrCreateGroup(final PersistentGroup... persistentGroups) {
		for (PersistentGroup group : Bennu.getInstance().getGroupsSet()) {
			if (group instanceof UnionGroup) {
				UnionGroup unionGroup = (UnionGroup) group;
				if (Iterables.elementsEqual(unionGroup.getPersistentGroups(), Arrays.asList(persistentGroups))) {
					return unionGroup;
				}
			}
		}
		return new UnionGroup(persistentGroups);
	}
}
