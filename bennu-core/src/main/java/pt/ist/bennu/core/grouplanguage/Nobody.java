package pt.ist.bennu.core.grouplanguage;

import pt.ist.bennu.core.domain.groups.NobodyGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Nobody extends Group {
	@Override
	public PersistentGroup group() {
		return NobodyGroup.getInstance();
	}
}
