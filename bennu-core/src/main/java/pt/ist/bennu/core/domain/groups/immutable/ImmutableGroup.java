package pt.ist.bennu.core.domain.groups.immutable;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.util.BundleUtil;

public abstract class ImmutableGroup extends ImmutableGroup_Base {
	protected ImmutableGroup() {
		super();
		setSystem(Bennu.getInstance());
	}

	@Override
	public String getName() {
		return BundleUtil.getString("BennuResources", "label.persistent.group." + getClass().getSimpleName());
	}
}
