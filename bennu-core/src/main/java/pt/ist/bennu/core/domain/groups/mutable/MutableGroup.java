package pt.ist.bennu.core.domain.groups.mutable;

import pt.ist.bennu.core.util.BundleUtil;

public abstract class MutableGroup extends MutableGroup_Base {
	public MutableGroup() {
		super();
	}

	@Override
	public String getName() {
		return getName() != null ? getName() : BundleUtil.getString("BennuResources", "label.persistent.group."
				+ getClass().getSimpleName());
	}

	public MutableGroup rename(String name) {
		MutableGroup group = copyAndPushHistory(this);
		group.setName(name);
		return group;
	}

	@SuppressWarnings("unchecked")
	protected static <T extends MutableGroup> T copyAndPushHistory(MutableGroup from) {
		try {
			MutableGroup newGroup = from.getClass().newInstance();
			newGroup.setName(from.getName());
			newGroup.setPrevious(from);
			return (T) newGroup;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new Error(e);
		}
	}
}
