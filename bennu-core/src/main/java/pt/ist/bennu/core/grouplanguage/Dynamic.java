package pt.ist.bennu.core.grouplanguage;

import pt.ist.bennu.core.domain.groups.DynamicGroup;
import pt.ist.bennu.core.domain.groups.GroupException;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Dynamic extends Group {
	private final String name;

	public Dynamic(String name) {
		this.name = name;
	}

	@Override
	public PersistentGroup group() throws GroupException {
		return DynamicGroup.getInstance(name);
	}
}
