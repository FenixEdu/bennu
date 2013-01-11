package pt.ist.bennu.core.domain.groups;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nonnull;

import org.antlr.runtime.RecognitionException;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.grouplanguage.GroupExpressionParser;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public abstract class PersistentGroup extends PersistentGroup_Base {
	protected PersistentGroup() {
		super();
		setBennu(Bennu.getInstance());
	}

	public abstract String expression();

	public abstract Set<User> getMembers();

	public abstract boolean isMember(final User user);

	public static PersistentGroup parse(String expression) {
		try {
			return GroupExpressionParser.parse(expression);
		} catch (RecognitionException | IOException e) {
			throw new DomainException(e, "BennuResources", "error.bennu.core.groups.parse");
		}
	}

	protected static <T extends PersistentGroup> T select(final @Nonnull Class<? extends T> type,
			final @Nonnull Predicate<? super T> predicate) {
		@SuppressWarnings("unchecked")
		Predicate<? super PersistentGroup> realPredicate = Predicates.and(Predicates.instanceOf(type),
				(Predicate<? super PersistentGroup>) predicate);
		return (T) Iterables.tryFind(Bennu.getInstance().getGroupsSet(), realPredicate).orNull();
	}

	protected static <T extends PersistentGroup> T selectSystemGroup(Class<? extends T> type) {
		return (T) Iterables.tryFind(Bennu.getInstance().getSystemGroupsSet(), Predicates.instanceOf(type)).orNull();
	}

}
