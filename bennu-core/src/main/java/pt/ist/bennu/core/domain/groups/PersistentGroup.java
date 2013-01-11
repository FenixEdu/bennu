package pt.ist.bennu.core.domain.groups;

import java.io.IOException;
import java.util.Set;

import org.antlr.runtime.RecognitionException;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.grouplanguage.GroupExpressionParser;

public abstract class PersistentGroup extends PersistentGroup_Base {
	protected PersistentGroup() {
		super();
		setBennu(Bennu.getInstance());
	}

	public abstract String expression();

	public abstract Set<User> getMembers();

	public abstract boolean isMember(final User user);

	@SuppressWarnings("unchecked")
	protected static <T extends PersistentGroup> T getSystemGroup(Class<? extends T> clazz) {
		for (final PersistentGroup group : Bennu.getInstance().getSystemGroupsSet()) {
			if (group.getClass().isAssignableFrom(clazz)) {
				return (T) group;
			}
		}
		return null;
	}

	public static PersistentGroup parse(String expression) {
		try {
			return GroupExpressionParser.parse(expression);
		} catch (RecognitionException | IOException e) {
			throw new DomainException(e, "BennuResources", "error.bennu.core.groups.parse");
		}
	}
}
