package pt.ist.bennu.core.grouplanguage;

import pt.ist.bennu.core.domain.groups.AnyoneGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Anyone extends Group {
	@Override
	public PersistentGroup group() {
		return AnyoneGroup.getInstance();
	}
}
