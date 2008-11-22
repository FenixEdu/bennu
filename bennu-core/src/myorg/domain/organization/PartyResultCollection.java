/**
 * 
 */
package myorg.domain.organization;

import java.util.Collection;
import java.util.LinkedList;

public class PartyResultCollection {
    private PartyPredicate predicate;
    private Collection<Party> result;

    PartyResultCollection(final PartyPredicate predicate) {
	this(new LinkedList<Party>(), predicate);
    }

    PartyResultCollection(final Collection<Party> result, final PartyPredicate predicate) {
	this.predicate = predicate;
	this.result = result;
    }

    public boolean conditionalAddParty(final Party party, final Accountability accountability) {
	return predicate.eval(party, accountability) && result.add(party);
    }

    @SuppressWarnings("unchecked")
    public <T extends Party> Collection<T> getResult() {
	return (Collection<T>) result;
    }
}
