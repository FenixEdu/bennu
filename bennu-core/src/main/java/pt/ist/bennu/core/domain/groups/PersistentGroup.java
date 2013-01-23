package pt.ist.bennu.core.domain.groups;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.antlr.runtime.RecognitionException;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.grouplanguage.GroupExpressionParser;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public abstract class PersistentGroup extends PersistentGroup_Base {
	protected PersistentGroup() {
		super();
		setHost(VirtualHost.getVirtualHostForThread());
	}

	public abstract String expression();

	public abstract Set<User> getMembers();

	public abstract boolean isMember(User user);

	public abstract Set<User> getMembers(DateTime when);

	public abstract boolean isMember(User user, DateTime when);

	public PersistentGroup and(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>();
		children.add(this);
		children.add(group);
		return IntersectionGroup.getInstance(children);
	}

	public PersistentGroup or(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>();
		children.add(this);
		children.add(group);
		return UnionGroup.getInstance(children);
	}

	public PersistentGroup minus(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>();
		children.add(this);
		children.add(group);
		return DifferenceGroup.getInstance(children);
	}

	public PersistentGroup not() {
		return NegationGroup.getInstance(this);
	}

	public PersistentGroup grant(User user) {
		return UnionGroup.getInstance(this, UserGroup.getInstance(user));
	}

	public PersistentGroup revoke(User user) {
		return DifferenceGroup.getInstance(this, UserGroup.getInstance(user));
	}

	public static PersistentGroup parse(String expression) {
		try {
			return GroupExpressionParser.parse(expression);
		} catch (RecognitionException | IOException | GroupException e) {
			throw new DomainException(e, "resources.BennuResources", "error.bennu.core.groups.parse");
		}
	}

	protected static <T extends PersistentGroup> T select(final @Nonnull Class<? extends T> type) {
		return (T) Iterables.tryFind(VirtualHost.getVirtualHostForThread().getGroupsSet(), Predicates.instanceOf(type)).orNull();
	}

	protected static <T extends PersistentGroup> T select(final @Nonnull Class<? extends T> type,
			final @Nonnull Predicate<? super T> predicate) {
		@SuppressWarnings("unchecked")
		Predicate<? super PersistentGroup> realPredicate = Predicates.and(Predicates.instanceOf(type),
				(Predicate<? super PersistentGroup>) predicate);
		return (T) Iterables.tryFind(VirtualHost.getVirtualHostForThread().getGroupsSet(), realPredicate).orNull();
	}
}
