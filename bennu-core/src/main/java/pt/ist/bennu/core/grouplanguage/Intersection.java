package pt.ist.bennu.core.grouplanguage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ist.bennu.core.domain.groups.GroupException;
import pt.ist.bennu.core.domain.groups.IntersectionGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Intersection extends Group {
	private final List<Group> children;

	public Intersection(List<Group> children) {
		this.children = children;
	}

	@Override
	public PersistentGroup group() throws GroupException {
		Set<PersistentGroup> groups = new HashSet<>();
		for (Group group : children) {
			groups.add(group.group());
		}
		return IntersectionGroup.getInstance(groups);
	}
}
