package myorg.domain.organization;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public class Accountability extends Accountability_Base {

    protected Accountability() {
	super();
	setMyOrg(MyOrg.getInstance());
	setBeginDate(new LocalDate());
    }

    protected Accountability(final Party parent, final Party child, final AccountabilityType type) {
	check(parent, "error.Accountability.invalid.parent");
	check(child, "error.Accountability.invalid.child");
	check(type, "error.Accountability.invalid.type");
	canCreate(parent, child, type);
	setParent(parent);
	setChild(child);
	setAccountabilityType(type);
    }

    private void check(final Object obj, final String message) {
	if (obj == null) {
	    throw new DomainException(message);
	}
    }

    protected void canCreate(final Party parent, final Party child, final AccountabilityType type) {
	if (parent.equals(child)) {
	    throw new DomainException("error.Accountability.parent.equals.child");
	}
	if (parent.ancestorsInclude(child, type)) {
	    throw new DomainException("error.Accountability.parent.ancestors.include.child.with.type");
	}
	if (!type.canCreateAccountability(parent, child)) {
	    throw new DomainException("error.Accountability.type.doesnot.allow.parent.child");
	}
    }

    @jvstm.cps.ConsistencyPredicate
    protected boolean checkDateInterval() {
	return hasBeginDate() && (!hasEndDate() || !getBeginDate().isAfter(getEndDate()));
    }

    public boolean isActive(final LocalDate date) {
	return contains(date);
    }

    public boolean contains(final LocalDate date) {
	return !getBeginDate().isAfter(date) && (!hasEndDate() || !getEndDate().isAfter(date));
    }

    public boolean contains(final LocalDate begin, final LocalDate end) {
	check(begin, "error.Accountability.intercepts.invalid.begin");
	return (end == null || !getBeginDate().isAfter(end)) && (!hasEndDate() || !begin.isAfter(getEndDate()));
    }

    private boolean hasBeginDate() {
	return getBeginDate() != null;
    }

    private boolean hasEndDate() {
	return getEndDate() != null;
    }

    public boolean hasAccountabilityType(AccountabilityType type) {
	return getAccountabilityType().equals(type);
    }

    @Service
    public void delete() {
	removeParent();
	removeChild();
	removeAccountabilityType();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    static public Accountability create(final Party parent, final Party child, final AccountabilityType type) {
	return new Accountability(parent, child, type);
    }

}
