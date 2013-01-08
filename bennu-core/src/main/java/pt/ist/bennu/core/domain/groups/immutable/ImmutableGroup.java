package pt.ist.bennu.core.domain.groups.immutable;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

public abstract class ImmutableGroup extends ImmutableGroup_Base {
	protected ImmutableGroup() {
		super();
		setSystem(Bennu.getInstance());
	}

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
