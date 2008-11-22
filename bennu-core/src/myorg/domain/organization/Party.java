package myorg.domain.organization;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import myorg.domain.MyOrg;
import myorg.domain.organization.PartyPredicate.PartyByAccountabilityType;
import myorg.domain.organization.PartyPredicate.PartyByClassType;
import myorg.domain.organization.PartyPredicate.PartyByPartyType;
import myorg.domain.organization.PartyPredicate.TruePartyPredicate;

abstract public class Party extends Party_Base {

    protected Party() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public Collection<Party> getParents() {
	return getParents(new TruePartyPredicate());
    }

    public Collection<Party> getParents(final AccountabilityType type) {
	return getParents(new PartyByAccountabilityType(type));
    }

    public Collection<Party> getParents(final PartyType type) {
	return getParents(new PartyByPartyType(type));
    }

    public Collection<Unit> getParentUnits() {
	return getParents(new PartyByClassType(Unit.class));
    }

    public Collection<Unit> getParentUnits(final AccountabilityType type) {
	return getParents(new PartyByAccountabilityType(Unit.class, type));
    }

    public Collection<Unit> getParentUnits(final PartyType type) {
	return getParents(new PartyByPartyType(Unit.class, type));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Party> Collection<T> getParents(final PartyPredicate predicate) {
	final Collection<Party> result = new LinkedList<Party>();
	for (final Accountability accountability : getParentAccountabilities()) {
	    if (predicate.eval(accountability.getParent(), accountability)) {
		result.add(accountability.getParent());
	    }
	}
	return (List<T>) result;
    }

    public Collection<Party> getChildren() {
	return getChildren(new TruePartyPredicate());
    }

    public Collection<Party> getChildren(final AccountabilityType type) {
	return getChildren(new PartyByAccountabilityType(type));
    }

    public Collection<Party> getChildren(final PartyType type) {
	return getChildren(new PartyByPartyType(type));
    }

    public Collection<Unit> getChildUnits() {
	return getChildren(new PartyByClassType(Unit.class));
    }

    public Collection<Unit> getChildUnits(final AccountabilityType type) {
	return getChildren(new PartyByAccountabilityType(Unit.class, type));
    }

    public Collection<Unit> getChildUnits(final PartyType type) {
	return getChildren(new PartyByPartyType(Unit.class, type));
    }

    public Collection<Person> getChildPersons() {
	return getChildren(new PartyByClassType(Person.class));
    }

    public Collection<Person> getChildPersons(final AccountabilityType type) {
	return getChildren(new PartyByAccountabilityType(Person.class, type));
    }

    public Collection<Person> getChildPersons(final PartyType type) {
	return getChildren(new PartyByPartyType(Person.class, type));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Party> Collection<T> getChildren(final PartyPredicate predicate) {
	final Collection<Party> result = new LinkedList<Party>();
	for (final Accountability accountability : getChildAccountabilities()) {
	    if (predicate.eval(accountability.getChild(), accountability)) {
		result.add(accountability.getChild());
	    }
	}
	return (List<T>) result;
    }

    public Collection<Party> getAncestors() {
	final PartyResultCollection result = new PartyResultCollection(new TruePartyPredicate());
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Party> getAncestors(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(type));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Party> getAncestors(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(type));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Unit> getAncestorUnits() {
	final PartyResultCollection result = new PartyResultCollection(new PartyByClassType(Unit.class));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Unit> getAncestorUnits(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(Unit.class, type));
	getAncestors(result);
	return result.getResult();
    }

    public Collection<Unit> getAncestorUnits(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(Unit.class, type));
	getAncestors(result);
	return result.getResult();
    }

    protected void getAncestors(final PartyResultCollection result) {
	for (final Accountability accountability : getParentAccountabilities()) {
	    result.conditionalAddParty(accountability.getParent(), accountability);
	    accountability.getParent().getAncestors(result);
	}
    }

    public boolean ancestorsInclude(final Party party, final AccountabilityType type) {
	for (final Accountability accountability : getParentAccountabilities()) {
	    if (accountability.hasAccountabilityType(type)) {
		if (accountability.getParent().equals(party)) {
		    return true;
		}
		if (accountability.getParent().ancestorsInclude(party, type)) {
		    return true;
		}
	    }
	}

	return false;
    }

    public Collection<Party> getDescendents() {
	final PartyResultCollection result = new PartyResultCollection(new TruePartyPredicate());
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Party> getDescendents(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Party> getDescendents(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Unit> getDescendentUnits() {
	final PartyResultCollection result = new PartyResultCollection(new PartyByClassType(Unit.class));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Unit> getDescendentUnits(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(Unit.class, type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Unit> getDescendentUnits(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(Unit.class, type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Person> getDescendentPersons() {
	final PartyResultCollection result = new PartyResultCollection(new PartyByClassType(Person.class));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Person> getDescendentPersons(final AccountabilityType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByAccountabilityType(Person.class, type));
	getDescendents(result);
	return result.getResult();
    }

    public Collection<Person> getDescendentPersons(final PartyType type) {
	final PartyResultCollection result = new PartyResultCollection(new PartyByPartyType(Person.class, type));
	getDescendents(result);
	return result.getResult();
    }

    protected void getDescendents(final PartyResultCollection result) {
	for (final Accountability accountability : getChildAccountabilities()) {
	    result.conditionalAddParty(accountability.getChild(), accountability);
	    accountability.getChild().getDescendents(result);
	}
    }

    public Collection<Party> getSiblings() {
	final Collection<Party> result = new LinkedList<Party>();
	for (final Accountability accountability : getParentAccountabilities()) {
	    result.addAll(accountability.getParent().getChildren());
	}
	result.remove(this);
	return result;
    }

    public boolean isUnit() {
	return false;
    }

}
