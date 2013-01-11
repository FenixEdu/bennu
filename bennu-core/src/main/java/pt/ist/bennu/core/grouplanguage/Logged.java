package pt.ist.bennu.core.grouplanguage;

import pt.ist.bennu.core.domain.groups.LoggedGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Logged extends Group {
	@Override
	public PersistentGroup group() {
		return LoggedGroup.getInstance();
	}
}
