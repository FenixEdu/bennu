package pt.ist.bennu.core.grouplanguage;

import java.io.Serializable;

import pt.ist.bennu.core.domain.groups.PersistentGroup;

abstract class Group implements Serializable {
	public abstract PersistentGroup group();
}
