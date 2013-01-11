package pt.ist.bennu.core.domain.groups;

import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public abstract class CompositionGroup extends CompositionGroup_Base {
	protected CompositionGroup() {
	}

	protected void init(Set<PersistentGroup> children) {
		getChildrenSet().addAll(children);
	}

	@Override
	public String expression() {
		Iterable<String> parts = Iterables.transform(getChildrenSet(), new Function<PersistentGroup, String>() {
			@Override
			public String apply(PersistentGroup group) {
				return group.expression();
			}
		});

		return "(" + Joiner.on(" " + operator() + " ").join(parts) + ")";
	}

	@Service
	public static <T extends CompositionGroup> T getInstance(Class<? extends T> type, Set<PersistentGroup> children) {
		for (PersistentGroup group : Bennu.getInstance().getGroupsSet()) {
			if (type.isAssignableFrom(group.getClass())) {
				@SuppressWarnings("unchecked")
				T composition = (T) group;
				if (Iterables.elementsEqual(composition.getChildrenSet(), children)) {
					return composition;
				}
			}
		}
		return null;
	}

	protected abstract String operator();
}
