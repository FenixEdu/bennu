package pt.ist.bennu.core.domain.groups;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public abstract class CompositionGroup extends CompositionGroup_Base {
	protected CompositionGroup() {
	}

	protected void init(Set<PersistentGroup> children) {
		getChildrenSet().addAll(children);
	}

	private class ExpressionTransformer implements Function<PersistentGroup, String> {
		@Override
		@Nullable
		public String apply(@Nullable PersistentGroup input) {
			return input.expression();
		}
	}

	@Override
	public String expression() {
		return "(" + Joiner.on(" " + operator() + " ").join(Iterables.transform(getChildrenSet(), new ExpressionTransformer()))
				+ ")";
	}

	protected abstract String operator();
}
