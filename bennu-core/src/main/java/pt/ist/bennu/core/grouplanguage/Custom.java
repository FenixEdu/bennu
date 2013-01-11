package pt.ist.bennu.core.grouplanguage;

import java.util.List;

import pt.ist.bennu.core.domain.groups.CustomGroup;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

class Custom extends Group {
	private final String operator;

	private final List<String> args;

	public Custom(String operator, List<String> args) {
		this.operator = operator;
		this.args = args;
	}

	@Override
	public PersistentGroup group() {
		return CustomGroup.parse(operator, args.toArray(new String[0]));
	}
}
