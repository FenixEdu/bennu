package pt.ist.bennu.core.grouplanguage;

import pt.ist.bennu.core.domain.groups.NegationGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Negation extends Group {
	private final Group group;

	public Negation(Group group) {
		this.group = group;
	}

	@Override
	public PersistentGroup group() {
		return NegationGroup.getInstance(group.group());
	}
}
