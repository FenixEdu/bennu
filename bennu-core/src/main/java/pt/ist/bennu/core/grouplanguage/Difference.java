package pt.ist.bennu.core.grouplanguage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ist.bennu.core.domain.groups.DifferenceGroup;
import pt.ist.bennu.core.domain.groups.GroupException;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Difference extends Group {
	private final List<Group> children;

	public Difference(List<Group> children) {
		this.children = children;
	}

	@Override
	public PersistentGroup group() throws GroupException {
		Set<PersistentGroup> groups = new HashSet<>();
		for (Group group : children) {
			groups.add(group.group());
		}
		return DifferenceGroup.getInstance(groups);
	}
}
