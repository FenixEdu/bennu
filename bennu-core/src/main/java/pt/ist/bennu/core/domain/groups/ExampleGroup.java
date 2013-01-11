package pt.ist.bennu.core.domain.groups;

import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.annotation.CustomGroupArgument;
import pt.ist.bennu.core.domain.groups.annotation.CustomGroupConstructor;
import pt.ist.bennu.core.domain.groups.annotation.CustomGroupOperator;

@CustomGroupOperator(operator = "example")
public class ExampleGroup extends CustomGroup {
	private Integer number;

	private String name;

	protected ExampleGroup() {
		super();
	}

	@CustomGroupConstructor
	protected ExampleGroup(Integer number, String name) {
		this.number = number;
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	@Override
	public Set<User> getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMember(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@CustomGroupArgument(index = 1)
	public static final Argument<Integer, ExampleGroup> numberArgument() {
		return new Argument<Integer, ExampleGroup>() {
			@Override
			public Integer parse(String argument) {
				return Integer.parseInt(argument);
			}

			@Override
			public String extract(ExampleGroup group) {
				return group.getNumber().toString();
			}
		};
	}

	@CustomGroupArgument(index = 2)
	public static final Argument<String, ExampleGroup> nameArgument() {
		return new Argument<String, ExampleGroup>() {
			@Override
			public String parse(String argument) {
				return argument;
			}

			@Override
			public String extract(ExampleGroup group) {
				return group.getName();
			}
		};
	}
}
