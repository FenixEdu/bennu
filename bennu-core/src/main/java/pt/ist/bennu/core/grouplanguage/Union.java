package pt.ist.bennu.core.grouplanguage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.domain.groups.UnionGroup;

class Union extends Group {
	private final List<Group> children;

	public Union(List<Group> children) {
		this.children = children;
	}

	@Override
	public PersistentGroup group() {
		Set<PersistentGroup> groups = new HashSet<>();
		for (Group group : children) {
			groups.add(group.group());
		}
		return UnionGroup.getInstance(groups);
	}
}
