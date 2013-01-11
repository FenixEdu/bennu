package pt.ist.bennu.core.grouplanguage;

import pt.ist.bennu.core.domain.groups.AnonymousGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Anonymous extends Group {
	@Override
	public PersistentGroup group() {
		return AnonymousGroup.getInstance();
	}
}
